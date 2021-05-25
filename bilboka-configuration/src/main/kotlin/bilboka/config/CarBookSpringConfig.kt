package ivaralek.bilboka.book.config

import ivaralek.bilboka.core.book.repository.BookStorage
import ivaralek.bilboka.core.book.repository.InMemoryStorage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CarBookSpringConfig {

    @Bean
    fun bookStorage(): BookStorage {
        return InMemoryStorage()
    }

}