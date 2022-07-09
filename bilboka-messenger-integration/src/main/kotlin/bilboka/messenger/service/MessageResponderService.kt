package bilboka.messenger.service

import bilboka.messenger.consumer.MessengerWebhookConsumer
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.FacebookMessage
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

            val senderPSID = messageEvent.sender["id"]
            logger.info(format("Sender PSID: %s", senderPSID))

            logger.info("messageEvent.message=${JSONObject(messageEvent.message)}")

            val text = messageEvent.message?.message?.get("text")
            logger.info(format("Mottok melding: %s", text))

            // TODO behandle melding

            sendReply(format("Du sendte melding: %s", text), senderPSID!!)
        } else {
            logger.warn("Ugyldig lengde på Messaging. Forventet 1, var {}", entry.messaging.size)
        }
    }

    private fun sendReply(text: String, recipientPSID: String) {
        messengerConsumer.sendMessage(
            FacebookMessage(
                recipient = mapOf(Pair("id", recipientPSID)),
                message = mapOf(Pair("text", text))
            )
        )
    }
}
