package org.example.mymap.navigation.models

data class Shop(
    val id: String,
    val name: String,
    val x: Int,
    val y: Int,
    val category: ShopCategory
)

data class NavigationEdge(
    val fromShopId: String,
    val toShopId: String,
    val distance: Float
)

data class MallMap(
    val shops: List<Shop>,
    val edges: List<NavigationEdge>
)

enum class ShopCategory {
    CLOTHING,
    FOOD,
    ELECTRONICS,
    ACCESSORIES,
    SERVICES,
    OTHER
}

data class NavigationState(
    val startShop: Shop? = null,
    val destinationShop: Shop? = null,
    val currentPath: List<Point> = emptyList(),
    val pathProgress: Float = 0f
)

data class Point(
    val x: Int,
    val y: Int
)

data class NavigationStep(
    val instruction: String,
    val distance: Float
) 