package org.example.mymap.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.StateFlow
import org.example.mymap.navigation.models.*

class NavigationViewModel(
    val mallMap: MallMap,
    private val orientationManager: OrientationManager
) {
    private val pathFinder = PathFinder(mallMap.shops, mallMap.edges)
    
    var navigationState by mutableStateOf(NavigationState())
        private set
    
    val heading: StateFlow<Float> = orientationManager.heading
    
    fun onStart() {
        orientationManager.startListening()
    }
    
    fun onStop() {
        orientationManager.stopListening()
    }
    
    fun selectStartShop(shop: Shop?) {
        navigationState = navigationState.copy(
            startShop = shop,
            currentPath = emptyList()
        )
        if (shop != null) {
            updatePath()
        }
    }
    
    fun selectDestinationShop(shop: Shop?) {
        navigationState = navigationState.copy(
            destinationShop = shop,
            currentPath = emptyList()
        )
        if (shop != null) {
            updatePath()
        }
    }
    
    private fun updatePath() {
        val start = navigationState.startShop
        val destination = navigationState.destinationShop
        
        if (start != null && destination != null) {
            val path = pathFinder.findShortestPath(start.id, destination.id)
            val points = pathFinder.pathToPoints(path)
            navigationState = navigationState.copy(currentPath = points)
        }
    }
    
    fun getNavigationInstructions(): List<String> {
        val start = navigationState.startShop
        val destination = navigationState.destinationShop
        
        return if (start != null && destination != null) {
            val path = pathFinder.findShortestPath(start.id, destination.id)
            pathFinder.generateNavigationInstructions(path).filter { it.isNotEmpty() }
        } else {
            emptyList()
        }
    }
    
    fun getMaxInstructionWidth(): Int {
        return getNavigationInstructions().maxOfOrNull { it.length } ?: 0
    }
} 