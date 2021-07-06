package bilboka.messenger.consumer

import bilboka.messenger.dto.FacebookMessage
import khttp.responses.Response
import org.json.JSONObject

object MessengerWebhookConfig {
    const val ACCESS_TOKEN = "access_token"
}

class MessengerWebhookConsumer(
    var sendUrl: String,
    var pageAccessToken: String
) {

    fun sendMessage(message: FacebookMessage): Response {
        return khttp.post(
            url = sendUrl,
            headers = mapOf(Pair(MessengerWebhookConfig.ACCESS_TOKEN, pageAccessToken)),
            data = JSONObject(message)
        )
    }

}