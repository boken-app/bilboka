package bilboka.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class MessengerProfileRequest(
    @param:JsonProperty("get_started")
    val getStarted: GetStarted,
    @param:JsonProperty("persistent_menu")
    val persistentMenu: List<PersistentMenu>
) : Serializable

data class PersistentMenu(
    val locale: String = "default",
    @param:JsonProperty("call_to_actions")
    val callToActions: List<PersistentMenuItem>
) : Serializable

data class PersistentMenuItem(
    val type: String = "postback",
    val title: String,
    val payload: String
) : Serializable

data class GetStarted(
    val payload: String
) : Serializable
