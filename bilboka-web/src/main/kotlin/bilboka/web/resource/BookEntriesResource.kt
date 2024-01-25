package bilboka.web.resource

import bilboka.client.BookEntryDto
import bilboka.core.vehicle.VehicleService
import bilboka.web.converter.EntryConverter.toDto
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import kotlin.random.Random

@RestController
@RequestMapping("vehicles/{id}/entries")
class BookEntriesResource(
    val vehicleService: VehicleService
) {
    @GetMapping()
    fun entries(@PathVariable id: String): ResponseEntity<List<BookEntryDto>> {
        return try {
            transaction {
                vehicleService.getVehicleById(id.toInt()).let {
                    ResponseEntity.ok(it.bookEntries.map { entry ->
                        entry.toDto(it.odometerUnit)
                    })
                }
            }
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("sample")
    fun sample(@PathVariable id: String): ResponseEntity<List<BookEntryDto>> {
        val dataSample = dataSample()
        return if (dataSample.containsKey(id)) {
            ResponseEntity.ok(dataSample[id]!!)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun dataSample(): Map<String, List<BookEntryDto>> {
        return (1..3).associate { key ->
            key.toString() to listOf(
                generateBookEntryDto(key.toString(), "FUEL", 12345 + key),
            )
        }
    }

    private fun generateBookEntryDto(id: String, type: String, odometer: Int): BookEntryDto {
        return BookEntryDto(
            id = id,
            type = type,
            dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0).plusDays(Random.nextLong(0, 30)),
            odometer = odometer + Random.nextInt(0, 1000),
            odometerKilometers = odometer + Random.nextInt(0, 1000),
            amount = Random.nextDouble(20.0, 60.0),
            costNOK = Random.nextDouble(300.0, 700.0),
            isFullTank = Random.nextBoolean(),
            maintenanceItem = null,
            event = null,
            comment = null
        )
    }

}
