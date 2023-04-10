package bilboka.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class MessengerProfileRequest(
    @param:JsonProperty("get_started")
    val getStarted: GetStarted,
    @param:JsonProperty("persistent_menu")
    val persistentMenu: List<PersistentMenu>,
    @param:JsonProperty("ice_breakers")
    val iceBreakers: List<IceBreaker> = emptyList()
) : Serializable

data class PersistentMenu(
    @param:JsonProperty("call_to_actions")
    val callToActions: List<PersistentMenuItem>,
    val locale: String = "default"
) : Serializable

data class PersistentMenuItem(
    val type: String = "postback",
    val title: String,
    val payload: String
) : Serializable

data class IceBreaker(
    @param:JsonProperty("get_started")
    val callToActions: List<IceBreakerAction>,
    val locale: String = "default"
) : Serializable

data class IceBreakerAction(
    val question: String,
    val payload: String
) : Serializable

data class GetStarted(
    val payload: String
) : Serializable
