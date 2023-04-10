package bilboka.messenger

import bilboka.messagebot.BotMessenger
import bilboka.messenger.consumer.MessengerSendAPIConsumer
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
import bilboka.messenger.dto.QuickReply
import org.springframework.stereotype.Component

@Component
class FacebookMessenger(
    val messengerConsumer: MessengerSendAPIConsumer
) : BotMessenger {
    override val sourceID: String
        get() = "fb_messenger"

    override fun sendMessage(message: String, recipientID: String) {
        sendReply(message, recipientID)
    }

    override fun sendOptions(message: String, options: List<String>, recipientID: String) {
        messengerConsumer.sendMessage(
            FacebookMessaging(
                recipient = mapOf(Pair("id", recipientID)),
                message = FacebookMessage(
                    text = message,
                    quickReplies = options.map {
                        QuickReply(title = it, payload = it)
                    }
                )
            )
        )
    }

    override fun sendPdf(file: ByteArray, fileName: String, recipientID: String) {
        messengerConsumer.sendAttachment(
            recipientPSID = recipientID,
            attachment = file,
            fileName = fileName,
            mediaType = "application/pdf"
        )
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
