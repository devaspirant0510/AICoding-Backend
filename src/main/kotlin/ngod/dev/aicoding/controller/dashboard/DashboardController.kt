package ngod.dev.aicoding.controller.dashboard

import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import ngod.dev.aicoding.data.projectrion.QuizProjection
import ngod.dev.aicoding.domain.service.DashboardService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) : DashboardSwagger {
    @GetMapping("/my/quiz/last")
    override fun getAllLastQuizLimit(
        @RequestHeader("Authorization") token: String,
    ): ApiResult<List<ContentQuizProjection>> {
        return ApiResult.success(
            dashboardService.getQuizLastLimitByUser(token, 3),
            HttpStatus.OK.value(),
            "대시보드 퀴즈 조회 성공"
        )
    }

    @GetMapping("my/codingTest/last")
    override fun getAllLastCodingTestLimit(
        @RequestHeader("Authorization")
        token: String
    ): ApiResult<List<ContentProjection>> {
        return ApiResult.success(
            dashboardService.getCodingTestLimitByUser(token),
            HttpStatus.OK.value(),
            "대시보드 코딩테스트 조회 성공"
        )
    }
}