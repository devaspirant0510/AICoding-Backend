package ngod.dev.aicoding.core.model

data class RefreshTokenPayload (
    val sub: String,
    val iss: String,
    val iat: Long,
    val exp: Long,
)