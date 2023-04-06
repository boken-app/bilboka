package bilboka.core

import bilboka.core.book.Book
import bilboka.core.book.BookEntryChronologyException
import bilboka.core.book.domain.EntryType
import bilboka.core.report.ReportGenerator
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest(classes = [VehicleService::class, Book::class, ReportGenerator::class])
class CarBookIT : H2Test() {

    @Autowired
    lateinit var vehicleService: VehicleService

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
}
