import com.mikuw.coupler.UserRegisterActivity
import org.junit.Assert
import org.junit.Test

class UserRegisterActivityTest {

    @Test
    fun isRegistrationDataValid_emptyEmail_returnsFalse() {
        val result = UserRegisterActivity.isRegistrationDataValid("", "password", "password", true)
        Assert.assertFalse(result)
    }

    @Test
    fun isRegistrationDataValid_emptyPassword_returnsFalse() {
        val result = UserRegisterActivity.isRegistrationDataValid("email@example.com", "", "password", true)
        Assert.assertFalse(result)
    }

    @Test
    fun isRegistrationDataValid_passwordsDoNotMatch_returnsFalse() {
        val result = UserRegisterActivity.isRegistrationDataValid("email@example.com", "password", "differentPassword", true)
        Assert.assertFalse(result)
    }

    @Test
    fun isRegistrationDataValid_agbNotAccepted_returnsFalse() {
        val result = UserRegisterActivity.isRegistrationDataValid("email@example.com", "password", "password", false)
        Assert.assertFalse(result)
    }

    @Test
    fun isRegistrationDataValid_validData_returnsTrue() {
        val result = UserRegisterActivity.isRegistrationDataValid("email@example.com", "password", "password", true)
        Assert.assertTrue(result)
    }
}
