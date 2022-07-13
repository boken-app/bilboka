package bilboka.messagebot.commands

class AddFuelRecordCommand : CarBookCommand {
    override fun isMatch(message: String): Boolean {
        return false
    }

    override fun execute(message: String): String {
        TODO("Not yet implemented")
    }

}
