package bilboka.core.repository

import bilboka.core.domain.book.Record
import org.springframework.data.repository.CrudRepository

interface BookEntriesRepository : CrudRepository<Record, Long> {


}