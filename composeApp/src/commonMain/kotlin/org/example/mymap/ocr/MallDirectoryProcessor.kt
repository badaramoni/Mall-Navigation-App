package org.example.mymap.ocr

interface MallDirectoryProcessor {
    suspend fun processImage(imageBytes: ByteArray): List<ShopLocation>
} 