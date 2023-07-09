package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.normaliserTegnkombinasjon
import bilboka.integration.autosys.dto.Godkjenning
import bilboka.integration.autosys.dto.Kjoretoydata
import bilboka.integration.autosys.dto.Registreringsstatus
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal class VehicleInfoAutosys(
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "(autosys-data)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]

        vehicleService.getAutosysKjoretoydata(vehicleName).apply {
            replyWithInfo(this, conversation)
        }
    }

    private fun replyWithInfo(
        data: Kjoretoydata,
        conversation: Conversation
    ) {
        conversation.sendReply(
            "\uD83D\uDE97 Kjøretøydata fra Autosys \n" +
                    "Kjennemerke: ${data.kjoretoyId?.kjennemerke ?: "(ukjent)"} \n" +
                    "Unr.: ${data.kjoretoyId?.understellsnummer ?: "(ukjent)"} \n" +
                    "Reg.status: ${data.registrering?.registreringsstatus?.toText() ?: "(ukjent)"} \n" +
                    "Første reg. Norge: ${data.forstegangsregistrering?.registrertForstegangNorgeDato?.format() ?: "(ukjent)"} \n" +
                    "Egenvekt: ${data.godkjenning?.tekniskGodkjenning?.tekniskeData?.vekter?.egenvekt?.kg() ?: "(ukjent)"} \n" +
                    "Nyttelast: ${data.godkjenning?.tekniskGodkjenning?.tekniskeData?.vekter?.nyttelast?.kg() ?: "(ukjent)"} \n" +
                    "Hengervekt (m/brems): ${data.godkjenning?.tekniskGodkjenning?.tekniskeData?.vekter?.tillattTilhengervektMedBrems?.kg() ?: "(ukjent)"} \n" +
                    "Lengde: ${data.godkjenning?.tekniskGodkjenning?.tekniskeData?.dimensjoner?.lengde?.formatFromMm() ?: "(ukjent)"} \n" +
                    "Reg. bevaringsverdig: ${data.godkjenning?.hasBevaringsverdig()?.toText() ?: "(ukjent)"} \n" +
                    "Sist godkj. PKK: ${data.periodiskKjoretoyKontroll?.sistGodkjent?.format() ?: "(ukjent)"} \n" +
                    "PKK-frist: ${data.periodiskKjoretoyKontroll?.kontrollfrist?.formattedDeadlineWithEmoji() ?: "(ukjent)"} \n"
        )
        data.kjoretoyId?.kjennemerke?.normaliserTegnkombinasjon()?.also {
            conversation.replyWithOptions(
                "Enda mer?",
                "autosys-dekkogfelg $it" to "Dekk- og felgdata ⚫"
            )
        }
    }
}

private fun Number.kg(): String {
    return "$this kg"
}

private fun Int.formatFromMm(): String {
    return "${(this / 1000.0).format()} m"
}

private fun Godkjenning.hasBevaringsverdig(): Boolean {
    return tekniskGodkjenning.unntak.any { it.unntak?.kodeVerdi == "BEVARINGSVERDIG_MED_BRUKSBEGRENSNING" }
}

private fun Boolean.toText(): String {
    return if (this) "Ja" else "Nei"
}

private fun Registreringsstatus.toText(): String {
    return "$kodeBeskrivelse ${if (kodeVerdi == "AVREGISTRERT") "🔴" else if (kodeVerdi == "REGISTRERT") "🟢" else ""}"
}

private fun LocalDate.formattedDeadlineWithEmoji(): String {
    val remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), this)

    return "${format()} ${
        when {
            remainingDays >= 700 -> "\uD83C\uDF89" // Party emoji (2 years or more)
            remainingDays >= 300 -> "\uD83D\uDE03" // Happy emoji 
            remainingDays >= 120 -> "🤠" // (120 days or more)
            remainingDays >= 60 -> "🤔" // (2 months or more)
            remainingDays >= 30 -> "\uD83D\uDE31" // Stressed emoji (1 month or more)
            remainingDays >= 7 -> "\uD83D\uDE33" // Worried emoji (1 week or more)
            remainingDays >= 1 -> "\uD83D\uDE29" // Anxious emoji (1 day or more)
            remainingDays == 0L -> "\uD83D\uDE16" // Nervous emoji (deadline is today)
            else -> "\uD83D\uDE2D" // Sad emoji (deadline has passed)
        }
    }"
}
