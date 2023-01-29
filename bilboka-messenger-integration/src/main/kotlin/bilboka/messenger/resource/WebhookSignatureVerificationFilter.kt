package bilboka.messenger.resource

import bilboka.messenger.MessengerProperties
import org.apache.commons.codec.binary.Hex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.security.InvalidKeyException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class WebhookSignatureVerificationFilter : OncePerRequestFilter() {
    @Autowired
    lateinit var messengerProperties: MessengerProperties

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val signature = request.getHeader("x-hub-signature-256")
        if (signature == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST)
        } else {
            try {
                val cachedRequest = CachedBodyHttpServletRequest(request)
                val body: ByteArray = StreamUtils.copyToByteArray(cachedRequest.inputStream)

                validateSignature(body, signature)
                filterChain.doFilter(cachedRequest, response)
            } catch (e: InvalidRequestSignatureException) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN)
            }
        }
    }

    private fun validateSignature(body: ByteArray, signature: String) {
        val signatureHash = signature.split("sha256=").last()
        if (signatureHash != body.hash(messengerProperties.appSecret)) {
            logger.warn("Signatur ugyldig!")
            throw InvalidRequestSignatureException()
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !request.requestURI.equals("/${MessengerWebhookConfig.WEBHOOK_URL}")
                || !request.method.equals(HttpMethod.POST.toString())
    }
}

fun ByteArray.hash(key: String): String {
    val secretKeySpec = SecretKeySpec(key.toByteArray(), "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKeySpec)
    return Hex.encodeHexString(mac.doFinal(this))
}

@ResponseStatus(HttpStatus.FORBIDDEN)
class InvalidRequestSignatureException : InvalidKeyException()
