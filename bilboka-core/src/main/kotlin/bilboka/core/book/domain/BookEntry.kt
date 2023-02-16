package bilboka.core.book.domain

import bilboka.core.user.domain.User
import bilboka.core.user.domain.Users
import bilboka.core.vehicle.domain.Vehicle
import bilboka.core.vehicle.domain.Vehicles
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant.now

object BookEntries : IntIdTable() {
    val dateTime = datetime("datetime").nullable()
    val vehicle = reference("vehicle", Vehicles)
    val odometer = integer("odometer").nullable()
    val type = enumerationByName("type", 50, EntryType::class)
    val amount = double("amount").nullable()
    val costNOK = double("cost_nok").nullable()
    val isFullTank = bool("is_full").nullable()
    val maintenanceItem = reference("maintenance_item", MaintenanceItems).nullable()
    val event = enumerationByName("event", 50, EventType::class).nullable()
    val comment = varchar("comment", 255).nullable()
    val enteredBy = reference("entered_by", Users).nullable()
    val dataSource = varchar("source", 50)
    val creationTimestamp = timestamp("created_timestamp").clientDefault { now() }
}

class BookEntry(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BookEntry>(BookEntries)

    var dateTime by BookEntries.dateTime
    var vehicle by Vehicle referencedOn BookEntries.vehicle
    var odometer by BookEntries.odometer
    var type by BookEntries.type
    var amount by BookEntries.amount
    var costNOK by BookEntries.costNOK
    var isFullTank by BookEntries.isFullTank
    var maintenanceItem by MaintenanceItem optionalReferencedOn BookEntries.maintenanceItem
    var event by BookEntries.event
    var comment by BookEntries.comment
    var enteredBy by User optionalReferencedOn BookEntries.enteredBy
    var source by BookEntries.dataSource
    var creationTimestamp by BookEntries.creationTimestamp

    fun pricePerLiter(): Double? {
        if (costNOK == null || amount == null) {
            return null
        }
        return (costNOK!! / amount!!)
    }
}
