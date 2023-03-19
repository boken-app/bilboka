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

    fun sendAttachment(recipientPSID: String, attachment: ByteArray, type: AttachmentType) {
        logger.info(
            "Sender vedlegg til $recipientPSID"
        )

        val mediaType = MediaType.parse("application/pdf")

//        val att = File("report.pdf")
//            .apply {
//                writeBytes(attachment)
//            }

        val request = Request.Builder()
            .url(getUrl())
            .post(
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("filedata", "report.pdf", RequestBody.create(mediaType, attachment))
                    .addFormDataPart("recipient", "{\"id\":$recipientPSID}")
                    .addFormDataPart("message", "{\"attachment\":{\"type\":\"${type.strVal}\", \"payload\":{}}}")
                    .build()
            )
            .build()

        val response = client.newCall(request).execute()

//        val response: Response = khttp.post(
//            url = getUrl(),
//            data = mapOf(
//                Pair("recipient", "{\"id\":$recipientPSID}"),
//                Pair("message", "{\"attachment\":{\"type\":\"${type.strVal}\", \"payload\":{}}}"),
//            ),
//            files = listOf(FileLike("filedata", att))
//        )

        if (response.isSuccessful) {
            logger.info("Vedlegg sendt!")
        } else {
            logger.error(
                format(
                    "Sending av vedlegg gikk ikke ok. Status: %s - %s",
                    response.code(),
                    response.message()
                )
            )
        }
    }

    private fun getUrl() =
        "${messengerProperties.sendUrl}?${MessengerSendApiConfig.ACCESS_TOKEN}=${messengerProperties.pageAccessToken}"
}
