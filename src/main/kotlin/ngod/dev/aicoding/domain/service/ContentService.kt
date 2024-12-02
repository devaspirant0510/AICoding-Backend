package ngod.dev.aicoding.domain.service

import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.repository.BaseContentRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ContentService(
    private val contentRepository: BaseContentRepository
) {
    fun findContentById(id:Long):ContentProjection{
        val content = contentRepository.findBaseContentById(id).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(),"BaseContent 를 찾을수 없습니다.")
        }
        return content
    }

}