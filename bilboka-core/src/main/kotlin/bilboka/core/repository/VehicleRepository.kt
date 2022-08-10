package bilboka.core.repository

import bilboka.core.domain.vehicle.Vehicle
import org.springframework.data.repository.CrudRepository

interface VehicleRepository : CrudRepository<Vehicle, Long> {

    fun getByName(name: String): Vehicle?

}