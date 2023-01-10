package bilboka.core.user.domain

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object UserRegistrations : IntIdTable() {
    val user = reference("user", Users)
    val registrationTypeID = varchar("registrationTypeID", 50)
    var registeredID = varchar("registeredID", 50)
    val creationTimestamp = timestamp("created_timestamp").clientDefault { Instant.now() }
}

class UserRegistration(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserRegistration>(UserRegistrations)

    var user by User referencedOn UserRegistrations.user
    var registrationTypeID by UserRegistrations.registrationTypeID
    var registeredID by UserRegistrations.registeredID
    var creationTimestamp by Users.creationTimestamp
}
