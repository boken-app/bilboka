package bilboka.messagebot

import java.io.File

interface BotMessenger {
    val sourceID: String

    fun sendMessage(message: String, recipientID: String)
    fun sendPostback(options: List<String>, recipientID: String)
    fun sendFile(file: File, recipientID: String)
}
