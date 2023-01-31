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
        validateFuelRequest(vehicle, dateTime, odoReading, amount, costNOK)
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

    private fun validateFuelRequest(
        vehicle: Vehicle,
        dateTime: LocalDateTime?,
        odoReading: Int?,
        amount: Double?,
        costNOK: Double?
    ) {
        vehicle.lastEntry(EntryType.FUEL)?.checkIfDuplicate(odoReading, amount, costNOK)
        odoReading?.validateAsOdometer()
        amount?.validateAsAmount()
        costNOK?.validateAsCost()
        vehicle.lastEntry()?.checkChronologyAgainst(dateTime, odoReading)
    }

    fun getLastFuelEntry(vehicle: String): BookEntry? {
        return vehicleService.findVehicle(vehicle).lastEntry(EntryType.FUEL)
    }
}

private fun BookEntry.checkIfDuplicate(odoReading: Int?, amount: Double?, costNOK: Double?) {
    if (this.odometer == odoReading
        && this.amount == amount
        && this.costNOK == costNOK
    ) {
        throw DuplicateBookEntryException()
    }
}

fun Double.validateAsAmount() {
    if (this > 1000) {
        throw BookEntryException("Usannsynlig verdi for mengde.")
    }
}

fun Double.validateAsCost() {
    if (this > 10000) {
        throw BookEntryException("Usannsynlig verdi for kostnad.")
    }
}

fun Int.validateAsOdometer() {
    if (this > 10000000) {
        throw BookEntryException("Usannsynlig verdi for kilometerstand.")
    }
}

private fun BookEntry.checkChronologyAgainst(dateTime: LocalDateTime?, odoReading: Int?) {
    if (odoReading != null
        && this.dateTime.compareTo(dateTime ?: LocalDateTime.now()) != this.odometer?.compareTo(odoReading)
    ) {
        throw BookEntryChronologyException("Angitt kilometerstand er ikke i kronologisk rekkefølge med tidligere angitt.")
    }
}
