package ngod.dev.aicoding.data.projectrion

import java.time.LocalDateTime

interface AccountProjection {
    val id:Long
    val userId:String
    val nickname:String
    val profileUrl: String?
    val createdAt:LocalDateTime
    val cash:Long
    val exp:Long
}