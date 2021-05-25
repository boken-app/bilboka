package ivaralek.bilboka.core.book.service

import ivaralek.bilboka.core.book.domain.Book
import ivaralek.bilboka.core.book.domain.Record
import ivaralek.bilboka.core.book.repository.BookStorage
import ivaralek.bilboka.core.vehicle.Vehicle
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
}