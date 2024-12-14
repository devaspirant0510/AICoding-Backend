package ngod.dev.aicoding.controller.codeReview

import ngod.dev.aicoding.controller.codeReview.dto.RequestCodeReviewDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodeReview
import ngod.dev.aicoding.data.projectrion.ContentProjection

interface CodeReviewSwagger {
    fun createCodeReview(token: String, requestCodeReviewDto: RequestCodeReviewDto): ApiResult<BaseContent>
    fun getOneCodeReview(token: String,id:Long): ApiResult<ContentProjection>
    fun getAllCodeReview(token:String):ApiResult<ContentProjection>
}