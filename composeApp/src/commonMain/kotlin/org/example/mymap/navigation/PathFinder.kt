package org.example.mymap.navigation

import org.example.mymap.navigation.models.*
import kotlin.math.sqrt

class PathFinder(
    private val shops: List<Shop>,
    private val edges: List<NavigationEdge>
) {
    private val adjacencyList: Map<String, List<Pair<String, Float>>> = buildAdjacencyList()
    
    private fun buildAdjacencyList(): Map<String, List<Pair<String, Float>>> {
        val adjacencyMap = mutableMapOf<String, MutableList<Pair<String, Float>>>()
        
        edges.forEach { edge ->
            // Add forward direction
            adjacencyMap.getOrPut(edge.fromShopId) { mutableListOf() }
                .add(edge.toShopId to edge.distance)
            
            // Add reverse direction (assuming bidirectional)
            adjacencyMap.getOrPut(edge.toShopId) { mutableListOf() }
                .add(edge.fromShopId to edge.distance)
        }
        
        return adjacencyMap
    }
    
    fun findShortestPath(start: String, end: String): List<String> {
        val distances = mutableMapOf<String, Float>()
        val previous = mutableMapOf<String, String>()
        val unvisited = mutableSetOf<String>()
        
        // Initialize distances
        shops.forEach { shop ->
            distances[shop.id] = Float.POSITIVE_INFINITY
            unvisited.add(shop.id)
        }
        distances[start] = 0f
        
        while (unvisited.isNotEmpty()) {
            val current = unvisited.minByOrNull { distances[it] ?: Float.POSITIVE_INFINITY }
                ?: break
            
            if (current == end) break
            
            unvisited.remove(current)
            
            adjacencyList[current]?.forEach { (neighbor, distance) ->
                if (neighbor in unvisited) {
                    val newDistance = distances[current]!! + distance
                    if (newDistance < distances[neighbor]!!) {
                        distances[neighbor] = newDistance
                        previous[neighbor] = current
                    }
                }
            }
        }
        
        // Reconstruct path
        val path = mutableListOf<String>()
        var current = end
        while (current != start) {
            path.add(0, current)
            current = previous[current] ?: break
        }
        path.add(0, start)
        
        return path
    }
    
    fun pathToPoints(path: List<String>): List<Point> {
        return path.map { shopId ->
            val shop = shops.first { it.id == shopId }
            Point(shop.x, shop.y)
        }
    }
    
    fun generateNavigationInstructions(path: List<String>): List<String> {
        if (path.size < 2) return emptyList()
        
        val instructions = mutableListOf<String>()
        
        path.zipWithNext { currentId, nextId ->
            val currentShop = shops.first { it.id == currentId }
            val nextShop = shops.first { it.id == nextId }
            val distance = calculateDistance(currentShop, nextShop)
            instructions.add("Head to ${nextShop.name} (${distance.toInt()}m)")
        }
        
        return instructions
    }
    
    private fun calculateDistance(shop1: Shop, shop2: Shop): Float {
        val dx = shop2.x - shop1.x
        val dy = shop2.y - shop1.y
        return sqrt((dx * dx + dy * dy).toFloat())
    }
} 