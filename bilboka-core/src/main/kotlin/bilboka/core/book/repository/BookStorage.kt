package ivaralek.bilboka.core.book.repository

import ivaralek.bilboka.core.book.domain.Book
import ivaralek.bilboka.core.vehicle.Vehicle
import org.springframework.stereotype.Repository

@Repository
interface BookStorage {

    fun save(book: Book): Book

    fun getForVehicle(vehicle: Vehicle): Book?

    fun getForVehicle(vehicleName: String): Book?
}