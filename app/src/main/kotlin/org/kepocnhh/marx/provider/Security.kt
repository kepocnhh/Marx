package org.kepocnhh.marx.provider

internal interface Security {
    fun sha256(bytes: ByteArray): ByteArray
    fun base64(bytes: ByteArray): String
}
