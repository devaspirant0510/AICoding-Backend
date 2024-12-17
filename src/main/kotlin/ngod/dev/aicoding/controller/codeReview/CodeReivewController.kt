package ngod.dev.aicoding.controller.codeReview

import ngod.dev.aicoding.controller.codeReview.dto.RequestCodeReviewDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodeReview
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.domain.service.CodeReviewService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/codeReview")
class CodeReviewController(
    private val codeReviewService: CodeReviewService
) : CodeReviewSwagger {
    @PostMapping()
    override fun createCodeReview(
        @RequestHeader("Authorization") token: String,
        @RequestBody requestCodeReviewDto: RequestCodeReviewDto
    ): ApiResult<BaseContent> {
        return ApiResult.success(
            codeReviewService.generateCoderReview(token, requestCodeReviewDto),
            HttpStatus.CREATED.value(),
            "코드리뷰 생성"
        )

    }

    @GetMapping("/{id}")
    override fun getOneCodeReview(
        @RequestHeader("Authorization")
        token: String,
        @PathVariable(name = "id")
        id: Long
    ): ApiResult<ContentProjection> {
        return ApiResult.success(
            codeReviewService.findOneCodeReview(id),
            HttpStatus.OK.value(),
            "content 조회 성공"
        )
    }


    @GetMapping("")
    override fun getAllCodeReview(@RequestHeader("Authorization") token: String): ApiResult<List<ContentProjection>> {
        return ApiResult.success(
            codeReviewService.findAllCodeReview(token),
            HttpStatus.OK.value(),
            "데이터 조회 성공"
        )

    }
}