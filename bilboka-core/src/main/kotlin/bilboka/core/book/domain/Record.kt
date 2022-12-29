package bilboka.core.book.domain

import bilboka.core.vehicle.domain.Vehicle
import bilboka.core.vehicle.domain.Vehicles
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant.now

object Records : IntIdTable() {
    val dateTime = datetime("datetime")
    val vehicle = reference("vehicle", Vehicles)
    val odometer = integer("odometer").nullable()
    val type = enumerationByName("type", 50, RecordType::class)
    val amount = double("amount").nullable()
    val costNOK = double("cost_nok").nullable()
    val isFullTank = bool("is_full").nullable()
    val dataSource = varchar("source", 50)
    val creationTimestamp = timestamp("created_timestamp").clientDefault { now() }
}

class Record(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Record>(Records)

    var dateTime by Records.dateTime
    var vehicle by Vehicle referencedOn Records.vehicle
    var odometer by Records.odometer
    var type by Records.type
    var amount by Records.amount
    var costNOK by Records.costNOK
    var isFullTank by Records.isFullTank
    var source by Records.dataSource
    var creationTimestamp by Records.creationTimestamp

    fun pricePerLiter(): Double? {
        if (costNOK == null || amount == null) {
            return null
        }
        return (costNOK!! / amount!!)
    }
}
