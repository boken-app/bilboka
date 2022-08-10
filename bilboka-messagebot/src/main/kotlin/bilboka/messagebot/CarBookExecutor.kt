package bilboka.messagebot

import bilboka.core.book.service.CarBookService
import bilboka.core.domain.book.Book
import bilboka.core.domain.book.Record
import bilboka.core.domain.vehicle.Vehicle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CarBookExecutor {

    @Autowired
    lateinit var carBookService: CarBookService

    fun addRecordToVehicle(record: Record, vehicle: String): Vehicle {
        return carBookService.addRecordForVehicle(record, vehicle)
    }

    fun getBookForVehicle(vehicle: String): Book {
        return carBookService.getBookForVehicle(vehicle)
    }
}
