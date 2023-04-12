package bilboka.messagebot

interface BotMessenger {
    val sourceID: String

    fun sendMessage(message: String, recipientID: String)
    fun sendOptions(message: String, options: List<Pair<String, String>>, recipientID: String)
    fun sendPdf(file: ByteArray, fileName: String, recipientID: String)
}
