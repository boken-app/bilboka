package bilboka.messenger.service

import bilboka.messenger.consumer.MessengerWebhookConsumer
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.FacebookMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.String.format

@Component
class MessageResponderService {

    @Autowired
    lateinit var messengerConsumer: MessengerWebhookConsumer

    fun handleMessage(entry: FacebookEntry) {

        if (entry.messaging.size == 1) {

            val messageEvent = entry.messaging[0]

            val senderPSID = messageEvent.sender["id"]
            print(format("Sender PSID: %s", senderPSID))

            val text = messageEvent.message?.message?.get("text")

            // TODO behandle melding

            messengerConsumer.sendMessage(
                FacebookMessage(
                    recipient = mapOf(),
                    message = mapOf(Pair("text", format("Du sendte melding: %s", text)))
                )
            )
        } else {
            print("Ugyldig lengde på Messaging.")
        }
    }
}