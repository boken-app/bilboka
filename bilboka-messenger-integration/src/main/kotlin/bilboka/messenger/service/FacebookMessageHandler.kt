package bilboka.messenger.service

import bilboka.messagebot.MessageBot
import bilboka.messenger.FacebookMessenger
import bilboka.messenger.dto.FacebookEntry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.String.format

@Component
class FacebookMessageHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var facebookMessenger: FacebookMessenger

    @Autowired
    lateinit var messageBot: MessageBot

    fun handleMessage(entry: FacebookEntry) {

        if (entry.messaging.size == 1) {
            val messageEvent = entry.messaging[0]
            val senderPSID = messageEvent.sender?.get("id") ?: throw IllegalArgumentException("Mangler sender")

            if (messageEvent.message?.text != null) {
                val text = messageEvent.message.text
                logger.info(format("Mottok melding=%s fra PSID=%s", text, senderPSID))
                messageBot.processMessage(text, senderPSID)
            } else {
                logger.info("Request inneholder ingen melding.")
                facebookMessenger.sendMessage("Du sendte noe rart jeg ikke skjønte", senderPSID)
            }

        } else {
            logger.warn("Ugyldig lengde på Messaging. Forventet 1, var {}", entry.messaging.size)
        }
    }
}
