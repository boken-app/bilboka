package bilboka.core.book

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.user.domain.User
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class Book(
    val vehicleService: VehicleService
) {

    fun addFuelForVehicle(
        vehicleName: String,
        dateTime: LocalDateTime? = LocalDateTime.now(),
        odoReading: Int?,
        amount: Double?,
        costNOK: Double?,
        enteredBy: User? = null,
        source: String,
        isFull: Boolean = false
    ): BookEntry {
        val vehicle = vehicleService.findVehicle(vehicleName)
        validateFuelRequest(vehicle, odoReading, amount, costNOK)
        return vehicle.addFuel(
            enteredBy = enteredBy,
            dateTime = dateTime,
            odometer = odoReading,
            amount = amount,
            costNOK = costNOK,
            isFull = isFull,
            source = source
        )
    }

    private fun validateFuelRequest(vehicle: Vehicle, odoReading: Int?, amount: Double?, costNOK: Double?) {
        val lastEntry = vehicle.lastEntry(EntryType.FUEL)
        if (lastEntry != null && lastEntry.odometer == odoReading
            && lastEntry.amount == amount && lastEntry.costNOK == costNOK
        ) {
            throw DuplicateBookEntryException("Kan ikke opprette to identiske drivstoff-oppføringer etter hverandre.")
        }
    }

    fun getLastFuelEntry(vehicle: String): BookEntry? {
        return vehicleService.findVehicle(vehicle).lastEntry(EntryType.FUEL)
    }

}
