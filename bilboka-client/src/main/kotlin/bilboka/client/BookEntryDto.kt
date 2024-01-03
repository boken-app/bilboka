package bilboka.client

data class BookEntryDto(
    val id: String,
    val type: String,
    val dateTime: String?,
    val odometer: Int?,
    val odometerKilometers: Int?,
    val amount: Double?,
    val costNOK: Double?,
    val isFullTank: Boolean,
    val maintenanceItem: String?,
    val event: String?,
    val comment: String?,
)
