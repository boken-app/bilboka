package bilboka.core.user.domain

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object RegistrationKeys : IntIdTable() {
    val user = reference("user", Users)
    val key = varchar("key", 100)
    val isUsable = bool("usable").default(true)
}

class RegistrationKey(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RegistrationKey>(RegistrationKeys)

    var user by User referencedOn RegistrationKeys.user
    var key by RegistrationKeys.key
    var isUsable by RegistrationKeys.isUsable
}
