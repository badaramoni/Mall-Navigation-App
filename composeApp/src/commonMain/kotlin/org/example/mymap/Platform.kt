package org.example.mymap

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform