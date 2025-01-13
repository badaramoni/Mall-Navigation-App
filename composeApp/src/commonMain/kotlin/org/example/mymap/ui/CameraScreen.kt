package org.example.mymap.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.example.mymap.camera.CameraPreview
import org.example.mymap.camera.createCameraController

@Composable
fun CameraScreen() {
    val cameraController = remember { createCameraController() }
    
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            cameraController = cameraController
        )
    }
} 