package bilboka.messagebot

import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private const val UKJENT = "<ukjent>"

fun Double?.format(): String {
    return this?.let {
        val instance = NumberFormat.getInstance(Locale.GERMANY)
        instance.maximumFractionDigits = 2
        return instance.format(this)
    } ?: UKJENT
}

fun Double?.formatShort(): String {
    return this?.let {
        val instance = NumberFormat.getInstance(Locale.GERMANY)
        instance.maximumFractionDigits = 0
        return instance.format(this)
    } ?: UKJENT
}

fun LocalDateTime?.format(): String {
    return this?.let {
        this.format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        )
    } ?: UKJENT
}

fun LocalDateTime?.formatAsDate(): String {
    return this?.let {
        this.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        )
    } ?: UKJENT
}

fun LocalDate?.format(): String {
    return this?.let {
        this.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        )
    } ?: UKJENT
}
