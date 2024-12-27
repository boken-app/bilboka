package bilboka.messagebot.commands.common

val ODOMETER_REGEX = Regex("([0-9]{5,7})(?:\\s?(?:km|mi))?|(\\d+)\\s?(?:km|mi)", RegexOption.IGNORE_CASE)
val VOLUME_REGEX = Regex("(\\d+(?:[.,]\\d{1,2})?)\\s?l", RegexOption.IGNORE_CASE)
val COST_REGEX = Regex("(\\d+(?:[.,]\\d{1,2})?)\\s?(kr|nok)(?:[^/]|$)", RegexOption.IGNORE_CASE)
val COST_PER_AMOUNT_REGEX = Regex("(\\d+(?:[.,]\\d{1,2})?)\\s?kr/l", RegexOption.IGNORE_CASE)
val VEHICLE_REGEX = Regex("([\\wæøå]+([\\s-]+?[\\wæøå]+)?)", RegexOption.IGNORE_CASE)
