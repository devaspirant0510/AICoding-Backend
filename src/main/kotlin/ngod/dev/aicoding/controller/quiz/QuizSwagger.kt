package ngod.dev.aicoding.controller.quiz

import ngod.dev.aicoding.controller.quiz.dto.CheckedQuizDto
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.Quiz
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import ngod.dev.aicoding.data.projectrion.QuizProjection

interface QuizSwagger {
    fun createQuiz(token: String, requestContentDto: RequestContentDto): ApiResult<BaseContent>
    fun findQuizByContentId(token: String, contentId: Long): ApiResult<BaseContent>
    fun findQuizById(token:String,id:Long):ApiResult<QuizProjection>
    fun findAllQuizByIds(token:String,id:List<Long>):ApiResult<List<QuizProjection>>
    fun showExplanationByQuiz(token:String,id:Long):ApiResult<String>
    fun checkedQuizAnswer(token:String,id:Long,myAnswer:CheckedQuizDto):ApiResult<Boolean>
    fun getAllQuiz(token:String):ApiResult<List<ContentQuizProjection>>
}