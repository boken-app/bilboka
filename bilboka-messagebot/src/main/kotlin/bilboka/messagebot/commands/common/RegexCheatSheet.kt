package bilboka.messagebot.commands.common

val ODOMETER_REGEX = Regex("([0-9]{5,7})(?:\\s?(?:km|mi))?|(\\d+)\\s?(?:km|mi)", RegexOption.IGNORE_CASE)
val NUMBER_REGEX = Regex("(\\d+(?:[.,]\\d{1,2})?)", RegexOption.IGNORE_CASE)
val VOLUME_REGEX = Regex("${NUMBER_REGEX.pattern}\\s?l", RegexOption.IGNORE_CASE)
val COST_REGEX = Regex("${NUMBER_REGEX.pattern}\\s?(kr|nok)(?:[^/]|$)", RegexOption.IGNORE_CASE)
val COST_PER_AMOUNT_REGEX = Regex("${NUMBER_REGEX.pattern}\\s?kr/l", RegexOption.IGNORE_CASE)
val VEHICLE_REGEX = Regex("([\\wæøå]+([\\s-]+?[\\wæøå]+)?)", RegexOption.IGNORE_CASE)

fun Regex.findGroup(str: String, group: Int): String? {
    return find(str)?.groupValues?.get(group)
}
