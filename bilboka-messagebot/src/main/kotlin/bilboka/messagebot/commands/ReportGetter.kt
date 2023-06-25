package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.*

internal class ReportGetter(val book: Book, val vehicleService: VehicleService, userService: UserService) :
    CarBookCommand(userService) {
    private val rapportMatcher = Regex("rapport", RegexOption.IGNORE_CASE)
    private val maintenanceMatcher = Regex("vedlikehold|vedl", RegexOption.IGNORE_CASE)
    private val yearRegex = Regex("\\d{4}")

    override fun isMatch(message: String): Boolean {
        return rapportMatcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val state = conversation.withdrawClaim<State>(this)?.apply {
            recordProvidedData(message) {
                when (this) {
                    State.ReportDataTypes.VEHICLE ->
                        VEHICLE_REGEX.find(message)
                            ?.let { vehicleService.getVehicle(it.value) }
                    else -> {}
                }
            }
        } ?: firstAttempt(message)

        state.complete()?.run {
            if (isMaint.content as Boolean) {
                replyWithReportOrNothing(
                    conversation,
                    filename = "vedlikeholdsrapport_${(vehicle.content as Vehicle).name.lowercase().replace(" ", "_")}"
                ) { book.getMaintenanceReport(vehicle.content as Vehicle) }
            } else {
                replyWithReportOrNothing(
                    conversation,
                    filename = "rapport${year ?: ""}_${(vehicle.content as Vehicle).name.lowercase().replace(" ", "_")}"
                ) { book.getReport(vehicle.content as Vehicle, year.content as Int?) }
            }
        } ?: askForNext(conversation, state)
    }

    private fun firstAttempt(message: String): State {
        val extractor = StringMatchExtractor(message)
            .apply { extract(rapportMatcher) {} }
        val isMaintenance = extractor
            .extract(maintenanceMatcher) { } != null
        val vehicle = extractor
            .extract(VEHICLE_REGEX) {
                vehicleService.findVehicle(it)
            }
        val year = extractor
            .extract(yearRegex) { it.toInt() }

        return State().apply {
            this.vehicle.content = vehicle
            this.year.content = year
            this.isMaint.content = isMaintenance
        }
    }

    private fun replyWithReportOrNothing(conversation: Conversation, filename: String, getReport: () -> ByteArray?) {
        getReport()?.run {
            conversation.sendPdf(this, filename)
        } ?: conversation.sendReply("Ingenting å rapportere ¯\\_(ツ)_/¯")
    }

    class State : DataCollectingChatState<State.ReportDataTypes>() {
        enum class ReportDataTypes { VEHICLE, YEAR, IS_MAINT }

        override val collectedData = linkedMapOf(
            Pair(ReportDataTypes.VEHICLE, QueryableDataItem("Hvilken bil? \uD83D\uDE97")),
            Pair(ReportDataTypes.YEAR, QueryableDataItem("Hvilket år?")),
            Pair(ReportDataTypes.IS_MAINT, QueryableDataItem("Vedlikeholdsrapport?")),
        )
        val vehicle = collectedData[ReportDataTypes.VEHICLE]!!
        val year = collectedData[ReportDataTypes.YEAR]!!
        val isMaint = collectedData[ReportDataTypes.IS_MAINT]!!

        override fun complete(): State? {
            if (vehicle.isMissing()) {
                return null
            }
            return this
        }
    }
}
