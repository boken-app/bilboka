package bilboka.web.resource

import bilboka.client.BilbokaDataPoint
import bilboka.client.BookEntryDto
import bilboka.core.vehicle.VehicleService
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
@RequestMapping("vehicles/{id}/datapoints")
class DataPointsResource(
    val vehicleService: VehicleService
) {
    @GetMapping()
    fun datapoints(@PathVariable id: String): ResponseEntity<List<BilbokaDataPoint>> {
        return try {
            transaction {
                vehicleService.getVehicleById(id.toInt()).let {
                    ResponseEntity.ok(emptyList()) // TODO lage datapoints
                }
            }
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("sample")
    fun sample(@PathVariable id: String): ResponseEntity<List<BilbokaDataPoint>> {
        val dataSample = dataSample()
        return if (dataSample.containsKey(id)) {
            ResponseEntity.ok(dataSample[id]!!)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun dataSample(): Map<String, List<BilbokaDataPoint>> {
        return (1..3).associate { key ->
            key.toString() to listOf(
                BilbokaDataPoint(
                    dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0).plusDays(Random.nextLong(0, 30)),
                    odometer = Random.nextInt(100000, 200000),
                    odometerKilometers = Random.nextInt(100000, 200000),
                    amount = Random.nextDouble(20.0, 60.0),
                    costNOK = Random.nextDouble(300.0, 700.0),
                    priceNOK = Random.nextDouble(9.0, 12.0),
                    isFullTank = Random.nextBoolean(),
                    estimatedConsumptionLitersPer10Km = Random.nextDouble(5.0, 15.0),
                    estimatedRemainingFuel = Random.nextDouble(10.0, 60.0),
                    averageKilometersPerDay = Random.nextDouble(50.0, 150.0),
                    sourceEntryFirst = generateBookEntryDto("1", "FUEL", 123456),
                    sourceEntryLast = generateBookEntryDto("2", "FUEL", 123556)
                )
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
