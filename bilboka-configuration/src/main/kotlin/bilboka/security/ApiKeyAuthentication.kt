package bilboka.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ApiKeyAuthentication(
    private val user: String,
    authorities: Collection<GrantedAuthority>
) :
    AbstractAuthenticationToken(authorities) {

    // TODO legge bruker/epost her?

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): String {
        return user
    }
}
