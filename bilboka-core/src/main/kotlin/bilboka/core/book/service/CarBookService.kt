package bilboka.core.book.service

import bilboka.core.domain.book.Book
import bilboka.core.domain.book.Record
import bilboka.core.domain.vehicle.Vehicle
import bilboka.core.repository.VehicleRepository
import bilboka.core.vehicle.VehicleNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CarBookService(val storage: VehicleRepository) {

    fun addVehicle(vehicle: Vehicle): Vehicle {
        return storage.save(vehicle)
    }

    fun getVehicle(vehicleName: String): Vehicle? {
        return storage.getByName(vehicleName)
    }

    fun getBookForVehicle(vehicleName: String): Book {
        return getVehicle(vehicleName)?.let { Book(it) } ?: throw VehicleNotFoundException(
            "Fant ikke bil $vehicleName",
            vehicleName
        )
    }

    fun addRecordForVehicle(record: Record, vehicleName: String): Vehicle {
        val book =
            getBookForVehicle(vehicleName)
        book.addRecord(record)
        return book.vehicle
    }
}