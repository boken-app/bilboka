package bilboka.core.book.service

import bilboka.core.domain.vehicle.Vehicle
import bilboka.core.repository.VehicleRepository
import bilboka.core.vehicle.VehicleNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class VehicleService(val storage: VehicleRepository) {

    fun addVehicle(vehicle: Vehicle): Vehicle {
        return storage.save(vehicle)
    }

    fun findVehicle(vehicleName: String): Vehicle {
        return storage.getByName(vehicleName)
            ?: storage.findByNicknames(vehicleName.lowercase())
            ?: storage.findByTegnkombinasjonNormalisert(vehicleName.normaliserTegnkombinasjon())
            ?: throw VehicleNotFoundException(
                "Fant ikke bil $vehicleName",
                vehicleName
            )
    }

}

fun String.normaliserTegnkombinasjon(): String {
    return this.uppercase().replace(" ", "").replace("-", "")
}
