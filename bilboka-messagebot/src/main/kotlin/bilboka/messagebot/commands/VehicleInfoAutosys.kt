package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.integration.autosys.dto.Godkjenning
import bilboka.integration.autosys.dto.Kjoretoydata
import bilboka.integration.autosys.dto.Registreringsstatus
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format

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
                    "Første reg. Norge: ${data.forstegangsregistrering?.registrertForstegangNorgeDato ?: "(ukjent)"} \n" +
                    "Egenvekt: ${data.godkjenning?.tekniskGodkjenning?.tekniskeData?.vekter?.egenvekt ?: "(ukjent)"} \n" +
                    "Nyttelast: ${data.godkjenning?.tekniskGodkjenning?.tekniskeData?.vekter?.nyttelast ?: "(ukjent)"} \n" +
                    "Reg. bevaringsverdig: ${data.godkjenning?.hasBevaringsverdig()?.toText() ?: "(ukjent)"} \n" +
                    "Sist godkj. PKK: ${data.periodiskKjoretoyKontroll?.sistGodkjent?.format() ?: "(ukjent)"} \n" +
                    "PKK-frist: ${data.periodiskKjoretoyKontroll?.kontrollfrist?.format() ?: "(ukjent)"} \n"
        )
    }


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
