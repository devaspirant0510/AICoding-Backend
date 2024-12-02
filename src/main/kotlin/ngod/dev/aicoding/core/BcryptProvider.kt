package ngod.dev.aicoding.core

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.UUID

// 비밀번호 해시
@Component
class BcryptProvider {
    private val encoder = BCryptPasswordEncoder()
    fun generateUUID():UUID{
        return UUID.randomUUID()
    }

    fun encodePassword(rawPassword: String): String {
        return encoder.encode(rawPassword)
    }

    fun isMatchPassword(rawPassword: String, encodedPassword: String): Boolean {
        return encoder.matches(rawPassword, encodedPassword)
    }

}