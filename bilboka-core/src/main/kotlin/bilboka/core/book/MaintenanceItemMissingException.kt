package bilboka.core.book

class MaintenanceItemMissingException(maintenanceItem: String) :
    RuntimeException("Ingen registrerte vedlikeholdspunkt $maintenanceItem") {
}
