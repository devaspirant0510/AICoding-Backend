package ngod.dev.aicoding.domain.service

import lombok.extern.slf4j.Slf4j
import ngod.dev.aicoding.controller.account.dto.RequestAccountDto
import ngod.dev.aicoding.controller.account.dto.RequestLoginDto
import ngod.dev.aicoding.controller.account.dto.ResponseLoginDto
import ngod.dev.aicoding.core.BcryptProvider
import ngod.dev.aicoding.core.JwtProvider
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.core.model.RefreshTokenPayload
import ngod.dev.aicoding.data.entity.Account
import ngod.dev.aicoding.data.projectrion.AccountProjection
import ngod.dev.aicoding.data.repository.AccountRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
@Slf4j
class AccountService(
    private val accountRepository: AccountRepository,
    private val bcryptProvider: BcryptProvider,
    private val jwtProvider: JwtProvider
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    // 아이디 중복 체크
    fun checkedDuplicatedId(userId: String): Boolean {
        return accountRepository.existsByUserId(userId)
    }
    fun createAccount(requestAccountDto: RequestAccountDto): AccountProjection {
        if(accountRepository.existsByUserId(requestAccountDto.userId)){
            throw ApiException(HttpStatus.CONFLICT.value(),"이미 존재하는 ID 입니다.")
        }
        // 비밀번호 해싱하여 데이터베이스에 저장
        val encodePassword = bcryptProvider.encodePassword(requestAccountDto.password)
        val userUid = bcryptProvider.generateUUID()
        val account = Account(
            nickname = requestAccountDto.nickname,
            userId = requestAccountDto.userId,
            password = encodePassword,
            uid = userUid
        )
        val savedAccount = accountRepository.save(account)
        return accountRepository.findAllById(savedAccount.id!!)
    }


    fun getUserByTokenAccount(token: String): Account {
        val userId = jwtProvider.verifyAccessToken(token).id
        val account = accountRepository.findById(userId).orElseThrow {
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "존재하지 않는 계정입니다.")
        }
        return account
    }
    fun getUserByToken(token: String): AccountProjection {
        val userId = jwtProvider.verifyAccessToken(token).id
        val account = accountRepository.findAccountById(userId).orElseThrow {
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "존재하지 않는 계정입니다.")
        }
        return account
    }

    fun getUserByUid(uid: String): Account {
        val account = accountRepository.findByUid(UUID.fromString(uid)).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 계정입니다")
        }
        return account
    }

    fun getAccountById(accountId: Long): Account {
        val account = accountRepository.findById(accountId).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(), "유저 정보를 찾을수 없습니다.")
        }
        return account
    }

    fun loginAccount(requestLoginDto: RequestLoginDto): ResponseLoginDto {
        log.info(requestLoginDto.userId)
        val loginAccount = accountRepository.findByUserId(requestLoginDto.userId)
        if (loginAccount.isEmpty) {
            throw ApiException(HttpStatus.NOT_ACCEPTABLE.value(), "로그인 실패: 존재하지 않는 아이디 입니다.")
        }
        if (!bcryptProvider.isMatchPassword(requestLoginDto.password, loginAccount.get().password)) {
            throw ApiException(HttpStatus.NOT_ACCEPTABLE.value(), "로그인 실패: 비밀번호가 일치하지 않습니다.")
        }
        val account = loginAccount.get()
        val responseDto = ResponseLoginDto(
            accessToken = jwtProvider.generateAccessToken(account),
            refreshToken = jwtProvider.generateRefreshToken(account.uid.toString()),
            user = object : AccountProjection {
                override val id: Long = account.id!!
                override val userId: String = account.userId
                override val nickname: String = account.nickname
                override val createdAt: LocalDateTime = account.createdAt
                override val exp: Long = account.exp
            }
        )
        return responseDto
    }

    fun fetchAllAccount(): List<AccountProjection> {
        val list = accountRepository.findAllBy()
        log.info("$list")
        return list
    }

    fun refreshAccessToken(refreshToken: String): String {
        val claims = jwtProvider.verifyToken(refreshToken)
        val refreshToken = jwtProvider.mappingToken(claims, RefreshTokenPayload::class.java)
        val account = getUserByUid(refreshToken.sub)
        return jwtProvider.generateAccessToken(account)
    }

    fun updateExpByUser(token: String,addedExp:Long):AccountProjection {
        val claims = jwtProvider.verifyToken(token)
        val accountId = (claims.get("id") as? Number)?.toLong()?:throw ApiException(HttpStatus.NOT_FOUND.value(),"인증되지 않은 유저입니다.")
        val account = accountRepository.findAccountById(accountId).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(),"유저를 찾는데 실패했습니다.")
        }
        accountRepository.updateExpById(accountId,addedExp)
        return accountRepository.findAccountById(accountId).orElseThrow {
            throw ApiException(HttpStatus.NOT_FOUND.value(),"유저를 찾는데 실패했습니다.")
        }
    }
}