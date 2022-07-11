package bilboka.messenger.service

import bilboka.messenger.consumer.MessengerWebhookConsumer
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
import org.json.JSONObject
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

            val sender = messageEvent.sender ?: throw IllegalArgumentException("Mangler sender")
            val senderPSID = sender["id"] ?: throw IllegalArgumentException("Mangler sender-PSID")
            logger.info(format("Sender PSID: %s", senderPSID))

            if (messageEvent.message != null) {
                logger.info("messageEvent.message=${JSONObject(messageEvent)}")

                val text = messageEvent.message.text
                logger.info(format("Mottok melding: %s", text))

                // TODO behandle melding

                sendReply(format("Du sendte melding: %s", text), senderPSID)
            } else {
                logger.info("Request inneholder ingen melding.")
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
