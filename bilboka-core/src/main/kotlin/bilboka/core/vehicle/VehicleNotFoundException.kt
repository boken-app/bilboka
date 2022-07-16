package bilboka.core.vehicle

class VehicleNotFoundException(message: String, val vehicleName: String) : RuntimeException(message) {
}
