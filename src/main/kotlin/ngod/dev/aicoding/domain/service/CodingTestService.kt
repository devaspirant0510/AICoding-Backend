package ngod.dev.aicoding.domain.service

import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodingTest
import ngod.dev.aicoding.data.entity.Quiz
import ngod.dev.aicoding.data.entity.enum.StudyType
import ngod.dev.aicoding.data.repository.BaseContentRepository
import ngod.dev.aicoding.data.repository.CodingTestRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CodingTestService(
    private val accountService: AccountService,
    private val baseContentRepository: BaseContentRepository,
    private val gptService: GptService,
    private val codingTestRepository: CodingTestRepository
) {

    @Transactional
    fun updateContentStudyName(content: BaseContent, studyName: String, codingTest:CodingTest) {
        content.studyName = studyName
        content.codingTest = codingTest
    }

    @Transactional
    fun createQuiz(requestContentDto: RequestContentDto, accountId: Long): BaseContent {
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
        updateContentStudyName(content, codingTest.title,codingTest)
        return findCodingTestByContent(content.id!!)
    }
    fun findCodingTestByContent(contentId:Long):BaseContent{
        val result = baseContentRepository.findById(contentId).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(), "content 가 존재 하지 않아요")
        }
        return result
    }
}