package ngod.dev.aicoding.controller.account.dto

import ngod.dev.aicoding.data.projectrion.AccountProjection

data class ResponseLoginDto(
    var user:AccountProjection,
    var accessToken:String,
    var refreshToken:String
)
