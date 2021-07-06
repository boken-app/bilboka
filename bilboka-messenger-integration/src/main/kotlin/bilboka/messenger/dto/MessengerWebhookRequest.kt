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
    val timestamp: Long,
    val sender: Map<String, String> = mapOf(),
    val recipient: Map<String, String> = mapOf(),
    val message: FacebookMessage?,
    val postback: FacebookPostback?
) : Serializable

data class FacebookMessage(
    val timestamp: Long,
    val sender: Map<String, String> = mapOf(),
    val recipient: Map<String, String> = mapOf(),
    val message: Map<String, String> = mapOf()
) : Serializable

data class FacebookPostback(
    val timestamp: Long,
    val sender: Map<String, String> = mapOf(),
    val recipient: Map<String, String> = mapOf(),
    val payload: String
) : Serializable
