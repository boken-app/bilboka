package bilboka.messenger.consumer

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.AttachmentType
import bilboka.messenger.dto.FacebookMessaging
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
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
    private val mapper = jacksonObjectMapper()
    private val client = OkHttpClient()

    fun sendMessage(message: FacebookMessaging) {
        logger.info(
            "Sender melding '${if (logger.isTraceEnabled) message.message?.text else ""}' " +
                    "til ${message.recipient?.get("id")}"
        )

        val request = Request.Builder()
            .url(getUrl())
            .post(
                mapper.writeValueAsString(message).toRequestBody("application/json".toMediaTypeOrNull())
            )

        client.newCall(request.build()).execute().use {
            if (it.isSuccessful) {
                logger.info("Melding sendt!")
            } else {
                logger.error(format("Sending gikk ikke ok. Status: %s - %s", it.code, it.body?.string()))
            }
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
                        attachment.toRequestBody(mediaType.toMediaTypeOrNull())
                    )
                    .addFormDataPart("recipient", "{\"id\":$recipientPSID}")
                    .addFormDataPart(
                        "message",
                        "{\"attachment\":{\"type\":\"${attachmentTypeFromMediaType(mediaType).strVal}\", \"payload\":{}}}"
                    )
                    .build()
            )

        client.newCall(request.build()).execute().use {
            if (it.isSuccessful) {
                logger.info("Vedlegg sendt!")
            } else {
                logger.error(
                    format(
                        "Sending av vedlegg gikk ikke ok. Status: %s - %s",
                        it.code,
                        it.body?.string()
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
