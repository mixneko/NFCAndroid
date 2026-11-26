package com.qifan.nfcemvread.extensions

/**
 * Created by Qifan on 05/12/2018.
 */

private const val VISIBLE_DIGITS = 4

/**
 * Hides everything but the last four digits. Card numbers vary in length (13 to 19 digits).
 */
fun maskedCardNumber(cardNumber: String?): String? = cardNumber?.let { number ->
    val visible = if (number.length > VISIBLE_DIGITS) number.takeLast(VISIBLE_DIGITS) else ""
    "**** - **** - **** - " + visible.padStart(VISIBLE_DIGITS, '*')
}
