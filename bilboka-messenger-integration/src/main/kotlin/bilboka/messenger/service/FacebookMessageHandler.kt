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
            logger.info("Håndterer innkommende request med " +
                    (messageEvent.message?.let { "| melding |" } ?: "") +
                    (messageEvent.postback?.let { "| postback |" } ?: ""))

            if (messageEvent.postback?.payload != null) {
                logger.info(format("Mottok postback (title=%s) fra PSID=%s", messageEvent.postback.title, senderPSID))
                logger.trace(format("Payload: '%s'", messageEvent.postback.payload))
                messageBot.processMessage(messageEvent.postback.payload, senderPSID)
            } else if (messageEvent.message?.text != null) {
                val text = messageEvent.message.text
                logger.info(format("Mottok melding fra PSID=%s", senderPSID))
                logger.trace(format("Meldingsinnhold: '%s'", text))
                messageBot.processMessage(text, senderPSID)
            } else {
                logger.debug("Request inneholder ingen melding eller postback.")
                facebookMessenger.sendMessage("Du sendte noe rart jeg ikke skjønte", senderPSID)
            }

        } else {
            logger.warn("Ugyldig lengde på Messaging. Forventet 1, var {}", entry.messaging.size)
        }
    }
}
