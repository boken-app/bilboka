package bilboka.core.book.repository

import bilboka.core.book.domain.Book
import bilboka.core.vehicle.Vehicle
import org.springframework.stereotype.Repository

@Repository
interface BookStorage {

    fun save(book: Book): Book

    fun getForVehicle(vehicle: Vehicle): Book?

    fun getForVehicle(vehicleName: String): Book?
}