package bilboka.core

import bilboka.core.book.Book
import bilboka.core.trips.domain.Trip
import bilboka.core.user.domain.User
import bilboka.core.vehicle.domain.Vehicle
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TripService(
    val carBook: Book
) {

    fun startTrip(
        vehicle: Vehicle,
        tripName: String,
        odo: Int,
        startedBy: User? = null,
        dateTime: LocalDateTime = LocalDateTime.now()
    ): Trip {
        return transaction {
            Trip.new {
                this.vehicle = vehicle
                this.odometerStart = odo
                this.tripName = tripName
                this.dateTimeStart = dateTime
                this.enteredBy = startedBy
            }.also { carBook.noteTripStart(it) }
        }
    }

    fun endCurrentTrip(
        vehicle: Vehicle,
        odo: Int,
        endedBy: User? = null,
        dateTime: LocalDateTime = LocalDateTime.now()
    ): Trip? {
        return transaction {
            getActiveTrip(vehicle)
                ?.apply { end(odo, dateTime) }
                ?.also { carBook.noteTripEnd(it, endedBy) }
        }
    }

    fun getActiveTrip(vehicle: Vehicle): Trip? {
        return transaction { Trip.all().singleOrNull { it.vehicle.id == vehicle.id && it.isActive() } }
    }
}
