package bilboka.core.vehicle

import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.OdometerUnit
import bilboka.core.vehicle.domain.Vehicle
import bilboka.core.vehicle.domain.Vehicles
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class VehicleService() {

    fun addVehicle(name: String, fuelType: FuelType, tegnkombinasjon: String? = null): Vehicle {
        return transaction {
            Vehicle.new {
                this.name = name.lowercase()
                this.fuelType = fuelType
                this.tegnkombinasjonNormalisert = tegnkombinasjon?.normaliserTegnkombinasjon()
                this.odometerUnit = OdometerUnit.KILOMETERS
            }
        }
    }

    fun findVehicle(vehicleName: String): Vehicle {
        return transaction {
            Vehicle.find {
                Vehicles.name eq vehicleName.lowercase() or (Vehicles.tegnkombinasjonNormalisert eq vehicleName.normaliserTegnkombinasjon())
            }
                .singleOrNull() ?: throw VehicleNotFoundException(
                "Fant ikke bil $vehicleName",
                vehicleName
            )
        }
    }

}

fun String.normaliserTegnkombinasjon(): String {
    return this.uppercase().replace(" ", "").replace("-", "")
}
