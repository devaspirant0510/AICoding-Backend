package ngod.dev.aicoding.controller.dashboard

import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import ngod.dev.aicoding.data.projectrion.QuizProjection

interface DashboardSwagger {
    fun getAllLastQuizLimit(token: String,): ApiResult<List<ContentQuizProjection>>
    fun getAllLastCodingTestLimit(token:String):ApiResult<List<ContentProjection>>
}