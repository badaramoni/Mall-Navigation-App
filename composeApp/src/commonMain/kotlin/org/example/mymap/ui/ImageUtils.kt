package org.example.mymap.ui

import androidx.compose.ui.graphics.ImageBitmap

expect fun bytesToImageBitmap(bytes: ByteArray): ImageBitmap
expect fun imageToByteArray(image: ImageBitmap): ByteArray 