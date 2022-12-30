package bilboka.core.book

import bilboka.core.book.domain.Record
import bilboka.core.book.domain.RecordType
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
        isFull: Boolean = false
    ): Record {
        val vehicle = vehicleService.findVehicle(vehicleName)
        return vehicle.addFuel(
            odometer = odoReading,
            amount = amount,
            costNOK = costNOK,
            isFull = isFull,
            source = "test"
        )
    }

    fun getLastFuelRecord(vehicle: String): Record? {
        return vehicleService.findVehicle(vehicle).lastRecord(RecordType.FUEL)
    }

}
