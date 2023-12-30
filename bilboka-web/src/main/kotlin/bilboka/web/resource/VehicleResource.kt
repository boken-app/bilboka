package bilboka.web.resource

import bilboka.client.VehicleResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("vehicles")
class VehicleResource(
) {

    @GetMapping("sample")
    fun sample(): ResponseEntity<List<VehicleResponse>> {
        return ResponseEntity.ok(vehiclesSample().values.toList())
    }

    @GetMapping("{id}/sample")
    fun sampleById(
        @PathVariable id: String
    ): ResponseEntity<VehicleResponse> {
        return vehiclesSample()[id]?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    private fun vehiclesSample() = mapOf(
        "1" to VehicleResponse(
            id = "1",
            name = "Testbil 1",
            tegnkombinasjon = "AB12367",
            odometerUnit = "km",
            fuelType = "BENSIN",
            tankVolume = 50,
        ),
        "2" to VehicleResponse(
            id = "2",
            name = "Testbil 2",
            tegnkombinasjon = "AB12345",
            odometerUnit = "mi",
            fuelType = "DIESEL",
            tankVolume = 70,
        ),
        "3" to VehicleResponse(
            id = "3",
            name = "Testbil 3",
            tegnkombinasjon = "AB12389",
            odometerUnit = "km",
            fuelType = "DIESEL",
            tankVolume = 65,
        ),
    )

}
