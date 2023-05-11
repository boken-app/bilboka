package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.integration.autosys.dto.Kjoretoydata
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand

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
                    "Understellsnr.: ${data.kjoretoyId?.understellsnummer ?: "(ukjent)"} \n" +
                    "Reg.status: ${data.registrering?.registreringsstatus?.kodeBeskrivelse ?: "(ukjent)"} \n" +
                    "Sist godkj. PKK: ${data.registrering?.periodiskKjoretoyKontroll?.sistGodkjent ?: "(ukjent)"} \n" +
                    "PKK-frist: ${data.registrering?.periodiskKjoretoyKontroll?.kontrollfrist ?: "(ukjent)"} \n"
        )
    }

}
