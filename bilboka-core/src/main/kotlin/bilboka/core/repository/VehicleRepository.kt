package bilboka.core.repository

import bilboka.core.domain.vehicle.Vehicle

interface VehicleRepository {

    fun save(vehicle: Vehicle): Vehicle

    fun getByName(name: String): Vehicle?

    fun findByNicknames(name: String): Vehicle?

    fun findByTegnkombinasjonNormalisert(name: String): Vehicle?

}