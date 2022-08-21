package bilboka.core

import bilboka.core.book.service.VehicleService
import bilboka.core.domain.book.FuelRecord
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class Book(
    val bookService: VehicleService
) {

    fun addFuelForVehicle(
        vehicleName: String,
        odoReading: Int,
        amount: Double,
        costNOK: Double,
        isFull: Boolean = false
    ): FuelRecord {
        val vehicle = bookService.findVehicle(vehicleName)
        return vehicle.addFuel(
            odometer = odoReading,
            amount = amount,
            costNOK = costNOK,
            isFull = isFull
        )
    }

    fun getLastFuelRecord(vehicle: String): FuelRecord? {
        return bookService.findVehicle(vehicle).bookEntries?.lastOrNull { record -> record is FuelRecord } as FuelRecord?
    }

}
