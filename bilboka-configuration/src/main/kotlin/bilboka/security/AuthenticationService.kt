package bilboka.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class AuthenticationService {
    companion object {
        const val AUTH_TOKEN_HEADER = "X-API-KEY"
    }

    @Value("\${bilboka.web.secret}")
    lateinit var apiSecret: String

    fun getAuthentication(request: HttpServletRequest): ApiKeyAuthentication {
        val token = request.getHeader(AUTH_TOKEN_HEADER)
        val user = getUserByEmail(decodeTokenToEmail(token)) ?: throw BadCredentialsException("Invalid API key")
        return ApiKeyAuthentication(user, AuthorityUtils.NO_AUTHORITIES)
    }

    private fun decodeTokenToEmail(token: String?): String? {
        return token?.let { // TODO implementere dekoding av token med secret
            try {
                String(Base64.getDecoder().decode(it))
            } catch (e: IllegalArgumentException) {
                return null
            }
        }
    }

    private fun getUserByEmail(email: String?): String? {
        return if (email == "some.test@mail.com") // TODO implementer henting fra usersevice
            "TestUser"
        else null
    }
}
