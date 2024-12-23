package ngod.dev.aicoding.core.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ngod.dev.aicoding.core.JwtProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.math.log

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
):OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try{

            val authorizationHeader = request.getHeader("Authorization")
            val user = jwtProvider.verifyAccessToken(authorizationHeader)
            val authentication = UsernamePasswordAuthenticationToken(
                user.uid,  // 사용자 이름
                null,           // 인증 정보 (보통 비밀번호, 여기서는 null)
                // 권한 리스트,
                listOf()
            )
            println(authentication)

            // 3. SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request,response)
        } catch (e: Exception) {
            // 인증 실패 시 SecurityContext를 초기화하고 로그 처리 (필요 시)
            SecurityContextHolder.clearContext()
            println(e)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: ${e.message}")
            return
        }

    }
}