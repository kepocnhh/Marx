package org.kepocnhh.marx.provider

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.entity.remote.ItemsSyncResponse
import java.util.UUID
import java.util.concurrent.TimeUnit

internal class FinalRemotes(
    private val serializer: Serializer,
) : Remotes {
    private val client = OkHttpClient.Builder()
        .callTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    override fun itemsSync(meta: Meta): ItemsSyncResponse {
        val body = serializer.meta.toByteArray(meta).toRequestBody()
        val request = Request.Builder()
            .url("http://192.168.88.225:40631/v1/items/sync") // todo
            .header("Content-Type", "application/json")
            .post(body)
            .build()
        return client.newCall(request).execute().use { response ->
            when (response.code) {
                200 -> {
                    TODO("Download!")
                }
                201 -> {
                    val sessionId = response.header("Session-Id", null)?.let {
                        UUID.fromString(it)
                    } ?: TODO()
                    ItemsSyncResponse.UploadSession(sessionId = sessionId)
                }
                304 -> ItemsSyncResponse.NotModified
                else -> TODO("Unknown code ${response.code}!")
            }
        }
    }

    override fun itemsUpload(sessionId: UUID, bytes: ByteArray) {
        val request = Request.Builder()
            .url("http://192.168.88.225:40631/v1/items") // todo
            .header("Session-Id", sessionId.toString())
            .post(bytes.toRequestBody())
            .build()
        return client.newCall(request).execute().use { response ->
            when (response.code) {
                200 -> {
                    // noop
                }
                else -> TODO("Unknown code ${response.code}!")
            }
        }
    }
}
