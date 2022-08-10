package bilboka.vehicle

import bilboka.core.domain.book.Book
import bilboka.core.domain.book.FuelRecord
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class VehicleTest {

    private val vehicle = Vehicle(name = "testbil", fuelType = FuelType.DIESEL)

    @BeforeEach
    fun setup() {
        val book = Book(vehicle)
    }

    @Test
    fun addFuel() {
        vehicle.addFuel(
            dateTime = LocalDateTime.now().minusDays(1),
            amount = 12.2,
            costNOK = 130.0,
            odometer = 12356,
            isFull = false
        )

        assertThat(vehicle.bookEntries).hasSize(1)
    }

    @Test
    fun addFuelIncomplete_hasDefaultValues() {
        val before = LocalDateTime.now()
        vehicle.addFuel(
            amount = 12.2,
            odometer = 12356,
        )

        val records = vehicle.bookEntries!!
        val fuelRecord = records[0] as FuelRecord
        assertThat(records).hasSize(1)
        assertThat(fuelRecord.odometer).isEqualTo(12356)
        assertThat(fuelRecord.amount).isEqualTo(12.2)
        assertThat(fuelRecord.costNOK).isNull()
        assertThat(fuelRecord.pricePerLiter()).isNull()
        assertThat(fuelRecord.dateTime).isAfterOrEqualTo(before)
    }
}