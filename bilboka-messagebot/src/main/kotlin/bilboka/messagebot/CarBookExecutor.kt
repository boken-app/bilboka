package bilboka.messagebot

import bilboka.core.book.domain.Record
import bilboka.core.book.service.CarBookService
import bilboka.core.vehicle.Vehicle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CarBookExecutor {

    @Autowired
    lateinit var carBookService: CarBookService

    fun addRecordToVehicle(record: Record, vehicle: String): Vehicle {
        return carBookService.addRecordForVehicle(record, vehicle)
    }

    fun getLastRecord(vehicle: String): Record? {
        return carBookService.getBookForVehicle(vehicle)?.records?.lastOrNull()
    }
}
