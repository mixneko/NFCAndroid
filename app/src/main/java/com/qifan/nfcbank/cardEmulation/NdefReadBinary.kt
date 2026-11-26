package com.qifan.nfcbank.cardEmulation

/**
 * Answers a READ BINARY command sent by a card reader. The command comes from outside the app,
 * so its length and offset are checked before they are used.
 */
internal object NdefReadBinary {

    // CLA, INS, P1, P2, Le
    private const val COMMAND_LENGTH = 5

    /**
     * @return the requested part of [file], or null if the command is not a valid read of it.
     */
    fun read(file: ByteArray, command: ByteArray): ByteArray? {
        if (command.size < COMMAND_LENGTH) return null

        val offset = (command[2].toUnsigned() shl 8) or command[3].toUnsigned()
        if (offset > file.size) return null

        val length = command[4].toUnsigned()
        return file.copyOfRange(offset, minOf(offset + length, file.size))
    }

    private fun Byte.toUnsigned() = toInt() and 0xFF
}
