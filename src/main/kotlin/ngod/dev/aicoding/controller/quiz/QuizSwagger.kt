package ngod.dev.aicoding.controller.quiz

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import ngod.dev.aicoding.controller.quiz.dto.CheckedQuizDto
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.controller.quiz.dto.RequestFeedbackDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import ngod.dev.aicoding.data.projectrion.QuizProjection

@Tag(name = "Quiz API", description = "퀴즈 관련 API")
interface QuizSwagger {
    @Operation(summary = "퀴즈 생성", description = "새로운 퀴즈 콘텐츠를 생성합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "퀴즈 생성 성공",
            content = [Content(schema = Schema(implementation = BaseContent::class))]),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
        ApiResponse(responseCode = "401", description = "인증 실패")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "requestContentDto", description = "퀴즈 생성 요청 DTO", required = true)
    )
    fun createQuiz(
        token: String,
        requestContentDto: RequestContentDto
    ): ApiResult<BaseContent>

    @Operation(summary = "퀴즈 피드백 제출", description = "퀴즈 결과에 대한 피드백을 제출합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "피드백 제출 성공",
            content = [Content(schema = Schema(implementation = String::class))]),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
        ApiResponse(responseCode = "401", description = "인증 실패")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "requestFeedbackDto", description = "퀴즈 피드백 요청 DTO", required = true)
    )
    fun feedBackQuizForResult(
        token: String,
        requestFeedbackDto: RequestFeedbackDto
    ): ApiResult<String>

    @Operation(summary = "콘텐츠 ID로 퀴즈 조회", description = "특정 콘텐츠 ID에 해당하는 퀴즈를 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "퀴즈 조회 성공",
            content = [Content(schema = Schema(implementation = BaseContent::class))]),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "contentId", description = "조회할 콘텐츠 ID", required = true)
    )
    fun findQuizByContentId(
        token: String,
        contentId: Long
    ): ApiResult<BaseContent>

    @Operation(summary = "퀴즈 ID로 퀴즈 조회", description = "특정 퀴즈 ID에 해당하는 퀴즈를 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "퀴즈 조회 성공",
            content = [Content(schema = Schema(implementation = QuizProjection::class))]),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "id", description = "조회할 퀴즈 ID", required = true)
    )
    fun findQuizById(
        token: String,
        id: Long
    ): ApiResult<QuizProjection>

    @Operation(summary = "여러 퀴즈 ID로 퀴즈 목록 조회", description = "제공된 ID 목록에 해당하는 퀴즈들을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "퀴즈 목록 조회 성공",
            content = [Content(array = ArraySchema(schema = Schema(implementation = QuizProjection::class)))]),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "id", description = "조회할 퀴즈 ID 목록", required = true)
    )
    fun findAllQuizByIds(
        token: String,
        id: List<Long>
    ): ApiResult<List<QuizProjection>>

    @Operation(summary = "퀴즈 설명 조회", description = "특정 퀴즈의 상세 설명을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "퀴즈 설명 조회 성공",
            content = [Content(schema = Schema(implementation = String::class))]),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "id", description = "설명을 조회할 퀴즈 ID", required = true)
    )
    fun showExplanationByQuiz(
        token: String,
        id: Long
    ): ApiResult<String>

    @Operation(summary = "퀴즈 답안 확인", description = "퀴즈의 답안을 확인하고 정답 여부를 판단합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "답안 확인 성공",
            content = [Content(schema = Schema(implementation = Boolean::class))]),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true),
        Parameter(name = "id", description = "답안을 확인할 퀴즈 ID", required = true),
        Parameter(name = "myAnswer", description = "제출할 답안", required = true)
    )
    fun checkedQuizAnswer(
        token: String,
        id: Long,
        myAnswer: CheckedQuizDto
    ): ApiResult<Boolean>

    @Operation(summary = "전체 퀴즈 목록 조회", description = "모든 퀴즈의 목록을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "퀴즈 목록 조회 성공",
            content = [Content(array = ArraySchema(schema = Schema(implementation = ContentQuizProjection::class)))]),
        ApiResponse(responseCode = "401", description = "인증 실패")
    )
    @Parameters(
        Parameter(name = "token", description = "사용자 인증 토큰", required = true)
    )
    fun getAllQuiz(
        token: String
    ): ApiResult<List<ContentQuizProjection>>
}