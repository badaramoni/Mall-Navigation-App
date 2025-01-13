package org.example.mymap.navigation

import kotlinx.coroutines.flow.StateFlow

interface OrientationManager {
    val heading: StateFlow<Float>
    fun startListening()
    fun stopListening()
}

expect fun createOrientationManager(): OrientationManager 