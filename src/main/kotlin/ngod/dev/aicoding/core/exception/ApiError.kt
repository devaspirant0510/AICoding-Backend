package ngod.dev.aicoding.core.exception

import org.springframework.http.HttpStatus

data class ApiError(
    var code:Int,
    var status: HttpStatus,
    var message:String,
)