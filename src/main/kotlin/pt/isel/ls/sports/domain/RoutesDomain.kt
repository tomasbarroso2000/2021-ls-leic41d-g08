package pt.isel.ls.sports.domain

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val number: Int,
    val startLocation: String,
    val endLocation: String,
    val distance: Double,
    val user: InteriorData
)

@Serializable
data class RouteInput(
    val startLocation: String? = null,
    val endLocation: String? = null,
    val distance: Double? = null
)

@Serializable
data class RouteUpdateInput(
    val startLocation: String? = null,
    val endLocation: String? = null,
    val distance: Double? = null
)
