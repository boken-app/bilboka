package bilboka.core

import bilboka.core.book.Book
import bilboka.core.book.BookEntryChronologyException
import bilboka.core.book.OdometerShouldNotBeDecreasingException
import bilboka.core.book.OdometerWayTooLargeException
import bilboka.core.book.domain.EntryType
import bilboka.core.book.domain.EventType
import bilboka.core.report.ReportGenerator
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.Vehicle
import bilboka.integration.autosys.AutosysProperties
import bilboka.integration.autosys.consumer.AkfDatautleveringConsumer
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest(classes = [VehicleService::class, TripService::class, Book::class, ReportGenerator::class, AkfDatautleveringConsumer::class, AutosysProperties::class])
@ExtendWith(SpringExtension::class)
@TestPropertySource(
    properties = [
        "autosys.akfDatautleveringUrl=test://url",
        "autosys.apiKey=testApiKey",
    ]
) // TODO er ikke sikkert alt dette trengs. Samme med test property fil
class CarBookIT : H2Test() {

    @Autowired
    lateinit var vehicleService: VehicleService

    @Autowired
    lateinit var tripService: TripService

    @Autowired
    lateinit var book: Book

    lateinit var vehicle: Vehicle

    @BeforeEach
    fun createVehicle() {
        vehicle = vehicleService.addVehicle("Xc70", fuelType = FuelType.DIESEL)
    }

    @Test
    fun addFuelForXC70_succeeds() {
        book.addFuelForVehicle(
            vehicleName = "XC70",
            odoReading = 1234,
            amount = 12.4,
            costNOK = 22.43,
            source = "test"
        )
        book.addFuelForVehicle(
            vehicleName = "XC70",
            odoReading = 1267,
            amount = 17.4,
            costNOK = 27.43,
            source = "test"
        )

        val vehicle = vehicleService.getVehicle("xc70")
        assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
        assertThat(vehicle.lastEntry(EntryType.FUEL)?.dateTime?.toLocalDate()).isEqualTo(LocalDate.now())
        assertThat(vehicle.lastEntry(EntryType.FUEL)?.odometer).isEqualTo(1267)
    }

    @Test
    fun addFuelForXC70_canAddBackInTime() {
        book.addFuelForVehicle(
            vehicleName = "XC70",
            odoReading = 1234,
            amount = 12.4,
            costNOK = 22.43,
            source = "test"
        )
        book.addFuelForVehicle(
            dateTime = LocalDateTime.now().minusMonths(1),
            vehicleName = "XC70",
            odoReading = 1167,
            amount = 17.4,
            costNOK = 27.43,
            source = "test"
        )

        val vehicle = vehicleService.getVehicle("xc70")
        assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
        assertThat(vehicle.lastEntry(EntryType.FUEL)?.dateTime?.toLocalDate()).isEqualTo(LocalDate.now())
        assertThat(vehicle.lastEntry(EntryType.FUEL)?.odometer).isEqualTo(1234)
    }

    @Test
    fun addFuelForXC70_succeedsAndValidatesOnlyAgainsLatest() {
        vehicle.addFuel(
            123456,
            23.6,
            300.0,
            dateTime = LocalDateTime.now().minusDays(3),
            source = "test"
        )
        vehicle.addFuel(
            1234,
            23.6,
            300.0,
            dateTime = LocalDateTime.now().minusDays(2),
            source = "test"
        )

        assertDoesNotThrow {
            book.addFuelForVehicle(
                vehicleName = "XC70",
                odoReading = 1235,
                amount = 12.4,
                costNOK = 22.43,
                source = "test"
            )
        }
    }

    @Test
    fun addFuelForXC70_failsAndValidatesOnlyAgainsLatest() {
        vehicle.addFuel(
            123456,
            23.6,
            300.0,
            dateTime = LocalDateTime.now().minusDays(3),
            source = "test"
        )
        vehicle.addFuel(
            1234,
            23.6,
            300.0,
            dateTime = LocalDateTime.now().minusDays(4),
            source = "test"
        )

        assertThrows<BookEntryChronologyException> {
            book.addFuelForVehicle(
                vehicleName = "XC70",
                odoReading = 1235,
                amount = 12.4,
                costNOK = 22.43,
                source = "test"
            )
        }
    }

    @Test
    fun addFuelForXC70drivingBackwards_fails() {
        book.addFuelForVehicle(
            vehicleName = "XC70",
            odoReading = 1234,
            amount = 12.4,
            costNOK = 22.43,
            source = "test"
        )
        assertThrows<BookEntryChronologyException> {
            book.addFuelForVehicle(
                vehicleName = "XC70",
                odoReading = 1217,
                amount = 17.4,
                costNOK = 27.43,
                source = "test"
            )
        }

        val vehicle = vehicleService.getVehicle("xc70")
        assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
        assertThat(vehicle.lastEntry(EntryType.FUEL)?.dateTime?.toLocalDate()).isEqualTo(LocalDate.now())
        assertThat(vehicle.lastEntry(EntryType.FUEL)?.odometer).isEqualTo(1234)
    }

    @Test
    fun noteStartOfTrip_worksAlsoOnSameOdoAsLastFuel() {
        transaction {
            val fuelEntry = book.addFuelForVehicle(
                vehicleName = "XC70",
                odoReading = 1256,
                amount = 12.4,
                costNOK = 22.43,
                source = "test"
            )

            tripService.startTrip(
                fuelEntry.vehicle,
                "just a trip",
                fuelEntry.odometer!!,
            )
        }

        val vehicle = vehicleService.getVehicle("xc70")
        assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
        assertThat(vehicle.lastEntry(EntryType.EVENT)?.event).isEqualTo(EventType.TRIP_START)
    }

