package pt.isel.ls.sports.domain

import kotlinx.serialization.Serializable

enum class Order {
    ASCENDING,
    DESCENDING
}

@Serializable
data class ListOfData<T>(val list: List<T>, val hasMore: Boolean)

@Serializable
data class DataOutput(val number: Int)

@Serializable
data class InteriorData(val number: Int?, val name: String?)
