package bilboka.messenger

import bilboka.messenger.consumer.MessengerProfileAPIConsumer
import bilboka.messenger.dto.MessengerProfileRequest
import bilboka.messenger.dto.PersistentMenu
import bilboka.messenger.dto.PersistentMenuItem
import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ProfileConfig(
    var profileAPIConsumer: MessengerProfileAPIConsumer
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ContextStartedEvent::class)
    fun setProfileConfig(event: ContextStartedEvent) {
        logger.info("Setting profile config")
        profileAPIConsumer.doProfileUpdate(
            MessengerProfileRequest(
                listOf(
                    PersistentMenu(
                        callToActions = listOf(
                            PersistentMenuItem(
                                title = "Hjelp",
                                payload = "hlp"
                            )
                        )
                    )
                )
            )
        )
    }
}
