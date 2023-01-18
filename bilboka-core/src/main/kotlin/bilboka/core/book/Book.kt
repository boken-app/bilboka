package bilboka.core.book

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.user.domain.User
import bilboka.core.vehicle.VehicleService
import org.springframework.stereotype.Component

@Component
class Book(
    val vehicleService: VehicleService
) {

    fun addFuelForVehicle(
        vehicleName: String,
        odoReading: Int,
        amount: Double,
        costNOK: Double,
        enteredBy: User? = null,
        source: String,
        isFull: Boolean = false
    ): BookEntry {
        val vehicle = vehicleService.findVehicle(vehicleName)
        return vehicle.addFuel(
            enteredBy = enteredBy,
            odometer = odoReading,
            amount = amount,
            costNOK = costNOK,
            isFull = isFull,
            source = source
        )
    }

    fun getLastFuelEntry(vehicle: String): BookEntry? {
        return vehicleService.findVehicle(vehicle).lastEntry(EntryType.FUEL)
    }

}
