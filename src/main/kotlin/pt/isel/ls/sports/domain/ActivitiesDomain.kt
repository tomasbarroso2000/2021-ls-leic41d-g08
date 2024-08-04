package pt.isel.ls.sports.domain

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    val number: Int,
    val date: LocalDate,
    val duration: DateTimePeriod,
    val user: InteriorData,
    val sport: InteriorData,
    val route: InteriorData? = null
)

@Serializable
data class ActivityInput(
    val date: LocalDate? = null,
    val duration: DateTimePeriod? = null,
    val routeNumber: Int? = null
)

@Serializable
data class ActivityUpdateInput(
    val date: LocalDate? = null,
    val duration: DateTimePeriod? = null,
)

@Serializable
data class ActivityDeleteInput(
    val activities: List<Int>? = null
)

@Serializable
data class ActivitiesOutput(
    val numbers: List<Int>
)
