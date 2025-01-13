package org.example.mymap.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun bytesToImageBitmap(bytes: ByteArray): ImageBitmap {
    return Image.makeFromEncoded(bytes).toComposeImageBitmap()
}

actual fun imageToByteArray(image: ImageBitmap): ByteArray {
    // TODO: Implement iOS image conversion
    return ByteArray(0)
} 