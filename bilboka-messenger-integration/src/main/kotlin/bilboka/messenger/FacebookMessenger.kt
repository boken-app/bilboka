package bilboka.messenger

import bilboka.messagebot.BotMessenger
import bilboka.messenger.consumer.MessengerSendAPIConsumer
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
import bilboka.messenger.dto.QuickReply
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FacebookMessenger(
    val messengerConsumer: MessengerSendAPIConsumer
) : BotMessenger {
    private val logger = LoggerFactory.getLogger(javaClass)

    override val sourceID: String
        get() = "fb_messenger"

    override fun sendMessage(message: String, recipientID: String) {
        sendReply(message, recipientID)
    }

    override fun sendOptions(message: String, options: List<Pair<String, String>>, recipientID: String) {
        messengerConsumer.sendMessage(
            FacebookMessaging(
                recipient = mapOf("id" to recipientID),
                message = FacebookMessage(
                    text = message,
                    quickReplies = options.map {
                        QuickReply(payload = it.first, title = it.second)
                            .also { qr -> logger.debug("Option: payload={}, title={}", qr.payload, qr.title) }
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
                recipient = mapOf("id" to recipientPSID),
                message = FacebookMessage(
                    text = text
                )
            )
        )
    }
}
