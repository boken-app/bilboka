package bilboka.config

import bilboka.core.repository.InMemoryStorage
import bilboka.core.repository.VehicleRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CarBookSpringConfig {

    @Bean
    fun vehicleRepository(): VehicleRepository {
        return InMemoryStorage()
    }

}