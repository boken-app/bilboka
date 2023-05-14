package bilboka.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MessengerWebhookRequest(
    @param:JsonProperty("object")
    @get:JsonProperty("object")
    val requestObject: String,

    val entry: List<FacebookEntry>
)

data class FacebookEntry(
    val id: String,
    val time: Long,
    val messaging: List<MessagingReceived> = listOf()
)

data class MessagingReceived(
    val timestamp: Long? = null,
    @param:JsonProperty("messaging_type")
    @get:JsonProperty("messaging_type")
    val messagingType: String? = null,
    val sender: Map<String, String>? = null,
    val recipient: Map<String, String>? = null,
    val message: MessageReceived? = null,
    val postback: PostbackReceived? = null
)

data class MessageReceived(
    val mid: String? = null,
    val text: String? = null,

    @param:JsonProperty("quick_reply")
    @get:JsonProperty("quick_reply")
    val quickReply: QuickReplyReceived? = null
)

data class QuickReplyReceived(
    val title: String?,
    val payload: String,
)

data class PostbackReceived(
    val mid: String? = null,
    val title: String?,
    val payload: String
)
