package bilboka.web.resource

import bilboka.client.BilbokaDataPoint
import bilboka.client.BookEntryDto
import bilboka.client.VehicleResponse
import bilboka.core.vehicle.VehicleMissingDataException
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.integration.autosys.consumer.KjoretoydataFeiletException
import bilboka.integration.autosys.dto.hasBevaringsverdig
import bilboka.web.converter.EntryConverter.toDto
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("vehicles")
class VehicleResource(
    val vehicleService: VehicleService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(VehicleResource::class.java)
    }

    @GetMapping()
    fun vehicles(): ResponseEntity<List<VehicleResponse>> {
        return transaction {
            vehicleService.getVehicles()
                .map { it.toSlimResponse() }
        }.let { ResponseEntity.ok(it) }
    }

    @GetMapping("{id}")
    fun vehicleById(
        @PathVariable id: String // TODO mulighet for å hente på kjennemerke
    ): ResponseEntity<VehicleResponse> {
        return try {
            transaction {
                vehicleService.getVehicleById(id.toInt())
                    .run { toRichResponse() }
                    .let { ResponseEntity.ok(it) }
                    ?: ResponseEntity.notFound().build()
            }
        } catch (e: NumberFormatException) {
            ResponseEntity.badRequest().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    private fun Vehicle.toSlimResponse(): VehicleResponse {
        return VehicleResponse(
            id = id.value.toString(),
            name = name,
            tegnkombinasjon = tegnkombinasjonNormalisert(),
            tegnkombinasjonVisning = tegnkombinasjonVisning,
            tegnkombinasjonNormalisert = tegnkombinasjonNormalisert(),
            odometerUnit = odometerUnit?.name,
            fuelType = fuelType?.name,
            tankVolume = tankVolume,
            lastOdometer = lastOdometer(),
            entriesCount = bookEntries.count().toInt()
        )
    }

    private fun Vehicle.toRichResponse(): VehicleResponse {
        val autosysKjoretoydata = kjoretoydataIfFound(this)
        return VehicleResponse(
            id = id.value.toString(),
            name = name,
            tegnkombinasjon = tegnkombinasjonNormalisert(),
            tegnkombinasjonVisning = tegnkombinasjonVisning,
            tegnkombinasjonNormalisert = tegnkombinasjonNormalisert(),
            odometerUnit = odometerUnit?.name,
            fuelType = fuelType?.name,
            tankVolume = tankVolume,
            merke = autosysKjoretoydata?.godkjenning?.tekniskGodkjenning?.tekniskeData?.generelt?.merke?.firstOrNull()?.merke,
            modell = autosysKjoretoydata?.godkjenning?.tekniskGodkjenning?.tekniskeData?.generelt?.handelsbetegnelse?.firstOrNull(),
            regStatus = autosysKjoretoydata?.registrering?.registreringsstatus?.kodeVerdi ?: "UKJENT",
            understellsnummer = autosysKjoretoydata?.kjoretoyId?.understellsnummer,
            sistePKK = autosysKjoretoydata?.periodiskKjoretoyKontroll?.sistGodkjent,
            fristPKK = autosysKjoretoydata?.periodiskKjoretoyKontroll?.kontrollfrist,
            egenvekt = autosysKjoretoydata?.godkjenning?.tekniskGodkjenning?.tekniskeData?.vekter?.egenvekt,
            nyttelast = autosysKjoretoydata?.godkjenning?.tekniskGodkjenning?.tekniskeData?.vekter?.nyttelast,
            hengervektMBrems = autosysKjoretoydata?.godkjenning?.tekniskGodkjenning?.tekniskeData?.vekter?.tillattTilhengervektMedBrems,
            lengde = autosysKjoretoydata?.godkjenning?.tekniskGodkjenning?.tekniskeData?.dimensjoner?.lengde,
            regBevaringsverdig = autosysKjoretoydata?.godkjenning?.hasBevaringsverdig() == true,
            lastOdometer = lastOdometer(),
            lastOdometerKilometers = lastOdometer()?.let { odometerUnit?.convertToKilometers(it) },
            averageFuelConsumption = lastConsumptionEstimate()?.let {
                it.takeIf { it.estimatedAt.dateTime != null }?.run {
                    BilbokaDataPoint(
                        dateTime = estimatedAt.dateTime!!,
                        sourceEntryFirst = estimatedFrom.toDto(odometerUnit),
                        sourceEntryLast = estimatedAt.toDto(odometerUnit),
                        estimatedConsumptionLitersPer10Km = lastConsumptionEstimate()?.litersPer10Km(),
                    )
                }
            },
            entriesCount = bookEntries.count().toInt()
        )
    }

    @GetMapping("sample")
    fun sample(): ResponseEntity<List<VehicleResponse>> {
        return ResponseEntity.ok(vehiclesSample().values.toList())
    }

    @GetMapping("{id}/sample")
    fun sampleById(
        @PathVariable id: String
    ): ResponseEntity<VehicleResponse> {
        return vehiclesSample()[id]?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    private fun kjoretoydataIfFound(it: Vehicle) = try {
        vehicleService.getAutosysKjoretoydata(it.name)
    } catch (e: KjoretoydataFeiletException) {
        logger.warn("Autosys-oppslag feilet ved henting av kjøretøy ${it.name}: ${e.message}", e)
        null
    } catch (e: VehicleMissingDataException) {
        logger.info(e.message, e)
        null
    }

    private fun vehiclesSample() = mapOf(
        "1" to VehicleResponse(
            id = "1",
            name = "Testbil 1",
            tegnkombinasjonVisning = "AB 12367",
            tegnkombinasjonNormalisert = "AB12367",
            odometerUnit = "km",
            fuelType = "BENSIN",
            tankVolume = 50,
            regStatus = "REGISTRERT",
            lastOdometer = 234567,
            entriesCount = 0
        ),
        "2" to VehicleResponse(
            id = "2",
            name = "Testbil 2",
            tegnkombinasjonVisning = "AB 12345",
            tegnkombinasjonNormalisert = "AB12345",
            odometerUnit = "mi",
            fuelType = "DIESEL",
            tankVolume = 70,
            regStatus = "AVREGISTRERT",
            lastOdometer = 123456,
            understellsnummer = "12345678901234567",
            sistePKK = LocalDate.of(2020, 1, 1),
            fristPKK = LocalDate.of(2021, 1, 1),
            regBevaringsverdig = true,
            egenvekt = 2000,
            nyttelast = 1000,
            hengervektMBrems = 2000,
            lengde = 500,
            entriesCount = 10,
            lastYearlyDifference = BilbokaDataPoint(
                dateTime = LocalDate.of(2020, 1, 1).atStartOfDay(),
                sourceEntryFirst = BookEntryDto(
                    id = "1",
                    type = "FUEL",
                    dateTime = LocalDate.of(2020, 1, 1).atStartOfDay(),
                    odometer = 123456,
                    odometerKilometers = 123456,
                    amount = 50.0,
                    costNOK = 500.0,
                ),
                sourceEntryLast = BookEntryDto(
                    id = "1",
                    type = "FUEL",
                    dateTime = LocalDate.of(2021, 1, 1).atStartOfDay(),
                    odometer = 123656,
                    odometerKilometers = 123456,
                    amount = 50.0,
                    costNOK = 500.0,
                )
            ),
            averageFuelConsumption = BilbokaDataPoint(
                dateTime = LocalDate.of(2020, 1, 1).atStartOfDay(),
                sourceEntryFirst = BookEntryDto(
                    id = "1",
                    type = "FUEL",
                    dateTime = LocalDate.of(2020, 1, 1).atStartOfDay(),
                    odometer = 123456,
                    odometerKilometers = 123456,
                    amount = 50.0,
                    costNOK = 500.0,
                ),
                sourceEntryLast = BookEntryDto(
                    id = "1",
                    type = "FUEL",
                    dateTime = LocalDate.of(2021, 1, 1).atStartOfDay(),
                    odometer = 123656,
                    odometerKilometers = 123456,
                    amount = 50.0,
                    costNOK = 500.0,
                ),
                estimatedConsumptionLitersPer10Km = 1.5,
            )
        ),
        "3" to VehicleResponse(
            id = "3",
            name = "Testbil 3",
            tegnkombinasjonNormalisert = "AB12389",
            tegnkombinasjon = "AB12389",
            tegnkombinasjonVisning = "AB 12389",
            odometerUnit = "km",
            fuelType = "DIESEL",
            tankVolume = 65,
            regStatus = "REGISTRERT",
            understellsnummer = "12345678901234567",
            sistePKK = LocalDate.of(2020, 1, 1),
            fristPKK = LocalDate.of(2021, 1, 1),
            regBevaringsverdig = true,
            egenvekt = 2000,
            nyttelast = 1000,
            hengervektMBrems = 2000,
            lengde = 500,
            entriesCount = 34
        ),
    )

}
