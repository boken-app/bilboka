package bilboka.messenger.consumer

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.Attachment
import bilboka.messenger.dto.AttachmentType
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
import khttp.responses.Response
import khttp.structures.files.FileLike
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.io.File
import java.lang.String.format

object MessengerSendApiConfig {
    const val ACCESS_TOKEN = "access_token"
}

@Component
class MessengerSendAPIConsumer(
    private val messengerProperties: MessengerProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendMessage(message: FacebookMessaging) {
        logger.info(
            "Sender melding '${if (logger.isTraceEnabled) message.message?.text else ""}' " +
                    "til ${message.recipient?.get("id")}"
        )

        val response: Response = khttp.post(
            url = getUrl(),
            json = JSONObject(message)
        )
        if (response.statusCode == HttpStatus.OK.value()) {
            logger.info("Melding sendt!")
        } else {
            logger.error(format("Sending gikk ikke ok. Status: %s - %s", response.statusCode, response.text))
        }
    }

    fun sendAttachment(recipientPSID: String, attachment: File, type: AttachmentType) {
        logger.info(
            "Sender vedlegg til ${recipientPSID}"
        )

        val response: Response = khttp.post(
            url = getUrl(),
            json = JSONObject(
                FacebookMessaging(
                    recipient = mapOf(Pair("id", recipientPSID)),
                    message = FacebookMessage(
                        attachment = Attachment(type = type, payload = null)
                    )
                )
            ),
            files = listOf(FileLike(attachment))
        )
        if (response.statusCode == HttpStatus.OK.value()) {
            logger.info("Vedlegg sendt!")
        } else {
            logger.error(format("Sending av vedlegg gikk ikke ok. Status: %s - %s", response.statusCode, response.text))
        }
    }

    private fun getUrl() =
        "${messengerProperties.sendUrl}?${MessengerSendApiConfig.ACCESS_TOKEN}=${messengerProperties.pageAccessToken}"
}
