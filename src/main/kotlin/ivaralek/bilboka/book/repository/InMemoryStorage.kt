package ivaralek.bilboka.book.repository

import ivaralek.bilboka.book.domain.Book
import ivaralek.bilboka.vehicle.Vehicle

class InMemoryStorage : BookStorage {

    val books: HashSet<Book> = HashSet()

    override fun save(book: Book): Book {
        books.add(book)
        return book
    }

    override fun getForVehicle(vehicle: Vehicle): Book? {
        return books.find { book -> book.vehicle == vehicle }
    }

    override fun getForVehicle(vehicleName: String): Book? {
        return books.find { book -> book.vehicle.name == vehicleName }
    }
}