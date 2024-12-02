package ngod.dev.aicoding.domain.service

import io.jsonwebtoken.Jwts
import ngod.dev.aicoding.core.JwtProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.extension.ExtendWith
import ngod.dev.aicoding.data.repository.AccountRepository
import ngod.dev.aicoding.core.BcryptProvider
import ngod.dev.aicoding.controller.account.dto.RequestAccountDto
import ngod.dev.aicoding.controller.account.dto.RequestLoginDto
import ngod.dev.aicoding.data.entity.Account
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class AccountServiceTest {

    private lateinit var accountService: AccountService
    private lateinit var accountRepository: AccountRepository
    private lateinit var bcryptProvider: BcryptProvider
    private lateinit var jwtProvider: JwtProvider

    @BeforeEach
    fun setup() {
        // Mock 객체 생성
        accountRepository = Mockito.mock(AccountRepository::class.java)
        bcryptProvider = BcryptProvider()
        jwtProvider = JwtProvider()

        // AccountService 인스턴스 생성
        accountService = AccountService(accountRepository, bcryptProvider, jwtProvider)
    }

    @Test()
    fun testGenerateToken() {
        val a = jwtProvider.generateAccessToken(
            Account(
                id=1,
                uid=UUID.randomUUID(),
                userId = "lsh0510",
                password = "1234",
                cash = 0,
                exp = 0,
                createdAt = LocalDateTime.now(),
                nickname = "s",

            )
        )
        println(a)
        val secret = "DcdVM8MzweKCSzeAVx71fw7xqCg3IWnOLCu4nPJcNiGMdCH5uU"
        val data = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(a).body
        println(data)
    }
    @Test
    fun testVerifyJwtToken(){
        val token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGU0NDhhOS02MTY2LTQ5MDctOWI0Mi0yMGU4NWIxOWI0YTYiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL3YxL2F1dGgiLCJpYXQiOjE3MzI4OTQ1MDQsImV4cCI6MTczMjg5ODEwNCwibmlja25hbWUiOiJzZXVuZ2hvIiwiaWQiOjEsInVzZXJJZCI6ImxzaDA1MTAiLCJ1aWQiOiJkZGU0NDhhOS02MTY2LTQ5MDctOWI0Mi0yMGU4NWIxOWI0YTYifQ.1S0toEHdhGlxWHgInb_u00b7FY-96wgFWz-IszhRHvk"
        val secret = "DcdVM8MzweKCSzeAVx71fw7xqCg3IWnOLCu4nPJcNiGMdCH5uU"
        val data = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token).body
        println(data)
    }

    @Test
    fun `test loginAccount generates token`() {
        // given: JWT 토큰이 특정 값으로 생성되도록 mock
        val mockToken = "mockJwtToken123"

        // when: 로그인 메서드 호출
        val token = accountService.loginAccount(RequestLoginDto(userId = "ad", password = "12"))

        // then: 반환된 토큰 값 검증
        assertThat(token).isEqualTo(mockToken)
        println("Generated Token: $token") // 콘솔에 출력하여 확인할 수 있음
    }
}
