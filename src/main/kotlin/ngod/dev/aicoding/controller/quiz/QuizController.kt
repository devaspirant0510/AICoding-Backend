package ngod.dev.aicoding.controller.quiz

import ngod.dev.aicoding.controller.quiz.dto.CheckedQuizDto
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import ngod.dev.aicoding.data.projectrion.QuizProjection
import ngod.dev.aicoding.domain.service.AccountService
import ngod.dev.aicoding.domain.service.QuizService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/quiz")
class QuizController(
    private val quizService: QuizService,
    private val accountService: AccountService
) : QuizSwagger {
    @PostMapping
    override fun createQuiz(
        @RequestHeader("Authorization") token: String,
        @RequestBody requestContentDto: RequestContentDto
    ): ApiResult<BaseContent> {
        val account = accountService.getUserByToken(token)
        return ApiResult.success(
            quizService.createQuiz(requestContentDto, account.id),
            HttpStatus.CREATED.value(),
            "퀴즈 생성 완료"
        )
    }

    @GetMapping("/{id}/a")
    override fun findQuizByContentId(
        @RequestHeader("Authorization")
        token: String,
        @PathVariable(value = "id")
        contentId: Long
    ): ApiResult<BaseContent> {
        return ApiResult.success(quizService.findAllQuizByContent(contentId), HttpStatus.OK.value(), "퀴즈 조회 완료")
    }

    @GetMapping("/{id}")
    override fun findQuizById(
        @RequestHeader("Authorization")
        token: String,
        @PathVariable(value = "id")
        id: Long
    ): ApiResult<QuizProjection> {
        return ApiResult.success(quizService.findQuizProjectionId(id), HttpStatus.OK.value(), "퀴즈 조회 완료")
    }

    @GetMapping("/getAll")
    override fun findAllQuizByIds(
        @RequestHeader("Authorization")
        token: String,
        @RequestParam(name = "id")
        id: List<Long>
    ): ApiResult<List<QuizProjection>> {
        return ApiResult.success(quizService.findAllQuizByIds(id), HttpStatus.OK.value(), "퀴즈 조회 완료")
    }

    @PostMapping("/{id}/explanation")
    override fun showExplanationByQuiz(
        @RequestHeader("Authorization")
        token: String,
        @PathVariable("id")
        id: Long
    ): ApiResult<String> {
        return ApiResult.success(quizService.getQuizExplanationByQuiz(id), HttpStatus.OK.value(), "Quiz Hint 불러오기 성공")
    }

    @PostMapping("/{id}/checkAnswer")
    override fun checkedQuizAnswer(
        @RequestHeader("Authorization")
        token: String,
        @PathVariable("id")
        id: Long,
        @RequestBody
        myAnswer: CheckedQuizDto
    ): ApiResult<Boolean> {
        val isCorrect = quizService.checkedQuizAnswer(id, myAnswer.myAnswer)
        return if (isCorrect) {
            ApiResult.success(isCorrect, HttpStatus.OK.value(), "Quiz 정답체크 : 정답")
        } else {
            ApiResult.success(isCorrect, HttpStatus.OK.value(), "Quiz 정답체크 : 오답")
        }
    }

    override fun getAllQuiz(token: String): ApiResult<List<ContentQuizProjection>> {
        TODO("Not yet implemented")
    }

}