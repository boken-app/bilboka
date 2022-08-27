package bilboka.core.domain.book

import bilboka.core.domain.vehicle.Vehicle
import java.time.LocalDateTime

class MaintenanceRecord(date: LocalDateTime?, vehicle: Vehicle, odometer: Int?) :
    Record(date, RecordType.MAINTENANCE, odometer, vehicle) {

}
