package bilboka.core.repository

import bilboka.core.book.domain.Book
import bilboka.core.vehicle.Vehicle

class InMemoryStorage : VehicleRepository {

    val vehicles: HashSet<Vehicle> = HashSet()

    init {
        val bil1 = Vehicle(
            name = "XC 70",
            nicknames = setOf("XC 70", "XC70", "XC70"),
            tegnkombinasjonNormalisert = "KT65881"
        )
        bil1.book = Book(bil1)
        save(bil1)
        val bil2 = Vehicle(
            name = "760",
            tegnkombinasjonNormalisert = "DF43250"
        )
        bil2.book = Book(bil2)
        save(bil2)
    }

    final override fun save(vehicle: Vehicle): Vehicle {
        vehicles.add(vehicle)
        return vehicle
    }

    final override fun getByName(name: String): Vehicle? {
        return vehicles.find { vehicle -> vehicle.isCalled(name) }
    }
}
