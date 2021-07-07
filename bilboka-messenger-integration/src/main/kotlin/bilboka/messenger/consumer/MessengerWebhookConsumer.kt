package bilboka.messenger.consumer

import bilboka.messenger.dto.FacebookMessage
import khttp.responses.Response
import org.json.JSONObject
import org.springframework.http.HttpStatus
import java.lang.String.format

object MessengerWebhookConfig {
    const val ACCESS_TOKEN = "access_token"
}

class MessengerWebhookConsumer(
    var sendUrl: String,
    var pageAccessToken: String
) {

    fun sendMessage(message: FacebookMessage) {
        val response: Response = khttp.post(
            url = sendUrl,
            headers = mapOf(Pair(MessengerWebhookConfig.ACCESS_TOKEN, pageAccessToken)),
            data = JSONObject(message)
        )
        if (response.statusCode == HttpStatus.OK.value()) {
            print("Melding sendt!")
        } else {
            print(format("Sending gikk ikke ok. Status: %s - %s", response.statusCode, response.text))
        }
    }

}