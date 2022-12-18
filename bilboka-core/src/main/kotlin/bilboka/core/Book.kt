package bilboka.core

import bilboka.core.book.service.VehicleService
import bilboka.core.domain.book.Record
import bilboka.core.domain.book.RecordType
import org.springframework.stereotype.Component

@Component
class Book(
    val bookService: VehicleService
) {

    fun addFuelForVehicle(
        vehicleName: String,
        odoReading: Int,
        amount: Double,
        costNOK: Double,
        isFull: Boolean = false
    ): Record {
        val vehicle = bookService.findVehicle(vehicleName)
        return vehicle.addFuel(
            odometer = odoReading,
            amount = amount,
            costNOK = costNOK,
            isFull = isFull,
            source = "test"
        )
    }

    fun getLastFuelRecord(vehicle: String): Record? {
        return bookService.findVehicle(vehicle).lastRecord(RecordType.FUEL)
    }

}
