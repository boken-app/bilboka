package bilboka.core

import bilboka.core.book.Book
import bilboka.core.book.BookEntryChronologyException
import bilboka.core.book.domain.EntryType
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest(classes = [VehicleService::class, Book::class])
class CarBookIT : H2Test() {

    @Autowired
    lateinit var vehicleService: VehicleService

    @Autowired
    lateinit var book: Book

    @Test
    fun addFuelForXC70_succeeds() {
        vehicleService.addVehicle("Xc70", fuelType = FuelType.BENSIN)

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
        vehicleService.addVehicle("Xc70", fuelType = FuelType.BENSIN)

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
        val car = vehicleService.addVehicle("Xc70", fuelType = FuelType.BENSIN)
        car.addFuel(
            123456,
            23.6,
            300.0,
            dateTime = LocalDateTime.now().minusDays(3),
            source = "test"
        )
        car.addFuel(
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
        val car = vehicleService.addVehicle("Xc70", fuelType = FuelType.BENSIN)
        car.addFuel(
            123456,
            23.6,
            300.0,
            dateTime = LocalDateTime.now().minusDays(3),
            source = "test"
        )
        car.addFuel(
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
        vehicleService.addVehicle("Xc70", fuelType = FuelType.BENSIN)

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
        vehicleService.addVehicle("Xc70", fuelType = FuelType.BENSIN)

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
    }
}
