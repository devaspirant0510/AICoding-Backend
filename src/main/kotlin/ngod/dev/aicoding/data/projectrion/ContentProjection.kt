package ngod.dev.aicoding.data.projectrion

import ngod.dev.aicoding.data.entity.Quiz
import ngod.dev.aicoding.data.entity.enum.Difficultly
import ngod.dev.aicoding.data.entity.enum.StudyType
import java.time.LocalDateTime

interface ContentProjection{
    val id:Long
    val studyName:String
    val category:String
    val createdAt: LocalDateTime
    val difficultly: Difficultly
    val studyType: StudyType
    val quiz:MutableList<Quiz>
    val account:AccountProjection?
}
