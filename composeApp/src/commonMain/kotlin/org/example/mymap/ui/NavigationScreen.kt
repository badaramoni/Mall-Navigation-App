package org.example.mymap.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.example.mymap.navigation.NavigationViewModel
import org.example.mymap.navigation.models.Shop
import androidx.compose.foundation.clickable

@Composable
fun NavigationScreen(
    viewModel: NavigationViewModel,
    onNavigateBack: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableStateOf(0f) }
    var showStartDropdown by remember { mutableStateOf(false) }
    var showDestinationDropdown by remember { mutableStateOf(false) }
    
    val textMeasurer = rememberTextMeasurer()
    
    // Add smooth rotation transition
    val smoothRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessLow
        )
    )
    
    // Function to get emoji for shop name
    fun getShopEmoji(name: String): String {
        return when {
            name.contains("food", ignoreCase = true) || 
            name.contains("cafe", ignoreCase = true) || 
            name.contains("restaurant", ignoreCase = true) -> "🍽️"
            
            name.contains("cloth", ignoreCase = true) || 
            name.contains("wear", ignoreCase = true) || 
            name.contains("fashion", ignoreCase = true) -> "👕"
            
            name.contains("shoe", ignoreCase = true) -> "👟"
            
            name.contains("book", ignoreCase = true) || 
            name.contains("library", ignoreCase = true) -> "📚"
            
            name.contains("tech", ignoreCase = true) || 
            name.contains("phone", ignoreCase = true) || 
            name.contains("computer", ignoreCase = true) -> "💻"
            
            name.contains("toy", ignoreCase = true) -> "🧸"
            
            name.contains("jewelry", ignoreCase = true) || 
            name.contains("accessory", ignoreCase = true) -> "💍"
            
            name.contains("sport", ignoreCase = true) || 
            name.contains("gym", ignoreCase = true) -> "🏃"
            
            name.contains("beauty", ignoreCase = true) || 
            name.contains("salon", ignoreCase = true) -> "💇"
            
            else -> "🏪"
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.heading.collectLatest { heading ->
            rotation = heading
        }
    }
    
    DisposableEffect(Unit) {
        viewModel.onStart()
        onDispose {
            viewModel.onStop()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mall Navigation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, _ ->
                            val oldScale = scale
                            scale = (scale * zoom).coerceIn(0.5f, 3f)
                            
                            // Adjust offset to keep the centroid point fixed while zooming
                            val scaleChange = scale - oldScale
                            offset = if (zoom != 1f) {
                                offset + (centroid / oldScale) * -scaleChange
                            } else {
                                offset + pan / scale
                            }
                        }
                    }
            ) {
                translate(offset.x * scale, offset.y * scale) {
                    scale(scale) {
                        rotate(smoothRotation) {
                            // Draw background
                            drawRect(
                                color = Color(0xFFF5F5F5),
                                size = size
                            )
                            
                            // Draw grid lines
                            val gridSize = 50f
                            val rows = (size.height / gridSize).toInt()
                            val cols = (size.width / gridSize).toInt()
                            
                            for (i in 0..rows) {
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(0f, i * gridSize),
                                    end = Offset(size.width, i * gridSize),
                                    strokeWidth = 1f
                                )
                            }
                            
                            for (i in 0..cols) {
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(i * gridSize, 0f),
                                    end = Offset(i * gridSize, size.height),
                                    strokeWidth = 1f
                                )
                            }
                            
                            // Draw paths with adjusted stroke width for scale
                            viewModel.navigationState.currentPath.zipWithNext { a, b ->
                                drawLine(
                                    color = Color.Blue,
                                    start = Offset(a.x.toFloat(), a.y.toFloat()),
                                    end = Offset(b.x.toFloat(), b.y.toFloat()),
                                    strokeWidth = 5f / scale
                                )
                            }
                            
                            // Draw all shops with rectangles and names
                            viewModel.mallMap.shops.forEach { shop ->
                                val color = when {
                                    shop == viewModel.navigationState.startShop -> Color.Green
                                    shop == viewModel.navigationState.destinationShop -> Color.Red
                                    else -> Color.Gray
                                }
                                
                                val shopX = shop.x.toFloat()
                                val shopY = shop.y.toFloat()
                                
                                // Draw shop rectangle with adjusted size for scale
                                drawRect(
                                    color = color.copy(alpha = 0.2f),
                                    topLeft = Offset(shopX - 40f, shopY - 30f),
                                    size = Size(80f, 60f)
                                )
                                
                                // Draw shop marker with adjusted radius for scale
                                drawCircle(
                                    color = color,
                                    radius = 20f,
                                    center = Offset(shopX, shopY)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 15f,
                                    center = Offset(shopX, shopY)
                                )
                                
                                // Calculate visible bounds for the scaled coordinate system
                                val visibleAreaWidth = size.width
                                val visibleAreaHeight = size.height
                                
                                val isVisible = shopX >= -offset.x &&
                                        shopX <= -offset.x + visibleAreaWidth &&
                                        shopY >= -offset.y &&
                                        shopY <= -offset.y + visibleAreaHeight
                                
                                if (isVisible) {
                                    // Draw emoji with fixed screen-space size
                                    val emoji = getShopEmoji(shop.name)
                                    val emojiStyle = TextStyle(fontSize = 16.sp)
                                    val emojiMeasureResult = textMeasurer.measure(
                                        text = emoji,
                                        style = emojiStyle
                                    )
                                    
                                    // Calculate emoji position
                                    val emojiX = shopX - (emojiMeasureResult.size.width / 2f)
                                    val emojiY = shopY - (emojiMeasureResult.size.height / 2f)
                                    
                                    // Check if emoji is within valid bounds
                                    if (emojiX >= 0 && 
                                        emojiX + emojiMeasureResult.size.width <= size.width &&
                                        emojiY >= 0 && 
                                        emojiY + emojiMeasureResult.size.height <= size.height) {
                                        drawText(
                                            textMeasurer = textMeasurer,
                                            text = emoji,
                                            style = emojiStyle,
                                            topLeft = Offset(emojiX, emojiY)
                                        )
                                    }
                                    
                                    // Draw shop name with fixed screen-space size
                                    val nameStyle = TextStyle(
                                        color = color,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    val nameText = textMeasurer.measure(
                                        text = shop.name,
                                        style = nameStyle
                                    )
                                    
                                    // Calculate name position
                                    val nameX = shopX - (nameText.size.width / 2f)
                                    val nameY = shopY + 25f
                                    
                                    // Check if name is within valid bounds
                                    if (nameX >= 0 && 
                                        nameX + nameText.size.width <= size.width &&
                                        nameY >= 0 && 
                                        nameY + nameText.size.height <= size.height) {
                                        drawText(
                                            textMeasurer = textMeasurer,
                                            text = shop.name,
                                            style = nameStyle,
                                            topLeft = Offset(nameX, nameY)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Search bars at the top
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                elevation = 8.dp,
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Start location dropdown
                    Box {
                        OutlinedTextField(
                            value = viewModel.navigationState.startShop?.name ?: "",
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStartDropdown = true },
                            label = { Text("Choose starting point") },
                            enabled = false,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray
                            )
                        )
                        DropdownMenu(
                            expanded = showStartDropdown,
                            onDismissRequest = { showStartDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            viewModel.mallMap.shops.forEach { shop ->
                                DropdownMenuItem(
                                    onClick = {
                                        viewModel.selectStartShop(shop)
                                        showStartDropdown = false
                                    }
                                ) {
                                    Text(shop.name)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Destination dropdown
                    Box {
                        OutlinedTextField(
                            value = viewModel.navigationState.destinationShop?.name ?: "",
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDestinationDropdown = true },
                            label = { Text("Choose destination") },
                            enabled = false,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray
                            )
                        )
                        DropdownMenu(
                            expanded = showDestinationDropdown,
                            onDismissRequest = { showDestinationDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            viewModel.mallMap.shops.forEach { shop ->
                                DropdownMenuItem(
                                    onClick = {
                                        viewModel.selectDestinationShop(shop)
                                        showDestinationDropdown = false
                                    }
                                ) {
                                    Text(shop.name)
                                }
                            }
                        }
                    }
                }
            }

            // Navigation Instructions at the bottom
            if (viewModel.navigationState.startShop != null && 
                viewModel.navigationState.destinationShop != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    elevation = 8.dp,
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Navigation Instructions",
                                style = MaterialTheme.typography.h6
                            )
                            Button(
                                onClick = {
                                    viewModel.selectStartShop(null)
                                    viewModel.selectDestinationShop(null)
                                }
                            ) {
                                Text("Reset")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        viewModel.getNavigationInstructions().forEach { instruction ->
                            Text(
                                text = instruction,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.body1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
} 