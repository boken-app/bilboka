package bilboka.core.user

import bilboka.core.user.domain.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class UserService {

    fun getUserByRegistration(regTypeID: String, id: String): User? {
        return transaction {
            UserRegistration.find {
                UserRegistrations.registrationTypeID eq regTypeID
                UserRegistrations.registeredID eq id
            }.singleOrNull()?.user
        }
    }

    fun register(regTypeID: String, idToRegister: String, regKey: String) {
        transaction {
            unlockUser(regKey).let {
                registerUserAs(it, regTypeID, idToRegister)
            }
        }
    }

    private fun unlockUser(regKey: String): User {
        val registration = RegistrationKey.find {
            RegistrationKeys.key eq regKey and (RegistrationKeys.isUsable eq true)
        }.singleOrNull()
            ?: throw InvalidRegistrationKeyException("Ugyldig nøkkel angitt")
        registration.isUsable = false
        return registration.user
    }

    private fun registerUserAs(user: User, regTypeID: String, id: String) {
        return transaction {
            UserRegistration.new {
                this.user = user
                this.registrationTypeID = regTypeID
                this.registeredID = id
            }
        }
    }

    fun addUser(username: String): User {
        return transaction {
            User.new {
                this.username = username
            }
        }
    }
}
