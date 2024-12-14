package ngod.dev.aicoding.controller.content

import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import ngod.dev.aicoding.domain.service.ContentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/content")
class ContentController(
    private val contentService: ContentService
) : ContentSwagger {
    @GetMapping("{id}")
    override fun findByContentId(
        @PathVariable(name = "id")
        id: Long
    ): ApiResult<ContentProjection> {
        return ApiResult.success(
            contentService.findContentById(id),
            HttpStatus.OK.value(),
            "BaseContent 를 성공적으로 불러왔습니다."
        )
    }


    @GetMapping("/last")
    override fun findAllLastCreatedContent(
        @RequestHeader("Authorization")
        token: String
    ): ApiResult<List<ContentQuizProjection>> {
        return ApiResult.success(
            contentService.findAllTop3ContentForCreatedAt(token),
            HttpStatus.OK.value(),
            "BaseContent 목록을 불러왔습니다."
        )
    }

}