package ngod.dev.aicoding.controller.account

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import ngod.dev.aicoding.controller.account.dto.*
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.data.entity.Account
import ngod.dev.aicoding.data.projectrion.AccountProjection

@Tag(name = "Account Api", description = "계정 관련 API")
interface AccountSwagger {
    @Operation(summary = "회원가입", description = "유저 회원가입")
    fun joinAccount(requestAccountDto: RequestAccountDto): ApiResult<AccountProjection>

    @Operation(summary = "로그인", description = "유저 로그인")
    fun login(
        requestLoginDto: RequestLoginDto
    ): ApiResult<ResponseLoginDto>

    @Operation(summary = "유저 조회", description = "특정 아이디로 유저 조회")
    fun getUserById(token: String, id: Long): ApiResult<AccountProjection>

    @Operation(summary = "전체 유저 조회", description = "특정 아이디로 유저 조회")
    fun getAllUser(token: String): ApiResult<List<AccountProjection>>

    @Operation(summary = "액세스 토큰 재발급", description = "액세스토큰이 만료될경우 리프레시토큰으로 토큰 재발급")
    fun regenerateAccessToken(request: HttpServletRequest):ApiResult<ResponseReGenerateToken>

    @Operation(summary ="액세스토큰으로 유저 정보 조회", description = "액세스토큰으로 유저정보 조히")
    fun getUserWithAccessToken(token:String):ApiResult<AccountProjection>

    @Operation(summary ="액세스토큰으로 유저 정보 조회", description = "액세스토큰으로 유저정보 조히")
    fun updateExpForUser(token:String,requestUpdateExp: RequestUpdateExp): ApiResult<AccountProjection>

}