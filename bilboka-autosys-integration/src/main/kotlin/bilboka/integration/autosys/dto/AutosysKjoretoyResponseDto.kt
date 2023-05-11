package bilboka.integration.autosys.dto

data class AutosysKjoretoyResponseDto(
    val kjoretoydataListe: List<Kjoretoydata> = emptyList()
)

data class Kjoretoydata(
    val kjoretoyId: KjoretoyId? = null,
    val forstegangsregistrering: Forstegangsregistrering? = null,
    val registrering: Registrering? = null,
    val periodiskKjoretoyKontroll: PeriodiskKjoretoyKontroll? = null
)

data class KjoretoyId(
    val kjennemerke: String? = null,
    val understellsnummer: String? = null
)

data class Forstegangsregistrering(
    val registrertForstegangNorgeDato: String? = null
)

data class Registrering(
    val fomTidspunkt: String? = null,
    val kjoringensArt: KjoringensArt? = null,
    val registreringsstatus: Registreringsstatus? = null,
    val registrertForstegangPaEierskap: String? = null,
)

data class KjoringensArt(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<String> = emptyList()
)

data class Registreringsstatus(
    val kodeBeskrivelse: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<String> = emptyList()
)

data class PeriodiskKjoretoyKontroll(
    val kontrollfrist: String? = null,
    val sistGodkjent: String? = null
)