    @Test
    fun noteStartOfTrip_failsWhenNotChronologic() {
        transaction {
            val fuelEntry = book.addFuelForVehicle(
                vehicleName = "XC70",
                odoReading = 1257,
                amount = 12.4,
                costNOK = 22.43,
                source = "test"
            )

            assertThrows<BookEntryChronologyException> {
                tripService.startTrip(
                    fuelEntry.vehicle,
                    "just a trip",
                    1255,
                )
            }
        }
    }

    @Test
    fun canGetLastPrices() {
        val aPrice = book.addFuelForVehicle(
            vehicleName = "XC70",
            odoReading = 1234,
            amount = 12.4,
            costNOK = 22.43,
            source = "test"
        ).pricePerLiter()
        val anotherPrice = book.addFuelForVehicle(
            vehicleName = "XC70",
            odoReading = 1267,
            amount = 17.4,
            costNOK = 27.43,
            source = "test"
        ).pricePerLiter()

        assertThat(book.getLastFuelPrices().map { it.second }).contains(aPrice, anotherPrice)
        assertThat(book.getLastFuelPrices(1, FuelType.DIESEL).map { it.second }).containsExactly(anotherPrice)
        assertThat(book.getLastFuelPrices(1, FuelType.BENSIN).map { it.second }).isEmpty()
    }

    @Test
    fun canEnterAndGetMaintenance() {
        vehicle.enterMaintenance(
            odometer = 12347,
            maintenanceItem = "BREMSEKLOSSER",
            comment = "Venstre foran",
            amount = 12.4,
            costNOK = 22.43,
            source = "test",
            createIfMissing = true
        )

        assertThat(book.maintenanceItems()).contains("BREMSEKLOSSER")
        assertThat(vehicle.lastMaintenance("BREMSEKLOSSER")?.odometer).isEqualTo(12347)
    }

    @Test
    fun setTankIsFullAt() {
        book.addFuelForVehicle(
            vehicleName = "XC70",
            odoReading = 1298,
            amount = 12.4,
            costNOK = 22.43,
            source = "test"
        )
        book.setIsFullTank(
            vehicleName = "XC70",
            odoReading = 1298,
        )

        val vehicle = vehicleService.getVehicle("xc70")
        assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
        assertThat(vehicle.lastEntry(EntryType.FUEL)?.isFullTank).isTrue
        assertThat(vehicle.lastEntry(EntryType.FUEL)?.odometer).isEqualTo(1298)
    }

    @Nested
    inner class AppendFuel {
        @Test
        fun appendFuelForXC70_succeeds() {
            book.appendFuelEntry(
                vehicleName = "XC70",
                odoReading = 12100,
                amount = 12.4,
                costNOK = 22.43,
                source = "test"
            )
            book.appendFuelEntry(
                vehicleName = "XC70",
                odoReading = 12200,
                amount = 17.4,
                costNOK = 27.43,
                source = "test"
            )

            val vehicle = vehicleService.getVehicle("xc70")
            assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
            assertThat(vehicle.lastEntry(EntryType.FUEL)?.odometer).isEqualTo(12200)
        }

        @Test
        fun appendFuelForXC70_succeedsAlsoWhenDataMissing() {
            book.appendFuelEntry(
                vehicleName = "XC70",
                odoReading = null,
                amount = 12.4,
                costNOK = 22.43,
                source = "test"
            )
            book.appendFuelEntry(
                vehicleName = "XC70",
                odoReading = 12200,
                amount = 17.4,
                costNOK = 27.43,
                source = "test"
            )

            val vehicle = vehicleService.getVehicle("xc70")
            assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
            assertThat(vehicle.lastEntry(EntryType.FUEL)?.odometer).isEqualTo(12200)
        }

        @Test
        fun appendFuelForXC70_canNotAppendBackwards() {
            book.appendFuelEntry(
                vehicleName = "XC70",
                dateTime = LocalDateTime.now(),
                odoReading = 12100,
                amount = 12.4,
                costNOK = 22.43,
                source = "test"
            )
            assertThrows<OdometerShouldNotBeDecreasingException> {
                book.appendFuelEntry(
                    vehicleName = "XC70",
                    dateTime = LocalDateTime.now().minusDays(1),
                    odoReading = 12000,
                    amount = 17.4,
                    costNOK = 27.43,
                    source = "test"
                )
            }

            val vehicle = vehicleService.getVehicle("xc70")
            assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
            assertThat(vehicle.lastEntry(EntryType.FUEL)?.odometer).isEqualTo(12100)
        }

        @Test
        fun appendFuelForXC70_canNotAppendHugelyIncreasingOdometer() {
            book.appendFuelEntry(
                vehicleName = "XC70",
                odoReading = 12100,
                amount = 12.4,
                costNOK = 22.43,
                source = "test"
            )
            assertThrows<OdometerWayTooLargeException> {
                book.appendFuelEntry(
                    vehicleName = "XC70",
                    odoReading = 120000,
                    amount = 17.4,
                    costNOK = 27.43,
                    source = "test"
                )
            }

            val vehicle = vehicleService.getVehicle("xc70")
            assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
            assertThat(vehicle.lastEntry(EntryType.FUEL)?.odometer).isEqualTo(12100)
        }
    }


}
