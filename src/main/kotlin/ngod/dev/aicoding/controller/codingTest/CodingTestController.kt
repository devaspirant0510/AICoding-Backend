package ngod.dev.aicoding.controller.codingTest

import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.domain.service.CodingTestService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
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
    private val codingTestService: CodingTestService
) : CodingTestSwagger {
    @PostMapping
    override fun createCodingTest(
        @RequestHeader("Authorization")
        token: String,
        @RequestBody
        requestContentDto: RequestContentDto
    ): ApiResult<BaseContent> {
        return ApiResult.success(codingTestService.createQuiz(requestContentDto,1),HttpStatus.CREATED.value(),"코딩테스트 생성 완료")
    }

    @GetMapping("/{id}")
    override fun findCodingTestByContentId(
        @RequestHeader("Authorization")
        token: String,
        @PathVariable("id")
        contentId: Long): ApiResult<BaseContent> {
        TODO("Not yet implemented")
    }
}