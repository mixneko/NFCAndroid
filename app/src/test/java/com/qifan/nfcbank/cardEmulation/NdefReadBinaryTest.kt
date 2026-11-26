package com.qifan.nfcbank.cardEmulation

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NdefReadBinaryTest {

    private val file = byteArrayOf(1, 2, 3, 4, 5)

    private fun command(offset: Int, length: Int) = byteArrayOf(
        0x00,
        0xB0.toByte(),
        (offset shr 8).toByte(),
        offset.toByte(),
        length.toByte(),
    )

    @Test
    fun readsRequestedRange() {
        assertArrayEquals(byteArrayOf(2, 3), NdefReadBinary.read(file, command(1, 2)))
    }

    @Test
    fun stopsAtEndOfFile() {
        assertArrayEquals(byteArrayOf(4, 5), NdefReadBinary.read(file, command(3, 255)))
        assertArrayEquals(byteArrayOf(), NdefReadBinary.read(file, command(5, 1)))
    }

    @Test
    fun rejectsOffsetPastEndOfFile() {
        assertNull(NdefReadBinary.read(file, command(6, 1)))
        assertNull(NdefReadBinary.read(file, command(0xFFFF, 1)))
    }

    @Test
    fun rejectsShortCommand() {
        assertNull(NdefReadBinary.read(file, byteArrayOf()))
        assertNull(NdefReadBinary.read(file, byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00)))
    }
}
