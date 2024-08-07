package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.integration.autosys.dto.Kjoretoydata
import bilboka.integration.autosys.dto.TekniskeData
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand

internal class VehicleInfoDimensjoner(
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "(autosys-dimensjon-og-vekt)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val values = matcher.find(message)!!.groupValues
        val tegnkombinasjon = values[2]

        vehicleService.getAutosysKjoretoydataByTegnkombinasjon(tegnkombinasjon).apply {
            replyWithInfo(this, conversation)
        }
    }

    private fun replyWithInfo(
        data: Kjoretoydata,
        conversation: Conversation
    ) {
        conversation.sendReply(
            "\uD83D\uDE97 Dimensjoner og vekter for " +
                    "${
                        data.kjoretoyId?.kjennemerke ?: data.kjoretoyId?.understellsnummer ?: "(ukjent)"
                    } \n" +
                    "${
                        data.godkjenning?.tekniskGodkjenning?.tekniskeData
                            ?.writeDimensjonerOgVekter()
                            ?: "(ingen data)"
                    } \n"
        )
    }
}

fun TekniskeData.writeDimensjonerOgVekter(): String {
    return "Lengde: ${dimensjoner?.lengde?.formatFromMm() ?: "(ukjent)"} \n" +
            "Bredde: ${dimensjoner?.bredde?.formatFromMm() ?: "(ukjent)"} \n" +
            "Sitteplasser foran: ${persontall?.sitteplasserForan ?: "(ukjent)"}" +
            "Sitteplasser totalt: ${persontall?.sitteplasserTotalt ?: "(ukjent)"}" +
            "Egenvekt: ${vekter?.egenvekt?.kg() ?: "(ukjent)"}" +
            "Nyttelast: ${vekter?.nyttelast?.kg() ?: "(ukjent)"}" +
            "Tillatt totalvekt: ${vekter?.tillattTotalvekt?.kg() ?: "(ukjent)"}" +
            "Tillatt taklast: ${vekter?.tillattTaklast?.kg() ?: "(ukjent)"}" +
            "Tillatt vogntogvekt: ${vekter?.tillattVogntogvekt?.kg() ?: "(ukjent)"}" +
            "Tillatt hengervekt m/ brems: ${vekter?.tillattTilhengervektMedBrems?.kg() ?: "(ukjent)"}" +
            "Tillatt hengervekt u/ brems: ${vekter?.tillattTilhengervektUtenBrems?.kg() ?: "(ukjent)"}" +
            "Tillatt vertikal koplinglast: ${vekter?.tillattVertikalKoplingslast?.kg() ?: "(ukjent)"}"
}
