package ngod.dev.aicoding.domain.service

import ngod.dev.aicoding.controller.quiz.dto.QuizResult
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.Quiz
import ngod.dev.aicoding.data.entity.enum.StudyType
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import ngod.dev.aicoding.data.projectrion.QuizIdProjection
import ngod.dev.aicoding.data.projectrion.QuizProjection
import ngod.dev.aicoding.data.repository.BaseContentRepository
import ngod.dev.aicoding.data.repository.QuizRepository
import ngod.dev.aicoding.domain.model.RequestGptQuizResult
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizService(
    private val accountService: AccountService,
    private val quizRepository: QuizRepository,
    private val baseContentRepository: BaseContentRepository,
    private val gptService: GptService
) {
    @Transactional
    fun updateContentStudyName(content: BaseContent, studyName: String, quizList: MutableList<Quiz>) {
        content.studyName = studyName
        content.quiz = quizList
    }
    fun createFeedback(quizResult:List<QuizResult>):String{
        val requests = mutableListOf<RequestGptQuizResult>()
        quizResult.map {
            val quiz = quizRepository.findById(it.quizId).orElseThrow{
                throw ApiException(HttpStatus.NOT_FOUND.value(), "퀴즈를 찾을 수 없습니다.") }
            val dto = RequestGptQuizResult(
                quiz = quiz,
                result = it.label
            )
            requests.add(dto)
        }
        return gptService.feedBackQuiz(requests)
    }

    @Transactional
    fun createQuiz(requestContentDto: RequestContentDto, accountId: Long): BaseContent{
        val account = accountService.getAccountById(accountId)
        val content = baseContentRepository.save(
            BaseContent(account = account, category = requestContentDto.category,
                difficultly = requestContentDto.difficultly, studyType = StudyType.QUIZ)
        )
        val quizResponse = gptService.generateQuiz(requestContentDto)
        println(quizResponse)
        val quizList = quizResponse.map {
            Quiz(question = it.question, answer = it.answer.toShort(), choices = it.choices,
                explanation = it.explanation,)
        }
        quizRepository.saveAll(quizList)
        updateContentStudyName(
            content,
            quizResponse.first().studyName,
            quizList.toMutableList())
        return findAllQuizByContent(content.id!!)
    }

    fun findQuizProjectionId(id:Long):QuizProjection{
        val quiz = quizRepository.findQuizById(id).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(),"찾을수 없는 Quiz 데이터입니다.")
        }
        return quiz
    }
    fun findQuizId(id:Long):Quiz{
        val quiz = quizRepository.findById(id).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(),"찾을수 없는 Quiz 데이터입니다.")
        }
        return quiz
    }
    fun getQuizExplanationByQuiz(id:Long):String{
        return findQuizId(id).explanation

    }

    fun findAllQuizByContent(contentId: Long): BaseContent {
        val result = baseContentRepository.findById(contentId).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(), "content 가 존재 하지 않아요")
        }
        return result
    }

    fun checkedQuizAnswer(quizId:Long,answer:Short):Boolean{
        val quiz = findQuizId(quizId)
        return quiz.answer==answer
    }

    fun findAllQuizByIds(ids:List<Long>):List<QuizProjection>{
        val quizList = mutableListOf<QuizProjection>()
        ids.forEach {
            val quiz = findQuizProjectionId(it)
            quizList.add(quiz)
        }
        return quizList
    }
    fun findAllQuizAll(token:String):List<ContentQuizProjection>{
        val accountId = accountService.getUserByToken(token).id
        return baseContentRepository.findBaseContentAllByAccountIdAndStudyTypeOrderByCreatedAtDesc(accountId,StudyType.QUIZ)
    }

    fun generateQuizWithGpt() {

    }
}