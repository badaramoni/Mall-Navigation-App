package org.example.mymap.camera

import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.mymap.MainActivity
import java.nio.ByteBuffer
import kotlin.coroutines.resume

class AndroidCameraController : CameraController {
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewView: PreviewView? = null
    private var isInitialized = false

    init {
        val context = MainActivity.instance ?: throw IllegalStateException("MainActivity not initialized")
        previewView = PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    override fun getPreviewView(): Any {
        return previewView ?: throw IllegalStateException("PreviewView not initialized")
    }

    override suspend fun capturePhoto(): Result<ByteArray> = suspendCancellableCoroutine { continuation ->
        val imageCapture = this.imageCapture ?: run {
            continuation.resume(Result.failure(IllegalStateException("Camera not initialized")))
            return@suspendCancellableCoroutine
        }

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(MainActivity.instance!!),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        image.close()
                        continuation.resume(Result.success(bytes))
                    } catch (e: Exception) {
                        continuation.resume(Result.failure(e))
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraController", "Photo capture failed", exception)
                    continuation.resume(Result.failure(exception))
                }
            }
        )
    }

    override fun stopCamera() {
        try {
            cameraProvider?.unbindAll()
            camera = null
            cameraProvider = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("CameraController", "Error stopping camera", e)
        }
    }

    override fun startCamera() {
        if (isInitialized) return
        isInitialized = true
        
        val context = MainActivity.instance ?: return
        val localPreviewView = previewView ?: return
        
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                
                // Unbind any existing use cases before rebinding
                cameraProvider?.unbindAll()

                // Create preview use case
                val preview = Preview.Builder()
                    .build()
                
                preview.setSurfaceProvider(localPreviewView.surfaceProvider)

                // Initialize image capture use case
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)  // Changed to maximize quality
                    .build()

                // Select back camera as default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Bind use cases to camera
                    camera = cameraProvider?.bindToLifecycle(
                        context,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    Log.d("CameraController", "Camera provider bound successfully")
                } catch (e: Exception) {
                    Log.e("CameraController", "Use case binding failed", e)
                }
            } catch (e: Exception) {
                Log.e("CameraController", "Failed to get camera provider", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
} 