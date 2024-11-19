package bilboka.core

import bilboka.core.book.Book
import bilboka.core.book.domain.EventType
import bilboka.core.report.ReportGenerator
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.Vehicle
import bilboka.integration.autosys.AutosysProperties
import bilboka.integration.autosys.consumer.AkfDatautleveringConsumer
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(classes = [VehicleService::class, TripService::class, Book::class, ReportGenerator::class, AkfDatautleveringConsumer::class, AutosysProperties::class])
@ExtendWith(SpringExtension::class)
@TestPropertySource(
    properties = [
        "autosys.akfDatautleveringUrl=test://url",
        "autosys.apiKey=testApiKey",
    ]
) // TODO er ikke sikkert alt dette trengs. Samme med test property fil
class TripsIT : H2Test() {

    @Autowired
    lateinit var tripService: TripService

    @Autowired
    lateinit var vehicleService: VehicleService

    lateinit var vehicle: Vehicle

    @BeforeEach
    fun createVehicle() {
        vehicle = vehicleService.addVehicle("Xc70", fuelType = FuelType.DIESEL)
    }

    @Test
    fun startAndEndTrip_succeeds() {
        val trip = tripService.startTrip(vehicle, "En test-tur", 100)
        assertThat(trip.odometerStart).isEqualTo(100)
        assertThat(trip.dateTimeStart).isNotNull()
        assertThat(trip.odometerEnd).isNull()
        assertThat(trip.dateTimeEnd).isNull()
        assertThat(trip.tripName).isEqualTo("En test-tur")

        trip.end(200)
        assertThat(trip.odometerEnd).isEqualTo(200)
        assertThat(trip.dateTimeEnd).isNotNull()
        assertThat(trip.dateTimeEnd).isAfterOrEqualTo(trip.dateTimeStart)
    }

    @Test
    fun getActiveTrip() {
        val createdTrip = tripService.startTrip(vehicle, "En test-tur", 100)
        val activeTrip = tripService.getActiveTrip(vehicle)

        assertThat(activeTrip?.id).isEqualTo(createdTrip.id)

        activeTrip?.end(200)

        assertThat(tripService.getActiveTrip(vehicle)).isNull()
    }

    @Test
    fun endActiveTrip() {
        val createdTrip = tripService.startTrip(vehicle, "En test-tur", 100)
        val endedTrip = tripService.endCurrentTrip(vehicle, 200)

        assertThat(endedTrip?.id).isEqualTo(createdTrip.id)
        assertThat(tripService.getActiveTrip(vehicle)).isNull()
    }

    @Test
    fun entriesAreCreated() {
        tripService.startTrip(vehicle, "En test-tur", 100)
        tripService.endCurrentTrip(vehicle, 200)

        transaction {
            assertThat(vehicle.bookEntries.find { it.event == EventType.TRIP_START && it.odometer == 100 }).isNotNull
            assertThat(vehicle.bookEntries.find { it.event == EventType.TRIP_END && it.odometer == 200 }).isNotNull
        }
    }

}
