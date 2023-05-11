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
    val tekniskeData: TekniskeData,
    val unntak: List<Unntak>
)

data class TekniskeData(
    val akslinger: Akslinger,
    val bremser: Bremser,
    val dekkOgFelg: DekkOgFelg,
    val dimensjoner: Dimensjoner,
    val generelt: Generelt,
    val karosseriOgLasteplan: KarosseriOgLasteplan,
    val miljodata: Miljodata,
    val motorOgDrivverk: MotorOgDrivverk,
    val ovrigeTekniskeData: List<Any>,
    val persontall: Persontall,
    val tilhengerkopling: Tilhengerkopling,
    val vekter: Vekter
)

data class Akslinger(
    val akselGruppe: List<AkselGruppe>,
    val antallAksler: Int
)

data class AkselGruppe(
    val akselListe: AkselListe,
    val id: Int,
    val plasseringAkselGruppe: String,
    val tekniskTillattAkselGruppeLast: Int
)

data class AkselListe(
    val aksel: List<Aksel>
)

data class Aksel(
    val avstandTilNesteAksling: Int,
    val drivAksel: Boolean,
    val id: Int,
    val plasseringAksel: String,
    val sporvidde: Int,
    val tekniskTillattAkselLast: Int
)

data class Bremser(
    val tilhengerBremseforbindelse: List<Any>
)

data class DekkOgFelg(
    val akselDekkOgFelgKombinasjon: List<AkselDekkOgFelgKombinasjon>
)

data class AkselDekkOgFelgKombinasjon(
    val akselDekkOgFelg: List<AkselDekkOgFelg>
)

data class AkselDekkOgFelg(
    val akselId: Int,
    val belastningskodeDekk: String,
    val dekkdimensjon: String,
    val felgdimensjon: String,
    val hastighetskodeDekk: String,
    val innpress: String
)

data class Dimensjoner(
    val bredde: Int,
    val lengde: Int
)

data class Generelt(
    val fabrikant: List<Any>,
    val handelsbetegnelse: List<String>,
    val merke: List<Merke>,
    val tekniskKode: TekniskKode,
    val typebetegnelse: String
)

data class Merke(
    val merke: String,
    val merkeKode: String
)

data class TekniskKode(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
)

data class KarosseriOgLasteplan(
    val antallDorer: List<Any>,
    val dorUtforming: List<Any>,
    val kjennemerketypeBak: Kjennemerketype,
    val kjennemerkestorrelseBak: Kjennemerkestorrelse,
    val kjennemerketypeForan: Kjennemerketype,
    val kjennemerkestorrelseForan: Kjennemerkestorrelse,
    val kjoringSide: String,
    val plasseringFabrikasjonsplate: List<Any>,
    val plasseringUnderstellsnummer: List<Any>,
    val rFarge: List<RFarge>
)

data class Kjennemerketype(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeTypeId: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
)

data class Kjennemerkestorrelse(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeTypeId: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
)

data class RFarge(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeTypeId: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
)

data class Miljodata(
    val miljoOgdrivstoffGruppe: List<MiljoOgDrivstoffGruppe>
)

data class MiljoOgDrivstoffGruppe(
    val drivstoffKodeMiljodata: DrivstoffKode,
    val lyd: Lyd
)

data class DrivstoffKode(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeTypeId: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
)

data class Lyd(
    val standstoy: Int,
    val stoyMalingOppgittAv: StoyMalingOppgittAv,
    val vedAntallOmdreininger: Int
)

data class StoyMalingOppgittAv(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeTypeId: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
)

data class MotorOgDrivverk(
    val girkassetype: Girkassetype,
    val girutvekslingPrGir: List<Any>,
    val hybridKategori: HybridKategori,
    val maksimumHastighet: List<Int>,
    val maksimumHastighetMalt: List<Any>,
    val motor: List<Motor>
)

data class Motor(
    val drivstoff: List<Drivstoff>,
    val slagvolum: Int
)

data class Girkassetype(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeTypeId: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
)

data class HybridKategori(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeTypeId: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
)

data class Drivstoff(
    val drivstoffKode: DrivstoffKode,
    val maksNettoEffekt: Double
)

data class Persontall(
    val sitteplasserForan: Int,
    val sitteplasserTotalt: Int
)

data class Tilhengerkopling(
    val kopling: List<Any>
)

data class Vekter(
    val egenvekt: Int,
    val nyttelast: Int,
    val tillattTaklast: Int,
    val tillattTilhengervektMedBrems: Int,
    val tillattTilhengervektUtenBrems: Int,
    val tillattTotalvekt: Int,
    val tillattVertikalKoplingslast: Int,
    val tillattVogntogvekt: Int,
    val vogntogvektAvhBremsesystem: List<Any>
)

data class Unntak(
    val merknad: List<String>,
    val unntak: UnntakInfo
)

data class UnntakInfo(
    val kodeBeskrivelse: String,
    val kodeNavn: String,
    val kodeVerdi: String,
    val tidligereKodeVerdi: List<Any>
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
