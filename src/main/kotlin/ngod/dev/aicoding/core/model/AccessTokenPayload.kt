package ngod.dev.aicoding.core.model
data class AccessTokenPayload(
    val sub: String,
    val iss: String,
    val iat: Long,
    val exp: Long,
    val nickname: String,
    val id: Long,
    val userId: String,
    val uid: String
)
