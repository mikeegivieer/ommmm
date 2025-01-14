package com.dutisoft.ommmm

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ValidationUtils {
    @Test
    fun `isValidEmail should return true for valid email`() {
        val validEmail = "test@example.com"
        assertTrue(isValidEmail(validEmail))
    }

    @Test
    fun `isValidEmail should return false for invalid email`() {
        val invalidEmail = "invalid-email"
        assertFalse(isValidEmail(invalidEmail))
    }

    @Test
    fun `isValidPassword should return true for strong password`() {
        val strongPassword = "Strong123"
        assertTrue(isValidPassword(strongPassword))
    }

    @Test
    fun `isValidPassword should return false for password without uppercase`() {
        val noUppercasePassword = "weakpassword1"
        assertFalse(isValidPassword(noUppercasePassword))
    }

    @Test
    fun `isValidPassword should return false for password without number`() {
        val noNumberPassword = "WeakPassword"
        assertFalse(isValidPassword(noNumberPassword))
    }

    @Test
    fun `isValidPassword should return false for short password`() {
        val shortPassword = "S123"
        assertFalse(isValidPassword(shortPassword))
    }
}