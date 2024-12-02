package ngod.dev.aicoding.core.handler

import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.core.exception.ApiError
import ngod.dev.aicoding.core.exception.ApiException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ApiResult<Any>> {
        ex.printStackTrace()
        val apiError = ApiError(code = ex.code, status = HttpStatus.valueOf(ex.code), message = ex.message)
        val apiResult =
            ApiResult<Any>(
                success = false, data = null, message = ex.message, error = apiError, statusCode = ex.code
            )
        return ResponseEntity(apiResult, HttpStatusCode.valueOf(ex.code))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResult<Any>> {
        ex.printStackTrace()
        val apiError = ApiError(
            code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "error"
        )
        val apiResult =
            ApiResult<Any>(
                success = false,
                data = null,
                message = ex.message!!,
                error = apiError,
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
            )
        return ResponseEntity(apiResult, HttpStatus.INTERNAL_SERVER_ERROR)
    }

}