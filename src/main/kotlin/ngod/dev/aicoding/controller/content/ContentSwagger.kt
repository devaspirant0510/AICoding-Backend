package ngod.dev.aicoding.controller.content

import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.projectrion.ContentProjection

interface ContentSwagger {
    fun findByContentId(id:Long): ApiResult<ContentProjection>
}