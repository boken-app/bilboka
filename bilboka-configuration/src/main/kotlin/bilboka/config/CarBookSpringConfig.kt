package bilboka.config

import bilboka.core.book.repository.BookStorage
import bilboka.core.book.repository.InMemoryStorage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CarBookSpringConfig {

    @Bean
    fun bookStorage(): BookStorage {
        return InMemoryStorage()
    }

}