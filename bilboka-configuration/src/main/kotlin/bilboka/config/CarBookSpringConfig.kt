package bilboka.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*

@Configuration
class CarBookSpringConfig {

    @Bean
    fun localeResolver(): LocaleResolver? {
        val slr = SessionLocaleResolver()
        slr.setDefaultLocale(Locale.GERMANY)
        return slr
    }
//
//    @Bean
//    fun cleanMigrateStrategy(): FlywayMigrationStrategy? {
//        return FlywayMigrationStrategy { flyway: Flyway ->
//            flyway.repair()
//            flyway.migrate()
//        }
//    }
}
