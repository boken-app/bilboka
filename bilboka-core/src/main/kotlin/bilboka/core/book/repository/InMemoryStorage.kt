package bilboka.core.book.repository

import bilboka.core.book.domain.Book
import bilboka.core.vehicle.Vehicle

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