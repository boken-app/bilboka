package bilboka.messagebot

import java.text.NumberFormat
import java.util.*

fun Double?.format(): String {
    val instance = NumberFormat.getInstance(Locale.GERMANY)
    instance.maximumFractionDigits = 2
    return instance.format(this)
}
