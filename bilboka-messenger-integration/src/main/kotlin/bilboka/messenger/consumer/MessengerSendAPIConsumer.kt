package bilboka.messenger.consumer

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.AttachmentType
import bilboka.messenger.dto.FacebookMessaging
import khttp.responses.Response
import okhttp3.*
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.lang.String.format

object MessengerSendApiConfig {
    const val ACCESS_TOKEN = "access_token"
}

@Component
class MessengerSendAPIConsumer(
    private val messengerProperties: MessengerProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val client = OkHttpClient()

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

    fun sendAttachment(recipientPSID: String, attachment: ByteArray, fileName: String, mediaType: String) {
        logger.info(
            "Sender vedlegg $fileName til $recipientPSID"
        )

        val request = Request.Builder()
            .url(getUrl())
            .post(
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "filedata",
                        fileName,
                        RequestBody.create(MediaType.parse(mediaType), attachment)
                    )
                    .addFormDataPart("recipient", "{\"id\":$recipientPSID}")
                    .addFormDataPart(
                        "message",
                        "{\"attachment\":{\"type\":\"${attachmentTypeFromMediaType(mediaType).strVal}\", \"payload\":{}}}"
                    )
                    .build()
            )
            .build()

        client.newCall(request).execute().use {
            if (it.isSuccessful) {
                logger.info("Vedlegg sendt!")
            } else {
                logger.error(
                    format(
                        "Sending av vedlegg gikk ikke ok. Status: %s - %s",
                        it.code(),
                        it.message()
                    )
                )
            }
        }
    }

    private fun getUrl() =
        "${messengerProperties.sendUrl}?${MessengerSendApiConfig.ACCESS_TOKEN}=${messengerProperties.pageAccessToken}"

    private fun attachmentTypeFromMediaType(mediaType: String): AttachmentType {
        return when (mediaType) {
            "application/pdf" -> AttachmentType.FILE
            else -> {
                throw NotImplementedError("Støtter ikke vedlegg med type $mediaType")
            }
        }
    }
}
