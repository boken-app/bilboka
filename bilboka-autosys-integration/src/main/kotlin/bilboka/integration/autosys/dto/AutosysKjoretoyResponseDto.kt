package bilboka.integration.autosys.dto

data class AutosysKjoretoyResponseDto(
    val kjoretoydataListe: List<Kjoretoydata> = emptyList()
)

data class Kjoretoydata(
    val kjoretoyId: KjoretoyId? = null,
    val forstegangsregistrering: Forstegangsregistrering? = null,
    val registrering: Registrering? = null,
    val godkjenning: Godkjenning? = null,
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

data class Godkjenning(
    val tekniskGodkjenning: TekniskGodkjenning
)

data class TekniskGodkjenning(
    val tekniskeData: TekniskeData,
    val unntak: List<Unntak>
)

data class TekniskeData(
    val akslinger: Akslinger? = null,
    val bremser: Bremser? = null,
    val dekkOgFelg: DekkOgFelg? = null,
    val dimensjoner: Dimensjoner? = null,
    val generelt: Generelt? = null,
    val karosseriOgLasteplan: KarosseriOgLasteplan? = null,
    val miljodata: Miljodata? = null,
    val motorOgDrivverk: MotorOgDrivverk? = null,
    val ovrigeTekniskeData: List<Any>? = emptyList(),
    val persontall: Persontall? = null,
    val tilhengerkopling: Tilhengerkopling? = null,
    val vekter: Vekter? = null
)

data class Akslinger(
    val akselGruppe: List<AkselGruppe>? = emptyList(),
    val antallAksler: Int? = null
)

data class AkselGruppe(
    val akselListe: AkselListe? = null,
    val id: Int? = null,
    val plasseringAkselGruppe: String? = null,
    val tekniskTillattAkselGruppeLast: Int? = null
)

data class AkselListe(
    val aksel: List<Aksel> = emptyList()
)

data class Aksel(
    val avstandTilNesteAksling: Int? = null,
    val drivAksel: Boolean? = null,
    val id: Int? = null,
    val plasseringAksel: String? = null,
    val sporvidde: Int? = null,
    val tekniskTillattAkselLast: Int? = null
)

data class Bremser(
    val tilhengerBremseforbindelse: List<Any> = emptyList()
)

data class DekkOgFelg(
    val akselDekkOgFelgKombinasjon: List<AkselDekkOgFelgKombinasjon> = emptyList()
)

data class AkselDekkOgFelgKombinasjon(
    val akselDekkOgFelg: List<AkselDekkOgFelg> = emptyList()
)

data class AkselDekkOgFelg(
    val akselId: Int? = null,
    val belastningskodeDekk: String? = null,
    val dekkdimensjon: String? = null,
    val felgdimensjon: String? = null,
    val hastighetskodeDekk: String? = null,
    val innpress: String? = null
)

data class Dimensjoner(
    val bredde: Int? = null,
    val lengde: Int? = null
)

data class Generelt(
    val fabrikant: List<Any> = emptyList(),
    val handelsbetegnelse: List<String> = emptyList(),
    val merke: List<Merke> = emptyList(),
    val tekniskKode: TekniskKode? = null,
    val typebetegnelse: String? = null
)

data class Merke(
    val merke: String? = null,
    val merkeKode: String? = null
)

data class TekniskKode(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any> = emptyList()
)

data class KarosseriOgLasteplan(
    val antallDorer: List<Any> = emptyList(),
    val dorUtforming: List<Any> = emptyList(),
    val kjennemerketypeBak: Kjennemerketype? = null,
    val kjennemerkestorrelseBak: Kjennemerkestorrelse? = null,
    val kjennemerketypeForan: Kjennemerketype? = null,
    val kjennemerkestorrelseForan: Kjennemerkestorrelse? = null,
    val kjoringSide: String? = null,
    val plasseringFabrikasjonsplate: List<Any> = emptyList(),
    val plasseringUnderstellsnummer: List<Any> = emptyList(),
    val rFarge: List<RFarge> = emptyList()
)

data class Kjennemerketype(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeTypeId: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any>? = null
)

data class Kjennemerkestorrelse(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeTypeId: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any>? = null
)

data class RFarge(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeTypeId: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any>? = null
)

data class Miljodata(
    val miljoOgdrivstoffGruppe: List<MiljoOgDrivstoffGruppe> = emptyList()
)

data class MiljoOgDrivstoffGruppe(
    val drivstoffKodeMiljodata: DrivstoffKode? = null,
    val lyd: Lyd? = null
)

data class DrivstoffKode(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeTypeId: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any>? = null
)

data class Lyd(
    val standstoy: Int? = null,
    val stoyMalingOppgittAv: StoyMalingOppgittAv? = null,
    val vedAntallOmdreininger: Int? = null
)

data class StoyMalingOppgittAv(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeTypeId: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any>? = null
)

data class MotorOgDrivverk(
    val girkassetype: Girkassetype? = null,
    val girutvekslingPrGir: List<Any> = emptyList(),
    val hybridKategori: HybridKategori? = null,
    val maksimumHastighet: List<Int> = emptyList(),
    val maksimumHastighetMalt: List<Any> = emptyList(),
    val motor: List<Motor> = emptyList()
)

data class Motor(
    val drivstoff: List<Drivstoff> = emptyList(),
    val slagvolum: Int? = null
)

data class Girkassetype(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeTypeId: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any>? = null
)

data class HybridKategori(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeTypeId: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any>? = null
)

data class Drivstoff(
    val drivstoffKode: DrivstoffKode? = null,
    val maksNettoEffekt: Double? = null
)

data class Persontall(
    val sitteplasserForan: Int? = null,
    val sitteplasserTotalt: Int? = null
)

data class Tilhengerkopling(
    val kopling: List<Any> = emptyList()
)

data class Vekter(
    val egenvekt: Int? = null,
    val nyttelast: Int? = null,
    val tillattTaklast: Int? = null,
    val tillattTilhengervektMedBrems: Int? = null,
    val tillattTilhengervektUtenBrems: Int? = null,
    val tillattTotalvekt: Int? = null,
    val tillattVertikalKoplingslast: Int? = null,
    val tillattVogntogvekt: Int? = null,
    val vogntogvektAvhBremsesystem: List<Any> = emptyList()
)

data class Unntak(
    val merknad: List<String> = emptyList(),
    val unntak: UnntakInfo? = null
)

data class UnntakInfo(
    val kodeBeskrivelse: String? = null,
    val kodeNavn: String? = null,
    val kodeVerdi: String? = null,
    val tidligereKodeVerdi: List<Any>? = null
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
