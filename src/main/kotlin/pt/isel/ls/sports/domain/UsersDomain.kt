package pt.isel.ls.sports.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val number: Int,
    val name: String,
    val email: String
)

@Serializable
data class UserInput(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
)

@Serializable
data class UserOutput(
    val token: String,
    val number: Int
)

@Serializable
data class UserCredentials(
    val email: String,
    val password: String
)

@Serializable
data class Session(
    val number: Int,
    val name: String,
    val token: String
)
