package bilboka.core.book.domain

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object MaintenanceItems : IntIdTable() {
    val item = varchar("item", 100).uniqueIndex()

    fun getItem(item: String): MaintenanceItem? {
        return MaintenanceItem.find { MaintenanceItems.item eq item.uppercase() }.singleOrNull()
    }
}

class MaintenanceItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MaintenanceItem>(MaintenanceItems)

    var item by MaintenanceItems.item.transform(
        toColumn = { a -> a.uppercase() },
        toReal = { a -> a }
    )

}
