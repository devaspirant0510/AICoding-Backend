package ngod.dev.aicoding.controller.codeReview

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import ngod.dev.aicoding.controller.codeReview.dto.RequestCodeReviewDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.projectrion.ContentProjection

@Tag(name = "Code Review API", description = "코드 리뷰 관련 API 엔드포인트")
interface CodeReviewSwagger {
    @Operation(summary = "코드 리뷰 생성", description = "새로운 코드 리뷰 콘텐츠를 생성합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "코드 리뷰 생성 성공",
            content = [Content(schema = Schema(implementation = BaseContent::class))]),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
        ApiResponse(responseCode = "401", description = "인증 실패")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "requestCodeReviewDto", description = "코드 리뷰 생성 요청 DTO", required = true)
    )
    fun createCodeReview(
        token: String,
        requestCodeReviewDto: RequestCodeReviewDto
    ): ApiResult<BaseContent>

    @Operation(summary = "단일 코드 리뷰 조회", description = "특정 ID의 코드 리뷰를 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "코드 리뷰 조회 성공",
            content = [Content(schema = Schema(implementation = ContentProjection::class))]),
        ApiResponse(responseCode = "404", description = "코드 리뷰를 찾을 수 없음"),
        ApiResponse(responseCode = "401", description = "인증 실패")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "id", description = "조회할 코드 리뷰의 ID", required = true)
    )
    fun getOneCodeReview(
        token: String,
        id: Long
    ): ApiResult<ContentProjection>

    @Operation(summary = "전체 코드 리뷰 목록 조회", description = "모든 코드 리뷰의 목록을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "코드 리뷰 목록 조회 성공",
            content = [Content(array = ArraySchema(schema = Schema(implementation = ContentProjection::class)))]),
        ApiResponse(responseCode = "401", description = "인증 실패")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true)
    )
    fun getAllCodeReview(
        token: String
    ): ApiResult<List<ContentProjection>>
}