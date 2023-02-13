package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format

internal class FuelPriceStatistics(
    private val book: Book,
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "(prisstatistikk|statistikk|priser|drivstoffpriser|stats)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        // TODO: vise pr. diesel og bensin
        val lastFuelPrices = book.getLastFuelPrices(6)
        val lastFuelPricesStr = lastFuelPrices
            .map {
                "${it.first.format()}: ${it.second.format()} kr/l"
            }

        conversation.sendReply(
            "Siste registrerte drivstoff-priser \n" +
                    "Gjennomsnitt: ${lastFuelPrices.map { it.second }.toTypedArray().average().format()} kr/l \n" +
                    "Siste 6 priser: \n${lastFuelPricesStr.joinToString(" \n")}"
        )
    }

}
