package bilboka.web.converter

import bilboka.client.BookEntryDto
import bilboka.core.book.domain.BookEntry
import bilboka.core.vehicle.domain.OdometerUnit

object EntryConverter {
    fun BookEntry.toDto(odometerUnit: OdometerUnit? = null): BookEntryDto {
        return BookEntryDto(
            id = id.toString(),
            type = type.toString(),
            dateTime = dateTime,
            odometer = odometer,
            odometerKilometers = odometer?.let { odometerUnit?.convertToKilometers(it) },
            amount = amount,
            costNOK = costNOK,
            isFullTank = isFullTank,
            maintenanceItem = maintenanceItem?.item,
            event = event?.name,
            comment = comment
        )
    }
}
