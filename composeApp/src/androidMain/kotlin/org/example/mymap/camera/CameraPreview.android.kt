package org.example.mymap.camera

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView

@Composable
actual fun CameraPreview(
    modifier: Modifier,
    cameraController: CameraController
) {
    DisposableEffect(Unit) {
        cameraController.startCamera()
        onDispose { cameraController.stopCamera() }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { _ ->
            (cameraController.getPreviewView() as PreviewView).apply {
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        }
    )
} 