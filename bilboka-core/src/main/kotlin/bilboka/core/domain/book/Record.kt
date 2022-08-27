package bilboka.core.domain.book

import bilboka.core.domain.vehicle.Vehicle
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.persistence.*

@Entity
open class Record(
    open val dateTime: LocalDateTime? = LocalDateTime.now(),
    open val type: RecordType,
    open val odometer: Int? = null,
    @ManyToOne
    open val vehicle: Vehicle,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,
    @CreatedDate
    open val creationDateTime: ZonedDateTime? = null
) {
    // val creationDateTime: ZonedDateTime = now()
}
