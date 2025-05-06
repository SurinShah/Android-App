package com.example.csci571hw4.network.cookie

import kotlinx.serialization.Serializable
import okhttp3.Cookie

@Serializable
data class SerializableCookie(
    val name: String,
    val value: String,
    val domain: String,
    val path: String,
    val expiresAt: Long,
    val secure: Boolean,
    val httpOnly: Boolean,
    val hostOnly: Boolean
)

fun Cookie.toSerializable(): SerializableCookie = SerializableCookie(
    name = name,
    value = value,
    domain = domain,
    path = path,
    expiresAt = expiresAt,
    secure = secure,
    httpOnly = httpOnly,
    hostOnly = hostOnly
)

fun SerializableCookie.toCookie(): Cookie = Cookie.Builder()
    .name(name)
    .value(value)
    .path(path)
    .expiresAt(expiresAt)
    .apply {
        if (hostOnly) hostOnlyDomain(domain) else domain(domain)
        if (secure) secure()
        if (httpOnly) httpOnly()
    }
    .build()
