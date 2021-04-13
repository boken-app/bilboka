package ivaralek.bilboka.book.config

import ivaralek.bilboka.book.repository.BookStorage
import ivaralek.bilboka.book.repository.InMemoryStorage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CarBookSpringConfig {

    @Bean
    fun bookStorage(): BookStorage {
        return InMemoryStorage()
    }

}