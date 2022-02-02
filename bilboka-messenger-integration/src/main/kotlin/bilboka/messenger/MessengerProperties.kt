package bilboka.messenger

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("messenger")
class MessengerProperties {

    lateinit var verifyToken: String
    lateinit var pageAccessToken: String
    lateinit var sendUrl: String

}
