package ngod.dev.aicoding.core.exception

class ApiException(
    val code: Int,
    override val message: String
) : RuntimeException(message)
