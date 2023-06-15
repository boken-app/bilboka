package bilboka.core.book

import bilboka.core.ImpossibleBilbokaActionException

class MaintenanceItemMissingException(maintenanceItem: String) :
    ImpossibleBilbokaActionException("Ingen registrerte vedlikeholdspunkt $maintenanceItem") {
}
