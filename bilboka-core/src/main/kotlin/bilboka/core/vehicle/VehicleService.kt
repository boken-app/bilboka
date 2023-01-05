package bilboka.core.vehicle

import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.OdometerUnit
import bilboka.core.vehicle.domain.Vehicle
import bilboka.core.vehicle.domain.normaliserTegnkombinasjon
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class VehicleService() {

    fun addVehicle(
        name: String,
        nicknames: Set<String> = setOf(),
        fuelType: FuelType,
        tegnkombinasjon: String? = null
    ): Vehicle {
        return transaction {
            Vehicle.new {
                this.name = name.lowercase()
                this.nicknames = nicknames
                this.fuelType = fuelType
                this.tegnkombinasjonNormalisert = tegnkombinasjon?.normaliserTegnkombinasjon()
                this.odometerUnit = OdometerUnit.KILOMETERS
            }
        }
    }

    fun findVehicle(vehicleName: String): Vehicle {
        return transaction {
            Vehicle.all().singleOrNull { vehicle -> vehicle.isCalled(vehicleName) }
                ?: throw VehicleNotFoundException(
                    "Fant ikke bil $vehicleName",
                    vehicleName
                )
        }
    }

}
