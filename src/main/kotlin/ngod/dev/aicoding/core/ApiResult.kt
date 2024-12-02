package ngod.dev.aicoding.core

import ngod.dev.aicoding.core.exception.ApiError

data class ApiResult<T>(
    var success: Boolean,
    var statusCode: Int,
    var message: String,
    var data: T? = null,
    var error: ApiError? = null
) {
    companion object {
        fun <T> success(data: T, statusCode: Int, message: String = "success"): ApiResult<T> {
            return ApiResult(success = true, message = message, statusCode = statusCode, data = data, error = null);
        }
        fun <T> fail(data: T, statusCode: Int, message: String = "error"): ApiResult<T> {
            return ApiResult(success = false, message = message, statusCode = statusCode, data = data, error = null);
        }
    }

}