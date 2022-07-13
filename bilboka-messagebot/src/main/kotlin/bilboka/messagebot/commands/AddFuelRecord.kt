package bilboka.messagebot.commands

import bilboka.messagebot.CarBookExecutor

class AddFuelRecord(
    executor: CarBookExecutor
) : CarBookCommand {
    override fun isMatch(message: String): Boolean {
        return false
    }

    override fun execute(message: String): String {
        TODO("Not yet implemented")
    }

}
