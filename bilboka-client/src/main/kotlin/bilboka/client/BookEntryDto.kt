package bilboka.client

import java.time.LocalDateTime

data class BookEntryDto(
    val id: String,
    val type: String,
    val dateTime: LocalDateTime? = null,
    val odometer: Int? = null,
    val odometerKilometers: Int? = null,
    val amount: Double? = null,
    val costNOK: Double? = null,
    val isFullTank: Boolean? = null,
    val maintenanceItem: String? = null,
    val event: String? = null,
    val comment: String? = null
)
