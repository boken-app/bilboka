package bilboka.messenger

import bilboka.messenger.consumer.MessengerProfileAPIConsumer
import bilboka.messenger.dto.MessengerProfileRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ProfileConfig(
    var profileAPIConsumer: MessengerProfileAPIConsumer
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun setProfileConfig(event: ApplicationReadyEvent) {
        logger.info("Setting profile config")
        profileAPIConsumer.doProfileUpdate(
            MessengerProfileRequest(
                listOf(

                )
            )
        )
    }
}
