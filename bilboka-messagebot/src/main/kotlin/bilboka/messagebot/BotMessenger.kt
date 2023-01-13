package bilboka.messagebot

interface BotMessenger {
    val sourceID: String

    fun sendMessage(message: String, recipientID: String)
    fun sendPostback(options: List<String>, recipientID: String)

}
