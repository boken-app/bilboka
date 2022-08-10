package bilboka.core.config

import bilboka.core.repository.VehicleRepository
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = [VehicleRepository::class])
@EntityScan(basePackages = ["bilboka.core.domain"])
class BilbokaCoreConfig {
}
