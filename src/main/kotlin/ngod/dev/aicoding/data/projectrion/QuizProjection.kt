package ngod.dev.aicoding.data.projectrion

import ngod.dev.aicoding.data.entity.enum.Difficultly
import ngod.dev.aicoding.data.entity.enum.StudyType
import java.time.LocalDateTime

interface QuizProjection {
    val id:Long
    val question:String
    val choices:List<String>
}