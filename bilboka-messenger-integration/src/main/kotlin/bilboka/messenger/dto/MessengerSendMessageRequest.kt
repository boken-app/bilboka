package bilboka.messenger.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
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
    @JsonInclude(Include.NON_NULL) val mid: String? = null,
    @Deprecated("Fjern denne, aner ikke hvorfor den nekter å kjøre uten")
    @JsonInclude(Include.NON_NULL) val seq: Long? = null,
    @JsonInclude(Include.NON_NULL) val text: String? = null,
    @JsonInclude(Include.NON_NULL) val attachment: Attachment? = null
) : Serializable

data class Attachment(
    val type: AttachmentType,
    val payload: Any?
)

enum class AttachmentType(
    @JsonValue val strVal: String
) {
    AUDIO("audio"),
    FILE("file"),
    IMAGE("image"),
    TEMPLATE("template"),
    VIDEO("video")
}

data class FacebookPostback(
    val mid: String? = null,
    val title: String?,
    val payload: String
) : Serializable
