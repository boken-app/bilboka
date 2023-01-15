package bilboka.messagebot.commands

import bilboka.core.user.InvalidRegistrationKeyException
import bilboka.core.user.UserAlreadyRegisteredException
import bilboka.core.user.UserService
import bilboka.messagebot.BotMessenger
import kotlin.text.RegexOption.IGNORE_CASE

class RegisterUser(
    private val botMessenger: BotMessenger,
    private val userService: UserService
) : GeneralChatCommand() {
    private val matcher = Regex(
        "(reg|registrer)",
        IGNORE_CASE
    )

    private var regInProrgess = false

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message) || regInProrgess
    }

    override fun execute(senderID: String, message: String) {
        if (regInProrgess) {
            try {
                userService.register(botMessenger.sourceID, senderID, message)
                botMessenger.sendMessage(
                    "Du er registrert!",
                    senderID
                )
            } catch (ex: UserAlreadyRegisteredException) {
                botMessenger.sendMessage(
                    "Du er allerede registrert.",
                    senderID
                )
            } catch (ex: InvalidRegistrationKeyException) {
                botMessenger.sendMessage(
                    "Feil kode! :(",
                    senderID
                )
            } finally {
                resetState()
            }
        } else if (userService.getUserByRegistration(botMessenger.sourceID, senderID) == null) {
            regInProrgess = true
            botMessenger.sendMessage(
                "Klar for registrering! Skriv din hemmelige kode.",
                senderID
            )
        } else {
            botMessenger.sendMessage(
                "Du er allerede registrert.",
                senderID
            )
        }
    }

    override fun resetState() {
        regInProrgess = false
    }
}
