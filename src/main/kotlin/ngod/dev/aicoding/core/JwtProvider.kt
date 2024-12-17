package ngod.dev.aicoding.core

import com.google.gson.Gson
import com.google.gson.JsonIOException
import io.jsonwebtoken.*
import ngod.dev.aicoding.core.exception.ApiException
import ngod.dev.aicoding.core.model.AccessTokenPayload
import ngod.dev.aicoding.data.entity.Account
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtProvider {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    @Value("\${JWT_SECRET}")
    private lateinit var jwtSecret: String

    fun generateAccessToken(account: Account): String {
        return Jwts.builder()
            .setSubject(account.uid.toString()).setIssuer("http://localhost:8080/api/v1/auth")
            .setIssuedAt(Date()).setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .claim("nickname", account.nickname).claim("id", account.id).claim("userId", account.userId)
            .claim("uid", account.uid).signWith(SignatureAlgorithm.HS256, jwtSecret).compact()
    }

    fun generateRefreshToken(uid: String): String {
        return Jwts.builder().setSubject(uid).setIssuer("http://localhost:8080/api/v1/auth").setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)).signWith(
                SignatureAlgorithm.HS256, jwtSecret).compact()
    }
    fun verifyToken(token:String):Claims{
        try{
            var parseToken = token
            // Bearer 토큰일 경우 파싱
            if (token.startsWith("Bearer ")) {
                parseToken = token.substring(7)
            }
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(parseToken).body
        }catch (e: ExpiredJwtException) {
            println("토큰이 만료되었습니다: ${e.message}")
            throw ApiException(HttpStatus.UNAUTHORIZED.value(), "토큰이 만료되었습니다: ${e.message}")
        } catch (e: SignatureException) {
            println("토큰 서명이 유효하지 않습니다: ${e.message}")
            throw ApiException(HttpStatus.UNAUTHORIZED.value(), "토큰 서명이 유효하지 않습니다: ${e.message}")
        } catch (e: MalformedJwtException) {
            println("잘못된 형식의 JWT 토큰입니다: ${e.message}")
            throw ApiException(HttpStatus.BAD_REQUEST.value(), "잘못된 형식의 JWT 토큰입니다: ${e.message}")
        } catch (e: JwtException) {
            println("JWT 토큰 검증 중 오류 발생: ${e.message}")
            throw ApiException(HttpStatus.UNAUTHORIZED.value(), "JWT 토큰 검증 중 오류 발생: ${e.message}")
        } catch (e: JsonIOException) {
            println("알 수 없는 오류 발생: ${e.message}")
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류 발생: ${e.message}")
        } catch (e: Exception) {
            println("알 수 없는 오류 발생: ${e.message}")
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류 발생: ${e.message}")
        }
    }
    fun<T> mappingToken(claims: Claims,targetClass:Class<T>):T{
        val gson = Gson()
        val claimsJson = gson.toJson(claims)
        println(claimsJson)
        val jwtBody = gson.fromJson(claimsJson,targetClass)
        return jwtBody
    }

    fun verifyAccessToken(token: String): AccessTokenPayload {
        try {
            log.info(token)
            var accessToken = token
            // Bearer 토큰일 경우 파싱
            if (token.startsWith("Bearer ")) {
                accessToken = token.substring(7)
            }
            log.info(jwtSecret)
            val data = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(accessToken).body
            val gson = Gson()
            val claimsJson = gson.toJson(data)
            println(claimsJson)
            val jwtBody = gson.fromJson(claimsJson, AccessTokenPayload::class.java)
            return jwtBody
        } catch (e: ExpiredJwtException) {
            println("토큰이 만료되었습니다: ${e.message}")
            throw ApiException(HttpStatus.UNAUTHORIZED.value(), "토큰이 만료되었습니다: ${e.message}")
        } catch (e: SignatureException) {
            println("토큰 서명이 유효하지 않습니다: ${e.message}")
            throw ApiException(HttpStatus.UNAUTHORIZED.value(), "토큰 서명이 유효하지 않습니다: ${e.message}")
        } catch (e: MalformedJwtException) {
            println("잘못된 형식의 JWT 토큰입니다: ${e.message}")
            throw ApiException(HttpStatus.BAD_REQUEST.value(), "잘못된 형식의 JWT 토큰입니다: ${e.message}")
        } catch (e: JwtException) {
            println("JWT 토큰 검증 중 오류 발생: ${e.message}")
            throw ApiException(HttpStatus.UNAUTHORIZED.value(), "JWT 토큰 검증 중 오류 발생: ${e.message}")
        } catch (e: JsonIOException) {
            println("알 수 없는 오류 발생: ${e.message}")
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류 발생: ${e.message}")
        } catch (e: Exception) {
            println("알 수 없는 오류 발생: ${e.message}")
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류 발생: ${e.message}")
        }

    }
}