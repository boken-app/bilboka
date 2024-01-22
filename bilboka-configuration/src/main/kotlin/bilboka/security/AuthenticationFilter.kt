package bilboka.security

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationFilter : OncePerRequestFilter() {
    @Autowired
    lateinit var encodedEmailKeyAuthService: EncodedEmailKeyAuthService

    companion object {
        val log: Logger = org.slf4j.LoggerFactory.getLogger(AuthenticationFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        httpResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            encodedEmailKeyAuthService.getAuthentication(request).let {
                SecurityContextHolder.getContext().authentication = it
            }
            filterChain.doFilter(request, httpResponse)
        } catch (e: BadCredentialsException) {
            log.error("Authentication failed", e)
            filterChain.doFilter(request, httpResponse)
        } finally {
            SecurityContextHolder.clearContext()
        }
    }
}
