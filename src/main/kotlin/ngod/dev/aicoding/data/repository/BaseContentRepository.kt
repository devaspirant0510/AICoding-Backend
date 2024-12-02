package ngod.dev.aicoding.data.repository

import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.projectrion.ContentProjection
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BaseContentRepository :JpaRepository<BaseContent,Long>{
    fun findBaseContentById(id:Long):Optional<ContentProjection>
}