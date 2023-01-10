package bilboka.core.user

import bilboka.core.H2Test
import bilboka.core.user.domain.RegistrationKey
import bilboka.core.user.domain.User
import bilboka.core.user.domain.Users
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class UserRegistrationIT : H2Test() {

    private val userService = UserService()

    private val testusername = "bruker_test"
    private val theKey = "some-random-key"
    private val justNow = Instant.now()

    @BeforeEach
    fun initRegKey() {
        transaction {
            userService.addUser(testusername)
            RegistrationKey.new {
                this.user = User.find { Users.username eq testusername }.single()
                this.key = theKey
            }
        }
    }

    @Test
    fun registerUser_ableToGetUserByRegistration() {
        val id = "2345"
        userService.register("fb_messenger", id, theKey)

        val userByRegistration = userService.getUserByRegistration("fb_messenger", id)
        assertThat(userByRegistration?.username).isEqualTo(testusername)
        assertThat(userByRegistration?.creationTimestamp).isAfterOrEqualTo(justNow)
        assertThat(userByRegistration?.getIDRegisteredFor("fb_messenger")).isEqualTo(id)
    }

    @Test
    fun wrongKey_throwsExceptionAndDoesNotRegister() {
        val id = "2345"

        assertThrows<InvalidRegistrationKeyException> {
            userService.register("fb_messenger", id, "wrongKey")
        }

        assertThat(userService.getUserByRegistration("fb_messenger", id)).isNull()
    }

    @Test
    fun sameKeyCanNotBeUsedTwice() {
        val id = "2345"

        userService.register("fb_messenger", id, theKey)
        assertThrows<InvalidRegistrationKeyException> {
            userService.register("fb_messenger", id, theKey)
        }

        val userByRegistration = userService.getUserByRegistration("fb_messenger", id)
        assertThat(userByRegistration?.username).isEqualTo(testusername)
    }
}
