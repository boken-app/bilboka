package bilboka.client

data class BookEntryDto(
    val id: String,
    val type: String,
    val dateTime: String? = null,
    val odometer: Int? = null,
    val odometerKilometers: Int? = null,
    val amount: Double? = null,
    val costNOK: Double? = null,
    val isFullTank: Boolean? = null,
    val maintenanceItem: String? = null,
    val event: String? = null,
    val comment: String? = null
)
