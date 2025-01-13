package org.example.mymap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.mymap.ui.MallDirectoryScreen
import org.example.mymap.ui.NavigationScreen
import org.example.mymap.navigation.models.Shop
import org.example.mymap.navigation.models.MallMap
import org.example.mymap.navigation.NavigationViewModel
import org.example.mymap.navigation.OrientationManager
import org.example.mymap.navigation.createOrientationManager

@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
            var mallMap by remember { mutableStateOf<MallMap?>(null) }
            
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { currentScreen = Screen.MallDirectory },
                            modifier = Modifier
                                .padding(16.dp)
                                .size(width = 200.dp, height = 56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Scan Mall Directory")
                        }
                    }
                }
                is Screen.MallDirectory -> {
                    MallDirectoryScreen(
                        onNavigateBack = { currentScreen = Screen.Home },
                        onNavigateToNavigation = { shops ->
                            mallMap = MallMap(shops = shops, edges = emptyList())
                            currentScreen = Screen.Navigation
                        }
                    )
                }
                is Screen.Navigation -> {
                    mallMap?.let { map ->
                        val viewModel = remember { NavigationViewModel(map, createOrientationManager()) }
                        NavigationScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = Screen.MallDirectory }
                        )
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object Home : Screen()
    object MallDirectory : Screen()
    object Navigation : Screen()
}