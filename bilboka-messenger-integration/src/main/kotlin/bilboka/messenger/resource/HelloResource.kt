package bilboka.messenger.resource

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class HelloResource {

    @GetMapping
    fun get(): ResponseEntity<String> {
        return ResponseEntity.ok("Hallo fra Bilboka!")
    }
}
