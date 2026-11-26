package com.qifan.nfcemvread

import com.qifan.nfcemvread.extensions.maskedCardNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MaskedCardNumberTest {

    @Test
    fun showsOnlyLastFourDigits() {
        assertEquals("**** - **** - **** - 1111", maskedCardNumber("4111111111111111"))
        assertEquals("**** - **** - **** - 0005", maskedCardNumber("378282246310005"))
    }

    @Test
    fun hidesNumbersTooShortToMask() {
        assertEquals("**** - **** - **** - ****", maskedCardNumber("1234"))
        assertEquals("**** - **** - **** - ****", maskedCardNumber(""))
    }

    @Test
    fun keepsMissingNumberMissing() {
        assertNull(maskedCardNumber(null))
    }
}
