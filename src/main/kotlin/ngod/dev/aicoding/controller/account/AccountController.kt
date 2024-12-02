package ngod.dev.aicoding.controller.account

import jakarta.servlet.http.HttpServletRequest
import lombok.extern.slf4j.Slf4j
import ngod.dev.aicoding.controller.account.dto.RequestAccountDto
import ngod.dev.aicoding.controller.account.dto.RequestLoginDto
import ngod.dev.aicoding.controller.account.dto.ResponseLoginDto
import ngod.dev.aicoding.controller.account.dto.ResponseReGenerateToken
import ngod.dev.aicoding.core.ApiResult
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.data.entity.Account
import ngod.dev.aicoding.data.projectrion.AccountProjection
import ngod.dev.aicoding.data.repository.AccountRepository
import ngod.dev.aicoding.domain.service.AccountService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Slf4j
@RequestMapping("/api/v1/account")
class AccountController(
    private val accountService: AccountService,
    private val accountRepository: AccountRepository
) : AccountSwagger {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping()
    override fun joinAccount(
        @RequestBody
        requestAccountDto: RequestAccountDto
    ): ApiResult<AccountProjection> {
        return ApiResult.success(accountService.createAccount(requestAccountDto), HttpStatus.CREATED.value(), "회원가입 성공")
    }

    @PostMapping("/login")
    override fun login(
        @RequestBody
        requestLoginDto: RequestLoginDto
    ): ApiResult<ResponseLoginDto> {
        return ApiResult.success(accountService.loginAccount(requestLoginDto), HttpStatus.OK.value(), "로그인 성공")
    }

    @GetMapping("/{id}")
    override fun getUserById(
        @RequestHeader(name = "Authorization")
        token: String,
        @PathVariable(name = "id")
        id: Long
    ): ApiResult<AccountProjection> {
        return ApiResult.success(accountService.getUserByToken(token),HttpStatus.OK.value(),"계정 불러오기 성공")
    }

    @GetMapping()
    override fun getAllUser(
        @RequestHeader("Authorization")
        token: String
    ): ApiResult<List<AccountProjection>> {
        return ApiResult.success(accountService.fetchAllAccount(), HttpStatus.OK.value(), "전체 유저 조회")
    }

    @PostMapping("/token/access/issue")
    override fun regenerateAccessToken(request:HttpServletRequest): ApiResult<ResponseReGenerateToken> {
        val cookies = request.cookies?.associateBy { it.name }

        val refreshTokenFromCookie = cookies?.get("refreshToken")?.value
        if(refreshTokenFromCookie==null){
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(),"refreshToken 이 존재하지 않습니다.")
        }
        log.info(refreshTokenFromCookie)
        val accessToken= accountService.refreshAccessToken(refreshTokenFromCookie)
        val data = ResponseReGenerateToken(
            accessToken =accessToken
        )
        return ApiResult.success(data,HttpStatus.CREATED.value(),"aceessToken 재발급")

    }
}