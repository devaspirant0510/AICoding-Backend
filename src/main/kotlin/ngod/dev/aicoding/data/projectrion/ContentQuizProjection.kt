package ngod.dev.aicoding.data.projectrion

import ngod.dev.aicoding.data.entity.CodeReview
import ngod.dev.aicoding.data.entity.CodingTest
import ngod.dev.aicoding.data.entity.Quiz
import ngod.dev.aicoding.data.entity.enum.Difficultly
import ngod.dev.aicoding.data.entity.enum.StudyType
import java.time.LocalDateTime

interface ContentQuizProjection {
    val id:Long
    val studyName:String
    val category:String
    val difficultly: Difficultly
    val createdAt: LocalDateTime
    val studyType: StudyType
    val quiz:MutableList<QuizIdProjection>
    val account:AccountProjection?
    val codeReview:CodeReview?
    val codingTest:CodingTest?
}