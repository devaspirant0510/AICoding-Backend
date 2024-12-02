package ngod.dev.aicoding.data.projectrion

import ngod.dev.aicoding.data.entity.Quiz
import ngod.dev.aicoding.data.entity.enum.Difficultly
import ngod.dev.aicoding.data.entity.enum.StudyType
import java.time.LocalDateTime

interface ContentQuizProjection {
    val id:Long
    val studyName:String
    val difficultly: Difficultly
    val studyType: StudyType
    val quiz:MutableList<QuizIdProjection>
    val account:AccountProjection?
}