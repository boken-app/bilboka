package bilboka.core.config

import bilboka.core.repository.VehicleRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = [VehicleRepository::class])
@EntityScan(basePackages = ["bilboka.core.domain"])
class BilbokaCoreConfig {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${spring.datasource.url:null}")
    private val dbUrl: String? = null

    @Bean
    @ConditionalOnProperty("spring.datasource.url")
    fun dataSource(): DataSource? {
        logger.debug("Creating datasource config for database $dbUrl")
        val config = HikariConfig()
        config.jdbcUrl = dbUrl
        return HikariDataSource(config)
    }
}
