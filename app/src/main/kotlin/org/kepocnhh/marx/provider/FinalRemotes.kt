package org.kepocnhh.marx.provider

import okhttp3.OkHttpClient
import okhttp3.Request
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.entity.remote.ItemsSyncResponse
import java.util.concurrent.TimeUnit

internal class FinalRemotes : Remotes {
    private val client = OkHttpClient.Builder()
        .callTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()
    override fun itemsSync(meta: Meta): ItemsSyncResponse {
        val request = Request.Builder()
            .url("http://192.168.88.225:40631/v1/items/sync") // todo
            .build()
        return client.newCall(request).execute().use {
            when (it.code) {
                200 -> {
                    TODO("Download!")
                }
                201 -> {
                    TODO("Upload session!")
                }
                304 -> ItemsSyncResponse.NotModified
                else -> TODO("Unknown code ${it.code}!")
            }
        }
    }
}
