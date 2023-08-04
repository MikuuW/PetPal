package com.mikuw.PetPal
import com.mikuw.coupler.UserRegisterActivity
import org.junit.Assert
import org.junit.Test

class UserRegisterActivityTest {


    // Unit tests for UserRegisterActivity.isRegistrationDataValid()
    @Test
    // empty email
    fun isRegistrationValid_emptyEmail() {
        val result = UserRegisterActivity.isRegistrationDataValid("", "password", "password", true)
        // expect false
        Assert.assertFalse(result)
    }

    @Test
    // empty password
    fun isRegistrationValid_emptyPassword() {
        val result = UserRegisterActivity.isRegistrationDataValid("email@example.com", "", "password", true)
        // expect false
        Assert.assertFalse(result)
    }

    @Test
    // empty password confirmation
    fun isRegistrationValid_passwordsDoNotMatch() {
        val result = UserRegisterActivity.isRegistrationDataValid("email@example.com", "password", "differentPassword", true)
        // expect false
        Assert.assertFalse(result)
    }

    @Test
    // password and password confirmation do not match
    fun isRegistrationValid_agbNotAccepted() {
        val result = UserRegisterActivity.isRegistrationDataValid("email@example.com", "password", "password", false)
        // expect false
        Assert.assertFalse(result)
    }

    @Test
    // valid data
    fun isRegistrationValid_validData() {
        val result = UserRegisterActivity.isRegistrationDataValid("email@example.com", "password", "password", true)
        // expect true
        Assert.assertTrue(result)
    }
}
