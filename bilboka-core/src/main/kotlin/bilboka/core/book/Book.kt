package bilboka.core.book

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
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
        source: String,
        isFull: Boolean = false
    ): BookEntry {
        val vehicle = vehicleService.findVehicle(vehicleName)
        return vehicle.addFuel(
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
