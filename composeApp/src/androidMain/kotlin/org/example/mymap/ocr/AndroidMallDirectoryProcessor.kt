package org.example.mymap.ocr

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidMallDirectoryProcessor : MallDirectoryProcessor {
    override suspend fun processImage(imageBytes: ByteArray): List<ShopLocation> = suspendCancellableCoroutine { continuation ->
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val shopLocations = visionText.textBlocks.map { block ->
                    val boundingBox = block.boundingBox!!
                    ShopLocation(
                        name = block.text,
                        x = boundingBox.centerX(),
                        y = boundingBox.centerY()
                    )
                }
                continuation.resume(shopLocations)
            }
            .addOnFailureListener {
                continuation.resume(emptyList())
            }
    }
} 