package ngod.dev.aicoding.data.repository

import ngod.dev.aicoding.data.entity.CodeReview
import org.springframework.data.jpa.repository.JpaRepository

interface CodeReviewRepository :JpaRepository<CodeReview,Long>{
}