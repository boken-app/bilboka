package bilboka.client

data class VehicleResponse(
    val id: String,
    val name: String,
    val tegnkombinasjon: String,
    val odometerUnit: String,
    val fuelType: String,
    val tankVolume: Int,
)
