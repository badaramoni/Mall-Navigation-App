package org.example.mymap.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.mymap.camera.CameraController
import org.example.mymap.camera.CameraPreview
import org.example.mymap.camera.createCameraController
import org.example.mymap.ocr.createMallDirectoryProcessor
import org.example.mymap.ocr.ShopLocation
import org.example.mymap.navigation.models.Shop
import org.example.mymap.navigation.models.ShopCategory
import org.example.mymap.ocr.MallDirectoryProcessor

@Composable
fun MallDirectoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNavigation: (List<Shop>) -> Unit
) {
    var capturedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var shopLocations by remember { mutableStateOf<List<ShopLocation>>(emptyList()) }
    val cameraController = remember { createCameraController() }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Mall Directory Scanner") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Add back icon here
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (capturedImage == null) {
                // Camera Preview
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    cameraController = cameraController
                )
                
                // Capture Button
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val result = cameraController.capturePhoto()
                                result.onSuccess { bytes ->
                                    capturedImage = bytesToImageBitmap(bytes)
                                }.onFailure { error ->
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        "Failed to capture photo: ${error.message}"
                                    )
                                }
                            } catch (e: Exception) {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "Error capturing photo: ${e.message}"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Capture Directory")
                }
            } else {
                // Show captured image and processing UI
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display captured image
                    Image(
                        bitmap = capturedImage!!,
                        contentDescription = "Captured mall directory",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    
                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { 
                                capturedImage = null
                                shopLocations = emptyList()
                            }
                        ) {
                            Text("Retake")
                        }
                        
                        Button(
                            onClick = {
                                scope.launch {
                                    isProcessing = true
                                    try {
                                        val processor = createMallDirectoryProcessor()
                                        val imageBytes = imageToByteArray(capturedImage!!)
                                        shopLocations = processor.processImage(imageBytes)
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Successfully processed directory"
                                        )
                                    } catch (e: Exception) {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Error processing image: ${e.message}"
                                        )
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            },
                            enabled = !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colors.onPrimary
                                )
                            } else {
                                Text("Process Directory")
                            }
                        }
                    }
                    
                    // Show detected shops
                    if (shopLocations.isNotEmpty()) {
                        Button(
                            onClick = {
                                // Convert ShopLocation to Shop
                                val shops = shopLocations.mapIndexed { index, location ->
                                    Shop(
                                        id = "shop_$index",
                                        name = location.name,
                                        x = location.x,
                                        y = location.y,
                                        category = ShopCategory.OTHER // Default category, can be enhanced with ML recognition
                                    )
                                }
                                onNavigateToNavigation(shops)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text("Start Navigation")
                        }
                        
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            items(shopLocations) { shop ->
                                ShopLocationItem(shop)
                            }
                        }
                    }
                }
            }
            
            // Loading indicator
            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ShopLocationItem(shop: ShopLocation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = shop.name,
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = "x: ${shop.x}, y: ${shop.y}",
                style = MaterialTheme.typography.caption
            )
        }
    }
} 