package ngod.dev.aicoding.data.repository

import ngod.dev.aicoding.data.entity.Quiz
import ngod.dev.aicoding.data.projectrion.QuizProjection
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuizRepository :JpaRepository<Quiz,Long>{
    fun findQuizById(id:Long): Optional<QuizProjection>
}