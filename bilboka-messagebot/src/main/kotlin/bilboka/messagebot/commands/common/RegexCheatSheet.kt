package bilboka.messagebot.commands.common

const val VEHICLE_NAME_REGEX = "(\\w+[[\\s-]+\\w+]??)"
const val VEHICLE_NAME_REGEX_GETTER = "(\\w+([\\s-]+?\\w+)?)"
const val ODOMETER_REGEX = "([0-9]{1,7})\\s?(km|mi)?"
const val VOLUME_REGEX = "(\\d+[.|,]?\\d{0,2})\\s?l"
const val COST_REGEX = "(\\d+[.|,]?\\d{0,2})\\s?kr"
