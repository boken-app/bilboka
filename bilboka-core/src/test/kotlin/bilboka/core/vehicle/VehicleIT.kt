package bilboka.core.vehicle

import bilboka.core.H2Test
import bilboka.core.book.domain.EntryType
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
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

    @Nested
    inner class AddingFuel {
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
                assertThat(Vehicle[vehicle.id].bookEntries).hasSize(1)
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
            assertThat(fuelEntry).isNotNull
            assertThat(fuelEntry?.odometer).isEqualTo(12356)
            assertThat(fuelEntry?.amount).isEqualTo(12.2)
            assertThat(fuelEntry?.costNOK).isNull()
            assertThat(fuelEntry?.pricePerLiter()).isNull()
            assertThat(fuelEntry?.dateTime).isAfterOrEqualTo(before)
        }
    }

    @Nested
    inner class GetLastEntry {
        @Test
        fun getsLastFuelEntryByTime() {
            vehicle.addFuel(
                dateTime = LocalDateTime.now().minusDays(2),
                amount = 12.2,
                costNOK = 130.0,
                odometer = 123590,
                source = "test"
            )
            val last = vehicle.addFuel(
                dateTime = LocalDateTime.now().minusDays(1),
                amount = 12.0,
                costNOK = 139.0,
                odometer = 123,
                source = "test"
            )

            assertThat(vehicle.lastEntry(EntryType.FUEL)?.id).isEqualTo(last.id)
        }

        @Test
        fun handlesNull() {
            val onlyDated = vehicle.addFuel(
                dateTime = LocalDateTime.now().minusDays(1),
                amount = 12.0,
                costNOK = 139.0,
                odometer = 123,
                source = "test"
            )
            vehicle.addFuel(
                dateTime = null,
                amount = 12.2,
                costNOK = 130.0,
                odometer = 123590,
                source = "test"
            )

            assertThat(vehicle.lastEntry(EntryType.FUEL)?.id).isEqualTo(onlyDated.id)
        }

        @Test
        fun getMaintenanceGetsLastByTimeOrOdometer() {
            val lastBremsekloss = vehicle.enterMaintenance(
                dateTime = null,
                maintenanceItem = "BREMSEKLOSSER",
                odometer = 234888,
                source = "test",
                createIfMissing = true
            )
            vehicle.enterMaintenance(
                dateTime = LocalDateTime.now().minusDays(2),
                maintenanceItem = "BREMSEKLOSSER",
                odometer = 234875,
                source = "test",
                createIfMissing = true
            )
            val lastOlje = vehicle.enterMaintenance(
                dateTime = LocalDateTime.now().minusDays(1),
                maintenanceItem = "OLJE",
                odometer = null,
                source = "test",
                createIfMissing = true
            )
            vehicle.enterMaintenance(
                dateTime = LocalDateTime.now().minusDays(2),
                maintenanceItem = "OLJE",
                odometer = 234877,
                source = "test",
                createIfMissing = true
            )
            vehicle.enterMaintenance(
                dateTime = null,
                maintenanceItem = "OLJE",
                odometer = 234665,
                source = "test",
                createIfMissing = true
            )

            assertThat(vehicle.lastMaintenance("BREMSEKLOSSER")?.id).isEqualTo(lastBremsekloss.id)
            assertThat(vehicle.lastMaintenance("OLJE")?.id).isEqualTo(lastOlje.id)
        }
    }

    @Nested
    inner class Maintenance {
        @Test
        fun enterAndGetMaintenance() {
            vehicle.enterMaintenance(
                dateTime = LocalDateTime.now().minusDays(2),
                maintenanceItem = "BREMSEKLOSSER",
                amount = 12.2,
                costNOK = 130.0,
                odometer = 234565,
                source = "test",
                createIfMissing = true
            )

            assertThat(vehicle.lastMaintenance("BREMSEKLOSSER")?.odometer).isEqualTo(234565)
        }
    }

    @Nested
    inner class Comment {
        @Test
        fun enterComment() {
            vehicle.enterComment(
                comment = "kommentar-test",
                dateTime = LocalDateTime.now().minusDays(2),
                costNOK = 130.0,
                odometer = 234565,
                source = "test",
            )

            assertThat(vehicle.lastEntry(EntryType.BASIC)).extracting { it?.comment }.isEqualTo("kommentar-test")
        }
    }

    private fun getVehicle(): Vehicle {
        return transaction { Vehicle[vehicle.id] }
    }
}
