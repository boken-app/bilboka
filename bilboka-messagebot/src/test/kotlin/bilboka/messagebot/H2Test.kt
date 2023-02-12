package bilboka.messagebot

import bilboka.core.book.domain.BookEntries
import bilboka.core.book.domain.MaintenanceItems
import bilboka.core.user.domain.RegistrationKeys
import bilboka.core.user.domain.UserRegistrations
import bilboka.core.user.domain.Users
import bilboka.core.vehicle.domain.Vehicles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

// TODO: Integrasjonstester på tvers av moduler kan være i config-modul
abstract class H2Test {
    @BeforeAll
    fun setupDatabase() {
        val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(db) {
            SchemaUtils.create(BookEntries, Vehicles, Users, UserRegistrations, RegistrationKeys, MaintenanceItems)
            commit()
        }
    }

    @AfterAll
    fun wipeDatabase() {
        transaction {
            BookEntries.deleteAll()
            Vehicles.deleteAll()
            UserRegistrations.deleteAll()
            RegistrationKeys.deleteAll()
            Users.deleteAll()
            MaintenanceItems.deleteAll()
        }
    }
}
