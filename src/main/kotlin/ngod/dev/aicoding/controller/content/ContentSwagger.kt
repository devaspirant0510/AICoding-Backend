package ngod.dev.aicoding.controller.content

import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection

interface ContentSwagger {
    fun findByContentId(id:Long): ApiResult<ContentProjection>
    fun findAllLastCreatedContent(token:String):ApiResult<List<ContentQuizProjection>>
}