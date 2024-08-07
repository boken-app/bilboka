package bilboka.messagebot.commands

import bilboka.core.book.entryClosestTo
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.OdometerUnit.KILOMETERS
import bilboka.core.vehicle.domain.Vehicle
import bilboka.core.vehicle.domain.normaliserTegnkombinasjon
import bilboka.integration.autosys.consumer.KjoretoydataIngenTreffException
import bilboka.integration.autosys.dto.Kjoretoydata
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format
import bilboka.messagebot.formatAsDate
import org.slf4j.LoggerFactory

internal class VehicleInfo(
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val matcher = Regex(
        "(inf|info|kjøretøyinfo)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]

        try {
            vehicleService.getVehicle(vehicleName).apply {
                replyWithInfo(this, conversation)
            }
        } catch (notFound: VehicleNotFoundException) {
            try {
                logger.info("Kjenner ikke til kjøretøy. Prøver Autosys-oppslag på $vehicleName")
                vehicleService.getAutosysKjoretoydataByTegnkombinasjon(vehicleName).apply {
                    replyWithAutosysInfo(this, conversation)
                }
            } catch (e: KjoretoydataIngenTreffException) {
                logger.warn("Ingen treff fra Autosys: ${e.message}", e)
                throw notFound
            }
        }
    }

    private fun replyWithAutosysInfo(kjoretoydata: Kjoretoydata, conversation: Conversation) {
        conversation.sendReply(
            "¯\\_(ツ)_/¯ Ukjent bil i Bilboka \n\n${kjoretoydata.print()}"
        )
        kjoretoydata.kjoretoyId?.kjennemerke?.normaliserTegnkombinasjon()?.also {
            conversation.replyWithOptions(
                "Enda mer?",
                "autosys-dekkogfelg $it" to "Dekk- og felgdata \uD83D\uDEDE",
                "autosys-dimensjon-og-vekt $it" to "Dimensjon og vekt ⚖"
            )
        }
    }

    private fun replyWithInfo(
        vehicle: Vehicle,
        conversation: Conversation
    ) {
        conversation.sendReply(
            "\uD83D\uDE97 \nBil-navn: ${vehicle.name} \n" +
                    "Registreringsnummer: ${vehicle.tegnkombinasjonVisning ?: "(ukjent)"} \n" +
                    "Forbruk: ${
                        vehicle.consumptionLastKm(500)
                            ?.let {
                                "${it.litersPer10Km().format()} liter per mil " +
                                        "(siste ${vehicle.odometerUnit?.convertToKilometers(it.estimatedAt.odometer!! - it.estimatedFrom.odometer!!)} km)"
                            } ?: "(mangler data)"
                    } \n" +
                    "Distansemåleenhet: ${vehicle.odometerUnit} \n" +
                    "Tankvolum: ${vehicle.tankVolume?.let { "$it liter" } ?: "(ukjent)"} \n" +
                    "Drivstofftype: ${vehicle.fuelType ?: "(ukjent)"} \n" +
                    "Alternative navn: ${vehicle.nicknames.joinToString(", ")} \n" +
                    "Antall oppføringer: ${vehicle.bookEntries.count()} \n" +
                    (vehicle.lastOdometerEntry()
                        ?.let { "Sist registrert km-stand: ${it.odometer ?: "-"} (${it.dateTime.formatAsDate()})\n" }
                        ?: " \n") +
                    "Kjørt siste år: ${getDistanceLastYear(vehicle)}"
        )
        conversation.replyWithOptions(
            "Hente mer data fra Autosys?",
            "autosys-data ${vehicle.name}" to "Ja! 🚙"
        )
    }

    private fun getDistanceLastYear(vehicle: Vehicle): String {
        val lastOdometerEntry = vehicle.lastOdometerEntry()
        val yearBeforeEntry = lastOdometerEntry?.run {
            dateTime?.run {
                vehicle.bookEntries.toList()
                    .entryClosestTo(minusYears(1)) { it.odometer != null }
            }
        }

        return yearBeforeEntry?.let {
            val diff = lastOdometerEntry.odometer!!.minus(it.odometer!!)
            if (vehicle.odometerUnit == null) {
                "(mangler enhet)"
            } else {
                "${(vehicle.odometerUnit!!.convertToKilometers(diff))} km " +
                        (if (vehicle.odometerUnit != KILOMETERS) "/ $diff ${vehicle.odometerUnit} " else "") +
                        "(siden ${it.dateTime.formatAsDate()})"
            }
        } ?: "(mangler data)"
    }
}
