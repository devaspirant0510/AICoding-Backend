package ngod.dev.aicoding.domain.service

import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.JwtProvider
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodingTest
import ngod.dev.aicoding.data.entity.enum.StudyType
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.repository.BaseContentRepository
import ngod.dev.aicoding.data.repository.CodingTestRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.lang.Exception

@Service
class CodingTestService(
    private val accountService: AccountService,
    private val baseContentRepository: BaseContentRepository,
    private val gptService: GptService,
    private val codingTestRepository: CodingTestRepository,
    private val jwtProvider: JwtProvider
) {

    @Transactional
    fun updateContentStudyName(content: BaseContent, studyName: String, codingTest: CodingTest) {
        content.studyName = studyName
        content.codingTest = codingTest
    }

    @Transactional
    fun createCodingTest(requestContentDto: RequestContentDto, token: String): BaseContent {
        val verifyToken = jwtProvider.verifyToken(token)
        val accountId = (verifyToken.get("id") as Number?)?.toLong() ?: throw ApiException(
            HttpStatus.UNAUTHORIZED.value(),
            "유저 인증 실패"
        )
        val account = accountService.getAccountById(accountId)
        val content = baseContentRepository.save(
            BaseContent(
                account = account,
                category = requestContentDto.category,
                difficultly = requestContentDto.difficultly,
                studyType = StudyType.CODING_TEST
            )
        )
        val quizResponse = gptService.generateCodingTest(requestContentDto)
        val codingTest = CodingTest(
            title = quizResponse.title,
            description = quizResponse.description,
            hint = quizResponse.hint,
            input = quizResponse.input,
            output = quizResponse.output,
            inputTestCase = quizResponse.inputTestCase.toMutableList(),
            outputTestCase = quizResponse.outputTestCase.toMutableList(),
        )
        println(quizResponse)
        codingTestRepository.save(codingTest)
        updateContentStudyName(content, codingTest.title, codingTest)
        return findCodingTestByContent(content.id!!)
    }

    fun findCodingTestByContent(contentId: Long): BaseContent {
        val result = baseContentRepository.findById(contentId).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(), "content 가 존재 하지 않아요")
        }
        return result
    }

    fun findCodingTestById(id: Long): CodingTest {
        val result = baseContentRepository.findById(id).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(), "coding Test 가 존재하지 않아요")
        }
        if (result.codingTest == null) {
            throw ApiException(HttpStatus.NOT_FOUND.value(), "데이터를 불러오는중 오류가 발생했습니다.")
        }
        return result.codingTest!!
    }

    fun findAllCodingTestByUserId(token: String): List<ContentProjection> {
        val user = jwtProvider.verifyToken(token)
        val accountId = (user.get("id") as? Number)?.toLong()
            ?: throw ApiException(HttpStatus.UNAUTHORIZED.value(), "유저정보를 불러오는데 실패 했습니다.");
        return baseContentRepository.findAllByAccountId(accountId)
    }

    fun evaluateCode(id: Long, code: String, language: String): Float {
        try {
            val codingTest = findCodingTestById(id)
            val inputText = codingTest.inputTestCase
            val outputText = codingTest.outputTestCase
            val codeDir = File("/Users/kotlinandnode/aicoding")

            // 언어별 확장자와 명령어 맵 설정
            val languageDetails = mapOf(
                "Python" to Pair("py", "python"),
                "Java" to Pair("java", "java"),
                "GO" to Pair("go", "go"),
                "C" to Pair("c", "gcc"),
                "C++" to Pair("cpp", "g++")
            )

            // 언어에 맞는 확장자와 실행 명령어 가져오기
            val (fileExtend, command) = languageDetails[language]
                ?: throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "지원하지 않는 언어입니다.")

            // 코드 파일 작성
            val userCode = File(codeDir, "test.$fileExtend")
            userCode.writeText(code)

            var count = 0

            // C, C++의 경우 컴파일을 먼저 해야함
            if (language == "C" || language == "C++") {
                val compileCommand = if (language == "C") "gcc" else "g++"
                val compileProcess = ProcessBuilder(compileCommand, userCode.absolutePath, "-o", "${codeDir}/test.out")
                    .redirectErrorStream(true)
                    .start()
                val compileExitCode = compileProcess.waitFor()
                if (compileExitCode != 0) {
                    throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "컴파일 오류 발생")
                }
                println("컴파일 성공: ${codeDir}/test.out")
            }

            // Go의 경우는 go build로 먼저 바이너리 파일을 만들어야 함
            if (language == "GO") {
                val compileProcess = ProcessBuilder("go", "build", "-o", "${codeDir}/test.out", userCode.absolutePath)
                    .redirectErrorStream(true)
                    .start()
                val compileExitCode = compileProcess.waitFor()
                if (compileExitCode != 0) {
                    throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Go 컴파일 오류 발생")
                }
                println("Go 컴파일 성공: ${codeDir}/test.out")
            }
            print(outputText?.size)
            print(inputText?.size)

            // 테스트 케이스 실행
            outputText?.forEachIndexed { index, expectedOutput ->
                try {
                    val processBuilder = when (language) {
                        "C", "C++", "GO" -> {
                            // C, C++, GO일 경우는 컴파일된 실행 파일을 실행
                            ProcessBuilder("${codeDir}/test.out")
                        }

                        else -> {
                            // Python, Java일 경우는 해당 명령어로 바로 실행
                            ProcessBuilder(command, userCode.absolutePath)
                        }
                    }

                    processBuilder.redirectErrorStream(true) // 에러 스트림을 표준 출력과 합침
                    val process = processBuilder.start()

                    // 입력 전달
                    process.outputStream.bufferedWriter().use { it.write(inputText?.get(index).toString()) }
                    process.outputStream.close()

                    // 실행 결과 읽기 (표준 출력과 표준 오류 출력)
                    val output = process.inputStream.bufferedReader().readText().trim()

                    // 프로세스 종료 상태 코드 확인 (0은 정상 종료, 그 외는 오류)
                    val exitCode = process.waitFor()

                    if (exitCode != 0) {
                        // 오류 발생 시 예외 처리
                        throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "프로그램 실행 오류: $output")
                    }
                    println("Input ${inputText?.get(index)}")

                    println("Output: $output")
                    println("Expected: $expectedOutput")

                    if (output == expectedOutput) {
                        count++
                        println("정답")
                    } else {
                        println("오답")
                    }
                } catch (e: Exception) {
                    println("오류 발생: ${e.message}")
                    throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.toString())
                }
            }

            // 결과 반환
            return if (count == inputText?.size) {
                100F
            } else {
                val correctPercentage = count.toDouble() / (inputText?.size ?: 1).toDouble() * 100
                correctPercentage.toFloat()
            }
        } catch (e: Exception) {
            println(e.message)
            println(e.toString())
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "${e.message}")
        }
    }


}