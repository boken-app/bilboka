package bilboka.core.book.repository

import bilboka.core.book.domain.Book
import bilboka.core.vehicle.Vehicle

class InMemoryStorage : BookStorage {

    val books: HashSet<Book> = HashSet()

    init {
        save(
            Book(
                Vehicle(
                    name = "XC 70",
                    nicknames = setOf("XC 70", "XC70"),
                )
            )
        )
        save(
            Book(
                Vehicle(
                    name = "760",
                )
            )
        )
    }

    final override fun save(book: Book): Book {
        books.add(book)
        return book
    }

    override fun getForVehicle(vehicle: Vehicle): Book? {
        return books.find { book -> book.vehicle == vehicle }
    }

    override fun getForVehicle(vehicleName: String): Book? {
        return books.find { book -> book.vehicle.isCalled(vehicleName) }
    }
}