package bilboka.messagebot

interface BotMessenger {

    fun sendMessage(message: String, recipientID: String)
    fun sendPostback(options: List<String>, recipientID: String)

}
