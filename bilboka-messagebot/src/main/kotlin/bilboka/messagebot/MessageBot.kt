package bilboka.messagebot

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageBot {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun processMessage(message: String): String {
        logger.info("Mottok melding $message")
        return "Hei! Du sendte: $message. -MessageBot"
    }
}
