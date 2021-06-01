package bilboka.messenger.integration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@SpringBootApplication
class MessengerWebhook {

    @GetMapping("webhook")
    fun test(): String = "Hei"


}