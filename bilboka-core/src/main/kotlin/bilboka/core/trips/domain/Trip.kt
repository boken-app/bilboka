package bilboka.core.trips.domain

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
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime

object Trips : IntIdTable() {
    val dateTimeStart = datetime("datetime_start").nullable()
    val dateTimeEnd = datetime("datetime_end").nullable()
    val tripName = varchar("trip_name", 255).nullable()
    val vehicle = reference("vehicle", Vehicles)
    val odometerStart = integer("odometer_start").nullable()
    val odometerEnd = integer("odometer_end").nullable()
    val comment = varchar("comment", 255).nullable()
    val enteredBy = reference("entered_by", Users).nullable()
    val creationTimestamp = timestamp("created_timestamp").clientDefault { Instant.now() }
}

class Trip(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Trip>(Trips)

    var dateTimeStart by Trips.dateTimeStart
    var dateTimeEnd by Trips.dateTimeEnd
    var tripName by Trips.tripName
    var vehicle by Vehicle referencedOn Trips.vehicle
    var odometerStart by Trips.odometerStart
    var odometerEnd by Trips.odometerEnd
    var comment by Trips.comment
    var enteredBy by User optionalReferencedOn Trips.enteredBy
    var creationTimestamp by Trips.creationTimestamp

    fun end(odo: Int, dateTime: LocalDateTime = LocalDateTime.now()) {
        transaction {
            dateTimeEnd = dateTime
            odometerEnd = odo
        }
    }

    fun isActive(): Boolean {
        return dateTimeEnd == null
    }
}
