package ngod.dev.aicoding.controller.codingTest

import ngod.dev.aicoding.controller.codingTest.dto.RequestCodeEvaluate
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodingTest
import ngod.dev.aicoding.data.projectrion.ContentProjection

interface CodingTestSwagger {
    fun createCodingTest(token: String, requestContentDto: RequestContentDto): ApiResult<BaseContent>
    fun findCodingTestByContentId(token: String, contentId: Long): ApiResult<CodingTest>
    fun evaluateTestCases(token:String,codingTestId:Long,code:RequestCodeEvaluate): ApiResult<Float>
    fun getAllCodingTest(token:String):ApiResult<List<ContentProjection>>
}