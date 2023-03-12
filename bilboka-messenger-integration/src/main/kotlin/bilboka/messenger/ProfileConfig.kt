package bilboka.messenger

import bilboka.messenger.consumer.MessengerProfileAPIConsumer
import bilboka.messenger.dto.MessengerProfileRequest
import bilboka.messenger.dto.PersistentMenu
import bilboka.messenger.dto.PersistentMenuItem
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ProfileConfig(
    var profileAPIConsumer: MessengerProfileAPIConsumer
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private var hasDone = false

    @Scheduled(fixedDelay = 1000, initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    fun setProfileConfig() {
        if (!hasDone) {
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
            hasDone = true
        } else {
            logger.info("Profile config already set")
        }
    }
}
