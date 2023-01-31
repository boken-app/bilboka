package bilboka.messagebot.commands

import bilboka.core.user.InvalidRegistrationKeyException
import bilboka.core.user.UserAlreadyRegisteredException
import bilboka.core.user.UserService
import bilboka.messagebot.Conversation
import kotlin.text.RegexOption.IGNORE_CASE

class RegisterUser(
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

    override fun execute(conversation: Conversation, message: String) {
        if (regInProrgess) {
            try {
                userService.register(conversation.getSource(), conversation.senderID, message)
                conversation.registerUser(
                    userService.findUserByRegistration(
                        conversation.getSource(),
                        conversation.senderID
                    ) ?: throw IllegalStateException("Registrerte en bruker men finner den ikke")
                )
                conversation.sendReply(
                    "Du er registrert! 🎉"
                )
            } catch (ex: UserAlreadyRegisteredException) {
                conversation.sendReply(
                    "Du er allerede registrert 😱"
                )
            } catch (ex: InvalidRegistrationKeyException) {
                conversation.sendReply(
                    "Feil kode! 🤨"
                )
            } finally {
                resetState()
            }
        } else if (userService.findUserByRegistration(conversation.getSource(), conversation.senderID) == null) {
            regInProrgess = true
            conversation.sendReply(
                "Klar for registrering! Skriv din hemmelige kode 🗝"
            )
        } else {
            conversation.sendReply(
                "Du er allerede registrert ¯\\_(ツ)_/¯"
            )
        }
    }

    override fun resetState(conversation: Conversation?) {
        regInProrgess = false
    }
}
