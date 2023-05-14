package bilboka.messenger.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable

data class FacebookSendRequest(
    val sender: Map<String, String>? = null,
    val recipient: Map<String, String>? = null,
    val message: FacebookMessage? = null,
) : Serializable

data class FacebookMessage(
    @JsonInclude(Include.NON_NULL) val text: String? = null,
    @JsonInclude(Include.NON_NULL) val attachment: Attachment? = null,

    @param:JsonProperty("quick_replies")
    @get:JsonProperty("quick_replies")
    @JsonInclude(Include.NON_NULL)
    val quickReplies: List<QuickReply>? = null
) : Serializable

data class QuickReply(
    val title: String,
    val payload: String,
    @param:JsonProperty("content_type")
    @get:JsonProperty("content_type")
    val contentType: String = "text",
    @param:JsonProperty("image_url")
    @get:JsonProperty("image_url")
    @JsonInclude(Include.NON_NULL)
    val imageUrl: String? = null,
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
