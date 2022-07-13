package bilboka.messagebot

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageBot {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun processMessage(message: String): String {
        logger.info("Mottok melding $message")

        return when (message) {
            "Hei" -> "Hei"
            "Hei!" -> "Hei!"
            "Skjer" -> "Ikke noe spes. Der?"
            "Skjer?" -> "Ikke noe spes. Der?"
            "Hvem der" -> "Bare meg!"
            "Ikke noe spes" -> "ok"
            "Ikke noe" -> "ok"
            "Ingenting" -> "ok"
            else -> "Usikker på hva du mener med $message"
        }
    }
}
