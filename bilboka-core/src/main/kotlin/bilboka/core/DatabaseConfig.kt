package bilboka.core

import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig {

    init {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    }

}
