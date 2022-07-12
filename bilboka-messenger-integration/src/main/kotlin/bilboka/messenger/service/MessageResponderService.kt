package bilboka.messenger.service

import bilboka.messenger.consumer.MessengerWebhookConsumer
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.String.format

@Component
class MessageResponderService {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var messengerConsumer: MessengerWebhookConsumer

    fun handleMessage(entry: FacebookEntry) {

        if (entry.messaging.size == 1) {

            val messageEvent = entry.messaging[0]

            val senderPSID = messageEvent.sender?.get("id") ?: throw IllegalArgumentException("Mangler sender")

            if (messageEvent.message?.text != null) {
                val text = messageEvent.message.text
                logger.info(format("Mottok melding=%s fra PSID=%s", text, senderPSID))

                // TODO behandle melding

                sendReply(format("Du sendte melding: %s", text), senderPSID)
            } else {
                logger.warn("Request inneholder ingen melding.")
                sendReply("Du sendte noe rart jeg ikke skjønte", senderPSID)
            }

        } else {
            logger.warn("Ugyldig lengde på Messaging. Forventet 1, var {}", entry.messaging.size)
        }
    }

    private fun sendReply(text: String, recipientPSID: String) {
        messengerConsumer.sendMessage(
            FacebookMessaging(
                recipient = mapOf(Pair("id", recipientPSID)),
                message = FacebookMessage(
                    text = text
                )
            )
        )
    }
}
