package bilboka.messagebot

import bilboka.core.book.domain.FuelRecord
import bilboka.core.book.service.CarBookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CarBookExecutor {

    @Autowired
    lateinit var carBookService: CarBookService

    fun addFuelRecord(vehicle: String, amount: Double, cost: Double, isFull: Boolean = false): FuelRecord {
        val fuelRecord = FuelRecord(amount = amount, costNOK = cost, isFull = isFull)
        carBookService.addRecordForVehicle(fuelRecord, vehicle)
        return fuelRecord
    }
}
