package pt.isel.ls.sports.webapi.comp

import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import org.http4k.core.Request
import org.http4k.routing.path
import pt.isel.ls.sports.domain.Order
import pt.isel.ls.sports.domain.UserCredentials
import java.util.Base64

/**
 * Name of the parameter that defines an object id
 */
private const val ID_PARAM = "number"

/**
 * Used to decode Authorization header
 */
val base64Decoder: Base64.Decoder = Base64.getDecoder()

/**
 * Extension function of Request that gets the user token from the Authorization header
 * @return the token
 */
val Request.token: String?
    get() {
        val auth = header("Authorization")
        if (auth != null) {
            val authData = auth.trim().split(' ')
            if (authData[0] == "Bearer") {
                return authData[1]
            }
        }
        return null
    }

/**
 * Extension function of Request gets the email and password from the Authorization header
 * @return the user's credentials
 */
val Request.credentials: UserCredentials?
    get() {
        val auth = header("Authorization")
        if (auth != null) {
            val authData = auth.trim().split(' ')
            if (authData[0] == "Basic") {
                val encoded = authData[1]
                val decoded = String(base64Decoder.decode(encoded))
                val credentials = decoded.split(':')
                return UserCredentials(credentials[0], credentials[1])
            }
        }
        return null
    }

/**
 * Gets a number from the query or null if the query doesn't exist
 * or if the query's value is not a number
 * @param query the query
 * @return the value of the given query
 */
fun Request.queryToInt(query: String): Int? =
    query(query)?.toIntOrNull()

/**
 * Gets a number from the parameter or null if the parameter doesn't exist
 * or if the parameter's value is not a number
 * @param param the parameter
 * @return the value of the given parameter
 */
fun Request.paramToInt(param: String): Int? =
    path(param)?.toIntOrNull()

/**
 * Extension function of Request that gets the limit value from the query
 * @return the limit
 */
val Request.limit: Int
    get() = queryToInt("limit") ?: 3

/**
 * Extension function of Request that gets the skip value from the query
 * @return the skip value
 */
val Request.skip: Int
    get() = queryToInt("skip") ?: 0

/**
 * Extension function of Request that gets the id from the path
 * @return the id
 */
val Request.id: Int?
    get() = paramToInt(ID_PARAM)

/**
 * Extension function of Request that gets the search query from the query
 * @return the search query
 */
val Request.searchQuery: String?
    get() = query("q")?.replace('+', ' ')

/**
 * Extension function of Request that gets the order from the query
 * @return the order
 */
val Request.order: Order
    get() = query("order").toOrder()

/**
 * Extension function of Request that gets the date from the query
 * @return the date
 */
val Request.date: LocalDate?
    get() = query("date")?.toLocalDateOrNull()

/**
 * Extension function of Request that gets the body object
 * @return the object id
 */
inline fun <reified T> Request.getBodyObject() =
    json.decodeFromString<T>(bodyString())
