package bilboka.core.book.service

import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.OdometerUnit
import bilboka.core.domain.vehicle.Vehicle
import bilboka.core.domain.vehicle.Vehicles
import bilboka.core.vehicle.VehicleNotFoundException
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class VehicleService() {

    fun addVehicle(name: String, fuelType: FuelType, tegnkombinasjonNormalisert: String? = null): Vehicle {
        return transaction {
            Vehicle.new {
                this.name = name
                this.fuelType = fuelType
                this.tegnkombinasjonNormalisert = tegnkombinasjonNormalisert
                this.odometerUnit = OdometerUnit.KILOMETERS
            }
        }
    }

    fun findVehicle(vehicleName: String): Vehicle {
        return transaction {
            Vehicle.find {
                Vehicles.name eq vehicleName or (Vehicles.tegnkombinasjonNormalisert eq vehicleName.normaliserTegnkombinasjon())
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
