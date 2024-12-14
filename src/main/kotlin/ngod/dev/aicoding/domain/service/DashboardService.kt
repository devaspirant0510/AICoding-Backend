package ngod.dev.aicoding.domain.service

import ngod.dev.aicoding.core.JwtProvider
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.data.entity.enum.StudyType
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.projectrion.ContentQuizProjection
import ngod.dev.aicoding.data.repository.BaseContentRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import kotlin.math.log

@Service
class DashboardService(
    private val contentRepository: BaseContentRepository,
    private val jwtProvider: JwtProvider
) {
    fun getQuizLastLimitByUser(token:String,limit:Int):List<ContentQuizProjection>{
        val user = jwtProvider.verifyToken(token)
        val userId = (user.get("id") as? Number)?.toLong() ?: throw ApiException(HttpStatus.UNAUTHORIZED.value(),"인증오류")
        val quizList = contentRepository.findTop3ByAccountIdAndStudyTypeOrderByCreatedAtDesc(
            userId,
            StudyType.QUIZ
        )
        return quizList
    }

    fun getCodingTestLimitByUser(token:String):List<ContentProjection>{
        val user = jwtProvider.verifyToken(token)
        val userId = (user.get("id") as? Number)?.toLong() ?: throw ApiException(HttpStatus.UNAUTHORIZED.value(),"인증오류")
        val quizList = contentRepository.findTop3ContentByAccountIdAndStudyTypeOrderByCreatedAtDesc(
            userId,
            StudyType.CODING_TEST
        )
        return quizList
    }

}