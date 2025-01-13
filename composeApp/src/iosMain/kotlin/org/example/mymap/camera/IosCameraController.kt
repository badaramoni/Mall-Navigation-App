package org.example.mymap.camera

class IosCameraController : CameraController {
    override fun startCamera() {
        // iOS implementation
    }

    override fun getPreviewView(): Any? {
        // iOS implementation
        return null
    }
}

actual fun createCameraController(): CameraController = IosCameraController() 