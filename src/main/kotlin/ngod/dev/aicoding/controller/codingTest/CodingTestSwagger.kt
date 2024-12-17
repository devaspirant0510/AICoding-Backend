package ngod.dev.aicoding.controller.codingTest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import ngod.dev.aicoding.controller.codingTest.dto.RequestCodeEvaluate
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodingTest
import ngod.dev.aicoding.data.projectrion.ContentProjection

@Tag(name = "Coding Test API", description = "코딩 테스트 관련 API")
interface CodingTestSwagger {
    @Operation(summary = "코딩 테스트 생성", description = "새로운 코딩 테스트 콘텐츠를 생성합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "코딩 테스트 생성 성공",
            content = [Content(schema = Schema(implementation = BaseContent::class))]),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
        ApiResponse(responseCode = "401", description = "인증 실패")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "requestContentDto", description = "코딩 테스트 생성 요청 DTO", required = true)
    )
    fun createCodingTest(
        token: String,
        requestContentDto: RequestContentDto
    ): ApiResult<BaseContent>

    @Operation(summary = "콘텐츠 ID로 코딩 테스트 조회", description = "특정 콘텐츠 ID에 해당하는 코딩 테스트를 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "코딩 테스트 조회 성공",
            content = [Content(schema = Schema(implementation = CodingTest::class))]),
        ApiResponse(responseCode = "404", description = "코딩 테스트를 찾을 수 없음")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "contentId", description = "조회할 콘텐츠 ID", required = true)
    )
    fun findCodingTestByContentId(
        token: String,
        contentId: Long
    ): ApiResult<CodingTest>

    @Operation(summary = "코딩 테스트 케이스 평가", description = "제출된 코드의 테스트 케이스를 평가합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "코드 평가 성공",
            content = [Content(schema = Schema(implementation = Float::class, description = "통과된 테스트 케이스 점수"))]),
        ApiResponse(responseCode = "400", description = "잘못된 코드 또는 요청"),
        ApiResponse(responseCode = "404", description = "코딩 테스트를 찾을 수 없음")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "codingTestId", description = "평가할 코딩 테스트 ID", required = true),
        Parameter(name = "code", description = "평가할 코드 정보", required = true)
    )
    fun evaluateTestCases(
        token: String,
        codingTestId: Long,
        code: RequestCodeEvaluate
    ): ApiResult<Float>

    @Operation(summary = "전체 코딩 테스트 목록 조회", description = "모든 코딩 테스트의 목록을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "코딩 테스트 목록 조회 성공",
            content = [Content(array = ArraySchema(schema = Schema(implementation = ContentProjection::class)))]),
        ApiResponse(responseCode = "401", description = "인증 실패")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true)
    )
    fun getAllCodingTest(
        token: String
    ): ApiResult<List<ContentProjection>>
}