package ngod.dev.aicoding.domain.service

import ngod.dev.aicoding.controller.codeReview.dto.RequestCodeReviewDto
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.data.entity.BaseContent
import ngod.dev.aicoding.data.entity.CodeReview
import ngod.dev.aicoding.data.entity.enum.StudyType
import ngod.dev.aicoding.data.projectrion.ContentProjection
import ngod.dev.aicoding.data.repository.BaseContentRepository
import ngod.dev.aicoding.data.repository.CodeReviewRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CodeReviewService(
    private val gptService: GptService,
    private val accountService: AccountService,
    private val baseContentRepository: BaseContentRepository,
    private val codeReviewRepository: CodeReviewRepository
) {
    fun findOneCodeReview(id:Long):ContentProjection{
        val content = baseContentRepository.findBaseContentById(id).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(),"content 를 찾을수 없습니다.")
        }
        println(content)
        if(content.codeReview==null){
            throw ApiException(HttpStatus.NOT_FOUND.value(),"데이터를 불러오는중 오류 발생")
        }
        return content
    }
    @Transactional
    fun updateContent(content:BaseContent,studyName:String,codeReview: CodeReview){
        content.studyName = studyName
        content.codeReview = codeReview
    }
    @Transactional
    fun generateCoderReview(token:String, requestCodeReviewDto: RequestCodeReviewDto):BaseContent{
        val account = accountService.getUserByTokenAccount(token)
        val baseContent = BaseContent(
            account = account,
            category = requestCodeReviewDto.language,
            studyType = StudyType.CODE_REVIEW,
        )
        baseContentRepository.save(baseContent)
        val codeReviewResult =gptService.generateCodeReview(requestCodeReviewDto)
        val codeReview =  CodeReview(
            language = requestCodeReviewDto.language,
            review = codeReviewResult.description,
            code=requestCodeReviewDto.code
        )
        codeReviewRepository.save(codeReview)
        updateContent(baseContent, codeReviewResult.title,codeReview)
        return findCodeReviewByContentId(baseContent.id!!)

    }
    fun findCodeReviewByContentId(contentId:Long):BaseContent{
        val result = baseContentRepository.findById(contentId).orElseThrow {
            throw ApiException(HttpStatus.OK.value(),"content 가 존재하지 않아요")
        }
        return result
    }
}