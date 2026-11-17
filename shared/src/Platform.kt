package com.example.aip_les_9

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
