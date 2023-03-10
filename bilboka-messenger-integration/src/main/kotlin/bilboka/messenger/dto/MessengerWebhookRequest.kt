package bilboka.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class MessengerWebhookRequest(
    @param:JsonProperty("object")
    @get:JsonProperty("object")
    val requestObject: String,

    val entry: List<FacebookEntry>
) : Serializable

data class FacebookEntry(
    val id: String,
    val time: Long,
    val messaging: List<FacebookMessaging> = listOf()
) : Serializable

data class FacebookMessaging(
    val timestamp: Long? = null,
    @param:JsonProperty("messaging_type")
    @get:JsonProperty("messaging_type")
    val messagingType: String? = null,
    val sender: Map<String, String>? = null,
    val recipient: Map<String, String>? = null,
    val message: FacebookMessage? = null,
    val postback: FacebookPostback? = null
) : Serializable

data class FacebookMessage(
    val mid: String? = null,
    @Deprecated("Fjern denne, aner ikke hvorfor den nekter å kjøre uten")
    val seq: Long? = null,
    val text: String?
) : Serializable

data class FacebookPostback(
    val mid: String? = null,
    val title: String?,
    val payload: String
) : Serializable
