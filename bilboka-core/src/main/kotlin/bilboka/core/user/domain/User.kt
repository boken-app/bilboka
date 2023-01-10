package bilboka.core.user.domain

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object Users : IntIdTable() {
    val username = varchar("username", 30).uniqueIndex()
    val creationTimestamp = timestamp("created_timestamp").clientDefault { Instant.now() }
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    val registrations by UserRegistration referrersOn UserRegistrations.user
    val creationTimestamp by Users.creationTimestamp

    fun getIDRegisteredFor(regTypeID: String): String? {
        return transaction {
            registrations.find {
                it.registrationTypeID == regTypeID
            }?.registeredID
        }
    }
}
