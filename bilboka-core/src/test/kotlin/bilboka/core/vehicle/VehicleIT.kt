package bilboka.core.vehicle

import bilboka.core.H2Test
import bilboka.core.book.domain.EntryType
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.Vehicle
import org.assertj.core.api.Assertions
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class VehicleIT : H2Test() {

    private lateinit var vehicle: Vehicle

    @BeforeEach
    fun setup() {
        vehicle = transaction {
            Vehicle.new {
                name = "testbilen"
                fuelType = FuelType.DIESEL
            }
        }
    }

    @Test
    fun addFuel() {
        vehicle.addFuel(
            dateTime = LocalDateTime.now().minusDays(1),
            amount = 12.2,
            costNOK = 130.0,
            odometer = 12356,
            isFull = false,
            source = "test"
        )

        transaction {
            Assertions.assertThat(Vehicle[vehicle.id].bookEntries).hasSize(1)
        }
    }

    @Test
    fun addFuelIncomplete_getsDefaultValues() {
        val before = LocalDateTime.now()
        vehicle.addFuel(
            amount = 12.2,
            odometer = 12356,
            costNOK = null,
            source = "test"
        )

        val fuelEntry = getVehicle().lastEntry(EntryType.FUEL)
        Assertions.assertThat(fuelEntry).isNotNull
        Assertions.assertThat(fuelEntry?.odometer).isEqualTo(12356)
        Assertions.assertThat(fuelEntry?.amount).isEqualTo(12.2)
        Assertions.assertThat(fuelEntry?.costNOK).isNull()
        Assertions.assertThat(fuelEntry?.pricePerLiter()).isNull()
        Assertions.assertThat(fuelEntry?.dateTime).isAfterOrEqualTo(before)
    }

    private fun getVehicle(): Vehicle {
        return transaction { Vehicle[vehicle.id] }
    }
}