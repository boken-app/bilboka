package bilboka.messagebot

import bilboka.core.user.InvalidRegistrationKeyException
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RegisterUserTest : AbstractMessageBotTest() {

    @Test
    fun startRegister_repliesReadyForRegister() {
        messagebot.processMessage("registrer", unregisteredSenderID)

        verifySentMessage("Klar for registrering! Skriv din hemmelige kode \uD83D\uDDDD", unregisteredSenderID)
    }

    @Test
    fun startRegisterAlreadyRegistered_repliesAlreadyRegistered() {
        messagebot.processMessage("registrer", registeredSenderID)

        verifySentMessage("Du er allerede registrert ¯\\_(ツ)_/¯")
    }

    @Nested
    @DisplayName("Given that a key has been assigned to user")
    inner class HasKeyForRegistering {
        private val key = "hemmelig_kode"

        @BeforeEach
        fun mockRegisterWithKey() {
            every {
                userService.register(
                    any(),
                    unregisteredSenderID,
                    any()
                )
            } throws InvalidRegistrationKeyException("Oh no")
            justRun { userService.register(any(), unregisteredSenderID, key) }
        }

        @Test
        fun startRegisterAndContinueSendingKey_completesRegistration() {
            messagebot.processMessage("registrer", unregisteredSenderID)
            verifySentMessage("Klar for registrering! Skriv din hemmelige kode \uD83D\uDDDD", unregisteredSenderID)
            every { userService.findUserByRegistration(any(), any()) } returns mockk(relaxed = true)
            messagebot.processMessage("hemmelig_kode", unregisteredSenderID)

            verify { userService.register(messengerSourceID, unregisteredSenderID, key) }
            verifySentMessage("Du er registrert! \uD83C\uDF89", unregisteredSenderID)
        }

        @Test
        fun startRegisterAndContinueSendingWrongKey_repliesInvalidKey() {
            messagebot.processMessage("registrer", unregisteredSenderID)
            verifySentMessage("Klar for registrering! Skriv din hemmelige kode \uD83D\uDDDD", unregisteredSenderID)
            messagebot.processMessage("feil_kode", unregisteredSenderID)

            verifySentMessage("Feil kode! \uD83E\uDD28", unregisteredSenderID)
        }
    }
}
