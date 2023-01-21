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
    val mid: String = "",
    val seq: String = "",
    val text: String?
) : Serializable

data class FacebookPostback(
    val timestamp: Long?,
    val sender: Map<String, String> = mapOf(),
    val recipient: Map<String, String> = mapOf(),
    val payload: String
) : Serializable
