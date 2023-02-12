package bilboka.core.book

import bilboka.core.book.domain.BookEntries
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.user.domain.User
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.sign

@Component
class Book(
    private val vehicleService: VehicleService
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
        val vehicle = vehicleService.getVehicle(vehicleName)
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
        return vehicleService.getVehicle(vehicle).lastEntry(EntryType.FUEL)
    }

    fun getLastFuelPrices(n: Int = 5): List<Pair<LocalDate, Double>> {
        return transaction {
            BookEntry
                .find { BookEntries.type eq EntryType.FUEL }
                .sortedByDescending { it.dateTime }
                .filter { it.pricePerLiter() != null }
                .take(n)
                .map { Pair(it.dateTime.toLocalDate(), it.pricePerLiter() as Double) }
        }
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
    if (odoReading != null && this.odometer != null
        && this.dateTime.compareTo(dateTime ?: LocalDateTime.now()).sign != this.odometer?.compareTo(odoReading)?.sign
    ) {
        throw BookEntryChronologyException("Angitt kilometerstand er ikke i kronologisk rekkefølge med tidligere angitt.")
    }
}
