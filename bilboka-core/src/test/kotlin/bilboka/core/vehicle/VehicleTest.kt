package bilboka.vehicle

import bilboka.core.H2Test
import bilboka.core.domain.book.RecordType
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class VehicleTest : H2Test() {

    private lateinit var vehicle: Vehicle

    @BeforeAll
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
            assertThat(Vehicle[vehicle.id].records).hasSize(1)
        }
    }

    @Test
    fun addFuelIncomplete_hasDefaultValues() {
        val before = LocalDateTime.now()
        vehicle.addFuel(
            amount = 12.2,
            odometer = 12356,
            costNOK = null,
            source = "test"
        )

        val fuelRecord = getVehicle().lastRecord(RecordType.FUEL)
        assertThat(fuelRecord).isNotNull
        assertThat(fuelRecord?.odometer).isEqualTo(12356)
        assertThat(fuelRecord?.amount).isEqualTo(12.2)
        assertThat(fuelRecord?.costNOK).isNull()
        assertThat(fuelRecord?.pricePerLiter()).isNull()
        assertThat(fuelRecord?.dateTime).isAfterOrEqualTo(before)
    }

    private fun getVehicle(): Vehicle {
        return transaction { Vehicle[vehicle.id] }
    }
}
