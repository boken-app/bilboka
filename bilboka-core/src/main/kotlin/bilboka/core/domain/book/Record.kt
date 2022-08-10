package bilboka.core.domain.book

import bilboka.core.domain.vehicle.Vehicle
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.persistence.*

@Entity
open class Record(
    val dateTime: LocalDateTime? = LocalDateTime.now(),
    val type: RecordType,
    val odometer: Int? = null,
    @ManyToOne
    val vehicle: Vehicle,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @CreatedDate
    val creationDateTime: ZonedDateTime? = null
) {
    // val creationDateTime: ZonedDateTime = now()
}
