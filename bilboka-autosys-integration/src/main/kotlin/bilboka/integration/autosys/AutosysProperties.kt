package bilboka.integration.autosys

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("autosys")
class AutosysProperties {

    lateinit var akfDatautleveringUrl: String
    lateinit var apiKey: String

}
