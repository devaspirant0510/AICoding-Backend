package ngod.dev.aicoding.domain.service

import lombok.extern.slf4j.Slf4j
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.JwtProvider
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodingTest
import ngod.dev.aicoding.data.entity.enum.StudyType
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.repository.BaseContentRepository
import ngod.dev.aicoding.data.repository.CodingTestRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.lang.Exception

@Service
@Slf4j
class CodingTestService(
    private val accountService: AccountService,
    private val baseContentRepository: BaseContentRepository,
    private val gptService: GptService,
    private val codingTestRepository: CodingTestRepository,
    private val jwtProvider: JwtProvider
) {
    @Value("\${COMPILED_BUILD_PATH}")
    private lateinit var COMPIELD_BUILD_PATH: String
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun updateContentStudyName(content: BaseContent, studyName: String, codingTest: CodingTest) {
        content.studyName = studyName
        content.codingTest = codingTest
    }

    @Transactional
    fun createCodingTest(requestContentDto: RequestContentDto, token: String): BaseContent {
        val accountId = accountService.getUserByToken(token).id
        val account = accountService.getAccountById(accountId)
        val content = baseContentRepository.save(
            BaseContent(
                account = account, category = requestContentDto.category,
                difficultly = requestContentDto.difficultly, studyType = StudyType.CODING_TEST
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
        return baseContentRepository.findAllByAccountIdAndStudyTypeOrderByCreatedAtDesc(
            accountId,
            StudyType.CODING_TEST
        )
    }


    fun evaluateCode(id: Long, code: String, language: String): Float {
        try {
            // 1. 코딩 테스트 및 언어 설정 추출
            val (codingTest, languageConfig) = extractTestAndLanguageConfig(id, language)
            val inputText = codingTest.inputTestCase
            val outputText = codingTest.outputTestCase
            val codeDir = File(COMPIELD_BUILD_PATH)

            // 2. 코드 파일 생성
            val userCode = createCodeFile(codeDir, code, languageConfig)

            // 3. 컴파일 (필요한 경우)
            compileCodeIfNeeded(userCode, language, codeDir)

            // 4. 테스트 케이스 실행 및 채점
            return evaluateTestCases(userCode, language, languageConfig, inputText, outputText, codeDir)
        } catch (e: Exception) {
            println(e.message)
            println(e.toString())
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "${e.message}")
        }
    }

    // 언어 설정 및 코딩 테스트 추출
    private fun extractTestAndLanguageConfig(id: Long, language: String): Pair<CodingTest, Pair<String, String>> {
        val languageDetails = mapOf(
            "Python" to Pair("py", "python"),
            "Java" to Pair("java", "java"),
            "GO" to Pair("go", "go"),
            "C" to Pair("c", "gcc"),
            "C++" to Pair("cpp", "g++")
        )

        val codingTest = findCodingTestById(id)
        val languageConfig = languageDetails[language]
            ?: throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "지원하지 않는 언어입니다.")

        return Pair(codingTest, languageConfig)
    }

    // 코드 파일 생성
    private fun createCodeFile(codeDir: File, code: String, languageConfig: Pair<String, String>): File {
        val (fileExtend, _) = languageConfig
        val userCode = File(codeDir, "test.$fileExtend")
        userCode.writeText(code)
        return userCode
    }

    // 컴파일 처리
    private fun compileCodeIfNeeded(userCode: File, language: String, codeDir: File) {
        val compileProcess = when (language) {
            "C" -> ProcessBuilder("gcc", userCode.absolutePath, "-o", "${codeDir}/test.out")
            "C++" -> ProcessBuilder("g++", userCode.absolutePath, "-o", "${codeDir}/test.out")
            "GO" -> ProcessBuilder("go", "build", "-o", "${codeDir}/test.out", userCode.absolutePath)
            else -> return
        }

        compileProcess.redirectErrorStream(true).start().let { process ->
            val compileExitCode = process.waitFor()
            if (compileExitCode != 0) {
                throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "$language 컴파일 오류 발생")
            }
            println("컴파일 성공: ${codeDir}/test.out")
        }
    }
//    private fun compileCodeIfNeeded(userCode: File, language: String, codeDir: File) {
//        val dockerImage = when (language) {
//            "C", "C++" -> "gcc:latest"
//            "GO" -> "golang:latest"
//            else -> throw ApiException(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 언어입니다.")
//        }
//        val compileProcess = ProcessBuilder(
//            "docker", "run", "--rm",
//            "-v", "${codeDir.absolutePath}:/app",
//            "-w", "/app",
//            dockerImage,
//            *getCompileCommand(language, userCode.name).toTypedArray()
//        )
//        compileProcess.redirectErrorStream(true).start().let { process ->
//            val compileExitCode = process.waitFor()
//            if (compileExitCode != 0)
//                throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "$language 컴파일 오류 발생")
//            log.error("컴파일 성공: ${codeDir}/test.out")
//        }
//    }

    // 언어별 컴파일 명령어 반환
    private fun getCompileCommand(language: String, userCodeFileName: String): List<String> {
        return when (language) {
            "C" -> listOf("gcc", userCodeFileName, "-o", "test.out")
            "C++" -> listOf("g++", userCodeFileName, "-o", "test.out")
            "GO" -> listOf("go", "build", "-o", "test.out", userCodeFileName)
            else -> throw ApiException(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 언어입니다.")
        }
    }

    // 테스트 케이스 평가
    private fun evaluateTestCases(
        userCode: File, language: String, languageConfig: Pair<String, String>,
        inputText: List<String>?, outputText: List<String>?, codeDir: File
    ): Float {
        var count = 0
        val (_, command) = languageConfig
        outputText?.forEachIndexed { index, expectedOutput ->
            val processBuilder = when (language) {
                "C", "C++", "GO" -> ProcessBuilder("${codeDir}/test.out")
                else -> ProcessBuilder(command, userCode.absolutePath)
            }
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            process.outputStream.bufferedWriter().use {
                it.write(inputText?.get(index).toString())
            }
            process.outputStream.close()
            val output = process.inputStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()
            if (exitCode != 0)
                throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "프로그램 실행 오류: $output")
            log.info("Input ${inputText?.get(index)}")
            log.info("Output: $output")
            log.info("Expected: $expectedOutput")
            // 정답 체크
            if (output == expectedOutput) {
                count++
                log.info("정답")
            } else {
                log.info("정답")
            }
        }
        // 점수 계산
        return if (count == inputText?.size) {
            100F
        } else {
            val correctPercentage = count.toDouble() / (inputText?.size ?: 1).toDouble() * 100
            correctPercentage.toFloat()
        }
    }


}