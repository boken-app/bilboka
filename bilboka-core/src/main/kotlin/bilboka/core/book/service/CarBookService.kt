package bilboka.core.book.service

import bilboka.core.book.domain.Book
import bilboka.core.book.domain.Record
import bilboka.core.repository.VehicleRepository
import bilboka.core.vehicle.Vehicle
import bilboka.core.vehicle.VehicleNotFoundException
import org.springframework.stereotype.Service

@Service
class CarBookService(val storage: VehicleRepository) {

    fun addVehicle(vehicle: Vehicle): Vehicle {
        vehicle.book = Book(vehicle)
        return storage.save(vehicle)
    }

    fun getVehicle(vehicleName: String): Vehicle? {
        return storage.getByName(vehicleName)
    }

    fun getBookForVehicle(vehicleName: String): Book {
        return getVehicle(vehicleName)?.book() ?: throw VehicleNotFoundException(
            "Fant ikke bil $vehicleName",
            vehicleName
        )
    }

    fun addRecordForVehicle(record: Record, vehicleName: String): Vehicle {
        val book =
            getBookForVehicle(vehicleName) ?: throw VehicleNotFoundException("Fant ikke bil $vehicleName", vehicleName)
        book.addRecord(record)
        return book.vehicle
    }
}