package bilboka.core.book.service

import bilboka.core.book.domain.Book
import bilboka.core.book.domain.Record
import bilboka.core.book.repository.BookStorage
import bilboka.core.vehicle.Vehicle
import bilboka.core.vehicle.VehicleNotFoundException
import org.springframework.stereotype.Service

@Service
class CarBookService(val storage: BookStorage) {

    fun makeNewBookForVehicle(vehicle: Vehicle): Book {
        return storage.save(Book(vehicle))
    }

    fun getBookForVehicle(vehicle: Vehicle): Book? {
        return storage.getForVehicle(vehicle)
    }

    fun getBookForVehicle(vehicleName: String): Book? {
        return storage.getForVehicle(vehicleName)
    }

    fun addRecordForVehicle(record: Record, vehicle: Vehicle) {
        val book = storage.getForVehicle(vehicle)
        book?.addRecord(record)
    }

    fun addRecordForVehicle(record: Record, vehicle: String) {
        val book = getBookForVehicle(vehicle) ?: throw VehicleNotFoundException("Fant ikke bil $vehicle")
        book.addRecord(record)
    }
}