package bilboka.messenger.consumer

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.FacebookMessage
import khttp.responses.Response
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.lang.String.format

object MessengerWebhookConfig {
    const val ACCESS_TOKEN = "access_token"
}

@Component
class MessengerWebhookConsumer(
    private val messengerProperties: MessengerProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendMessage(message: FacebookMessage) {
        logger.info("Sender melding med access token=${messengerProperties.pageAccessToken}, sendUrl=${messengerProperties.sendUrl}")

        val response: Response = khttp.post(
            url = messengerProperties.sendUrl,
            headers = mapOf(Pair(MessengerWebhookConfig.ACCESS_TOKEN, messengerProperties.pageAccessToken)),
            data = JSONObject(message)
        )
        if (response.statusCode == HttpStatus.OK.value()) {
            logger.info("Melding sendt!")
        } else {
            logger.error(format("Sending gikk ikke ok. Status: %s - %s", response.statusCode, response.text))
        }
    }

}