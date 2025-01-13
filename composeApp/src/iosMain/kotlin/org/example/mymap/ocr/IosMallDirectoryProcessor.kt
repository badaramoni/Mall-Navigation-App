package org.example.mymap.ocr

class IosMallDirectoryProcessor : MallDirectoryProcessor {
    override suspend fun processImage(imageBytes: ByteArray): List<ShopLocation> {
        // TODO: Implement iOS text recognition
        return emptyList()
    }
} 