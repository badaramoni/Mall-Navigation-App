package org.example.mymap.navigation

actual fun createOrientationManager(): OrientationManager = IosOrientationManager() 