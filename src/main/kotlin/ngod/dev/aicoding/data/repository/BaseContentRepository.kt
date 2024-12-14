package ngod.dev.aicoding.data.repository

import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.enum.StudyType
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BaseContentRepository : JpaRepository<BaseContent, Long> {
    fun findBaseContentById(id: Long): Optional<ContentProjection>
    fun findTop3ByAccountIdOrderByCreatedAtDesc(
        accountId: Long
    ):List<ContentQuizProjection>
    fun findTop3ByAccountIdAndStudyTypeOrderByCreatedAtDesc(
        accountId: Long,
        studyType: StudyType
    ): List<ContentQuizProjection>
    fun findTop3ContentByAccountIdAndStudyTypeOrderByCreatedAtDesc(
        accountId: Long,
        studyType: StudyType
    ):List<ContentProjection>
    fun findAllByAccountId(
        accountId: Long
    ):List<ContentProjection>
}