package bilboka.client

import java.time.LocalDateTime

data class BilbokaDataPoint(
    val dateTime: LocalDateTime,
    val vehicleId: String? = null,
    val odometer: Int? = null,
    val odometerKilometers: Int? = null,
    val amount: Double? = null,
    val costNOK: Double? = null,
    val priceNOK: Double? = null,
    val isFullTank: Boolean? = null,
    val estimatedConsumptionLitersPer10Km: Double? = null,
    val estimatedRemainingFuel: Double? = null,
    val averageKilometersPerDay: Double? = null,
    val sourceEntryFirst: BookEntryDto? = null,
    val sourceEntryLast: BookEntryDto? = null,
)
