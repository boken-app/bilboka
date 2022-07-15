package bilboka.messagebot.commands

import bilboka.messagebot.BotMessenger

abstract class CarBookCommand(private val botMessenger: BotMessenger) {

    abstract fun isMatch(message: String): Boolean
    abstract fun execute(senderID: String, message: String)
    abstract fun resetState()

}