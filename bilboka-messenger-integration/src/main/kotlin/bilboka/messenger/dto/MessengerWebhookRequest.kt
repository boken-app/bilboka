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
    val messaging: List<FacebookMessaging>
) : Serializable

data class FacebookMessaging(
    val timestamp: Long,
    val sender: Map<String, String>,
    val recipient: Map<String, String>,
    val message: FacebookMessage
) : Serializable

data class FacebookMessage(
    val timestamp: Long,
    val sender: Map<String, String>,
    val recipient: Map<String, String>,
    val message: Map<String, String>
) : Serializable
