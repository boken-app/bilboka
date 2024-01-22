package bilboka.security

import bilboka.core.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.servlet.http.HttpServletRequest

@Service
class EncodedEmailKeyAuthService(
    private val userService: UserService
) {
    companion object {
        const val AUTH_TOKEN_HEADER = "X-API-KEY"
        private val log: Logger = LoggerFactory.getLogger(EncodedEmailKeyAuthService::class.java)
    }

    @Value("\${bilboka.web.privateKey:key}")
    lateinit var privateKeyProp: String
    private val privateKeyEnv = System.getenv("WEB_PRIVATE_KEY")

    fun getAuthentication(request: HttpServletRequest): ApiKeyAuthentication {
        val token = request.getHeader(AUTH_TOKEN_HEADER)
        val user = token?.let { getUserByEmail(decodeTokenToEmail(it)) }
            ?: throw BadCredentialsException("Invalid API key")
        return ApiKeyAuthentication(user, AuthorityUtils.NO_AUTHORITIES)
    }

    private fun decodeTokenToEmail(token: String): String? {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
            .apply { init(Cipher.DECRYPT_MODE, privateKey()) }

        val decodedBytes = Base64.getDecoder().decode(token)
        val decryptedBytes = cipher.doFinal(decodedBytes)

        return String(decryptedBytes)
            .also { log.info("Dekryptert token: {}", it) }
            .getAsEmail()
            .also {
                if (it != null) log.info("Dekryptert token til epost {}", it)
                else log.info("Dekryptert token er ikke en gyldig epost")
            }
    }

    fun privateKey(): PrivateKey {
        val privateKeyPEM = (privateKeyEnv ?: privateKeyProp)
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val encoded = Base64.getDecoder().decode(privateKeyPEM)
        val keySpec = PKCS8EncodedKeySpec(encoded)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePrivate(keySpec)
    }

    fun String.getAsEmail(): String? {
        return if (this.isNotEmpty())
            Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$").matchEntire(this)?.value
        else null
    }

    fun getUserByEmail(email: String?): String? {
        return email?.let { userService.findUserByRegistration("web_email", it)?.username }
    }
}
