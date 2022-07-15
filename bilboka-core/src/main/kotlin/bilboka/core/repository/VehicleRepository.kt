package bilboka.core.repository

import bilboka.core.vehicle.Vehicle
import org.springframework.stereotype.Repository

@Repository
interface VehicleRepository {

    fun save(vehicle: Vehicle): Vehicle
    fun getByName(name: String): Vehicle?

}