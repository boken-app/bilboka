package bilboka.core.domain.book

import bilboka.core.domain.vehicle.Vehicle
import org.springframework.stereotype.Component

@Component
class Book(
    val vehicle: Vehicle,
)/*: AbstractPersistable<Long>()*/ {

    // TODO Egentlig trengs vel ikke Book som egen entitet.
    var records: MutableList<Record> = vehicle.bookEntries!!

    fun getForVehicle(vehicle: Vehicle) {

    }

    fun addRecord(record: Record) {
        records.add(record)
    }

    fun getLastFuelRecord(): FuelRecord? {
        return records.lastOrNull { record -> record is FuelRecord } as FuelRecord?
    }

}
