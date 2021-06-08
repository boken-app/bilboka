package bilboka.messenger.dto

import java.io.Serializable
import java.util.*

data class MessengerWebhookRequest(
    var object_: String,
    var entry: List<FacebookEntry>
) : Serializable

data class FacebookEntry(
    var id: String,
    var time: Long,
    var messaging: List<FacebookMessaging>
) : Serializable

data class FacebookMessaging(
    var timestamp: Long,
    var sender: Map<String, String>,
    var recipient: Map<String, String>,
    var message: FacebookMessage
) : Serializable

data class FacebookMessage(
    var timestamp: Long,
    var sender: Map<String, String>,
    var recipient: Map<String, String>,
    var message: Map<String, String>
) : Serializable
