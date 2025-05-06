package com.example.csci571hw4.network.cookie

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(context: Context) : CookieJar {
    private val prefs = context.getSharedPreferences("cookies", Context.MODE_PRIVATE)
    private val json = Json

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val editor = prefs.edit()
        cookies.forEach { cookie ->
            val key = "${url.host}_${cookie.name}"
            val serialized = json.encodeToString(cookie.toSerializable())
            editor.putString(key, serialized)
        }
        editor.apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return prefs.all.mapNotNull { (key, value) ->
            try {
                if (key.startsWith(url.host)) {
                    val sCookie = json.decodeFromString<SerializableCookie>(value as String)
                    val cookie = sCookie.toCookie()
                    if (cookie.expiresAt > System.currentTimeMillis()) cookie else null
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
