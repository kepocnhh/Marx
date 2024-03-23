package org.kepocnhh.marx.provider

import android.util.Base64
import java.security.MessageDigest

internal class FinalSecurity : Security {
    private val md = MessageDigest.getInstance("SHA-256")

    override fun sha256(bytes: ByteArray): ByteArray {
        return md.digest(bytes)
    }

    override fun base64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
