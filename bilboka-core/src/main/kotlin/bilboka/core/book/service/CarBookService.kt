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

    fun getBookForVehicle(vehicleName: String): Book? {
        return storage.getByName(vehicleName)?.book
    }

    fun addRecordForVehicle(record: Record, vehicle: String): Vehicle {
        val book = getBookForVehicle(vehicle) ?: throw VehicleNotFoundException("Fant ikke bil $vehicle")
        book.addRecord(record)
        return book.vehicle
    }
}