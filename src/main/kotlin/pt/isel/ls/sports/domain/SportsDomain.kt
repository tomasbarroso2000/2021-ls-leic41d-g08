package pt.isel.ls.sports.domain

import kotlinx.serialization.Serializable

@Serializable
data class Sport(
    val number: Int,
    val name: String,
    val description: String? = null,
    val user: InteriorData
)

@Serializable
data class SportUpdateInput(
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class SportInput(
    val name: String? = null,
    val description: String? = null
)
