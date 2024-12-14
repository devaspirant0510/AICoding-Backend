package ngod.dev.aicoding.controller.codingTest

import ngod.dev.aicoding.controller.codingTest.dto.RequestCodeEvaluate
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodingTest
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.domain.service.CodingTestService
import ngod.dev.aicoding.domain.service.ContentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController()
@RequestMapping("api/v1/codingTest")
class CodingTestController(
    private val codingTestService: CodingTestService,
    private val contentService: ContentService
) : CodingTestSwagger {
    @PostMapping
    override fun createCodingTest(
        @RequestHeader("Authorization")
        token: String,
        @RequestBody
        requestContentDto: RequestContentDto
    ): ApiResult<BaseContent> {
        return ApiResult.success(
            codingTestService.createCodingTest(requestContentDto, token),
            HttpStatus.CREATED.value(),
            "코딩테스트 생성 완료"
        )
    }

    @GetMapping("/{id}")
    override fun findCodingTestByContentId(
        @RequestHeader("Authorization")
        token: String,
        @PathVariable("id")
        contentId: Long
    ): ApiResult<CodingTest> {
        return ApiResult.success(codingTestService.findCodingTestById(contentId), HttpStatus.OK.value(), "a")
    }

    @PostMapping("/{id}/evaluate")
    override fun evaluateTestCases(
        @RequestHeader("Authorization")
        token: String,
        @PathVariable(name = "id")
        codingTestId: Long,
        @RequestBody
        code: RequestCodeEvaluate
    ): ApiResult<Float> {
        val testResult = codingTestService.evaluateCode(codingTestId, code.code, code.language)
        var resultMessage = ""
        if (testResult == 100F) {
            resultMessage = "정답"
        } else {
            resultMessage = "오답: 전체 테스트케이스중 ${testResult}%"
        }
        return ApiResult.success(
            codingTestService.evaluateCode(codingTestId, code.code, code.language),
            HttpStatus.OK.value(),
            resultMessage
        )
    }


    @GetMapping("")
    override fun getAllCodingTest(@RequestHeader("Authorization") token: String): ApiResult<List<ContentProjection>> {
        return ApiResult.success(
            codingTestService.findAllCodingTestByUserId(token),
            HttpStatus.OK.value(),

            )
    }
}