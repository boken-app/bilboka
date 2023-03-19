package bilboka.messenger

import bilboka.messagebot.BotMessenger
import bilboka.messenger.consumer.MessengerSendAPIConsumer
import bilboka.messenger.dto.AttachmentType
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
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

    override fun sendPostback(options: List<String>, recipientID: String) {
        TODO("Not yet implemented")
    }

    override fun sendFile(file: ByteArray, recipientID: String) {
        messengerConsumer.sendAttachment(recipientPSID = recipientID, attachment = file, type = AttachmentType.FILE)
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
