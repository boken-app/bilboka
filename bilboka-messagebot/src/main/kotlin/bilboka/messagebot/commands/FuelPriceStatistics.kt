package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.core.vehicle.domain.FuelType
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format
import java.time.LocalDate

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
        val lastDieselPrices = book.getLastFuelPrices(4, FuelType.DIESEL)
        val lastBensinPrices = book.getLastFuelPrices(4, FuelType.BENSIN)
        val toDisplayString: (Pair<LocalDate, Double>) -> String =
            { "${it.first.format()}: ${it.second.format()} kr/l" }

        conversation.sendReply(
            "Siste registrerte drivstoff-priser" +
                    "\n\nDieselpriser (snitt ${lastDieselPrices.averaged().format()} kr/l): \n" +
                    lastDieselPrices.joinToString(" \n", transform = toDisplayString) +
                    "\n\nBensinpriser (snitt ${lastBensinPrices.averaged().format()} kr/l): \n" +
                    lastBensinPrices.joinToString(" \n", transform = toDisplayString)
        )
    }
}

fun List<Pair<LocalDate, Double>>.averaged(): Double {
    return this.map { it.second }.toTypedArray().average()
}
