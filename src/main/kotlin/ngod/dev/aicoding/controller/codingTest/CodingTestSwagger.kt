package ngod.dev.aicoding.controller.codingTest

import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent

interface CodingTestSwagger {
    fun createCodingTest(token: String, requestContentDto: RequestContentDto): ApiResult<BaseContent>
    fun findCodingTestByContentId(token: String, contentId: Long): ApiResult<BaseContent>
}