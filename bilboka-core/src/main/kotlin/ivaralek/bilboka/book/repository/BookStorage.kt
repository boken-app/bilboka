package ivaralek.bilboka.book.repository

import ivaralek.bilboka.book.domain.Book
import ivaralek.bilboka.vehicle.Vehicle
import org.springframework.stereotype.Repository

@Repository
interface BookStorage {

    fun save(book: Book): Book

    fun getForVehicle(vehicle: Vehicle): Book?

    fun getForVehicle(vehicleName: String): Book?
}