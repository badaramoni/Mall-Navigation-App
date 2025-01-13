package org.example.mymap.camera

interface CameraController {
    fun startCamera()
    fun getPreviewView(): Any?
    suspend fun capturePhoto(): Result<ByteArray>
    fun stopCamera()
}

expect fun createCameraController(): CameraController 