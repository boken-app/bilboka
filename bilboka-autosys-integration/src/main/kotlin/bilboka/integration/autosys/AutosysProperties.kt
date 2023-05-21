package bilboka.integration.autosys

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("autosys")
class AutosysProperties {

    var akfDatautleveringUrl: String? = null
    var apiKey: String? = null

}
