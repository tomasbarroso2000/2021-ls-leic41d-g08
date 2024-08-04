package pt.isel.ls.sports.webapi.comp

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import pt.isel.ls.sports.domain.Order
import pt.isel.ls.sports.services.comp.AppException
import pt.isel.ls.sports.services.comp.AppExceptionStatus

/**
 * Object that contains the possible error codes
 */
object ErrorCode {
    const val BAD_REQUEST = 1000
    const val UNAUTHENTICATED = 1001
    const val NOT_FOUND = 1002
    const val INTERNAL_ERROR = 1003
}

/**
 * Class that represents the errors
 * shown to the user as Json objects
 * @property errorCode the code of the error
 * @property errorMessage the message of the error
 */
@Serializable
data class Error(
    val errorCode: Int,
    val errorMessage: String
)

/**
 * Calls a task using the same try-catch block
 * @param task the task to be called
 * @return the response
 */
fun doApiTask(task: () -> Response): Response {
    return try {
        task()
    } catch (error: Exception) {
        handleError(error)
    }
}

/**
 * Prints in the console the request made
 * @param request the request made
 */
fun printRequest(request: Request) {
    println("Method ${request.method}")
    println("Uri ${request.uri}")
    println("Header content-type ${request.header("content-type")}")
    println("Header accept ${request.header("accept")}")
}

/**
 * Handles the errors caught in the Web-Api
 * @param error the exception thrown
 * @return a status response for the user to see
 */
fun handleError(error: Exception): Response {
    return if (error is AppException)
        onAppException(error)
    else
        makeResponse(
            Status.BAD_REQUEST,
            Json.encodeToString(Error(ErrorCode.BAD_REQUEST, "Check the format of the request body"))
        )
}

/**
 * Handles any [AppException] that was thrown
 * by the Services module
 * @param error the exception thrown
 * @return a status response for the user to see
 */
fun onAppException(error: AppException): Response {
    val message = error.message ?: "¯\\_(ツ)_/¯"
    return when (error.status) {
        AppExceptionStatus.UNAUTHORIZED -> makeResponse(
            Status.UNAUTHORIZED, Json.encodeToString(Error(ErrorCode.UNAUTHENTICATED, message))
        )
        AppExceptionStatus.BAD_REQUEST -> makeResponse(
            Status.BAD_REQUEST, Json.encodeToString(Error(ErrorCode.BAD_REQUEST, message))
        )
        AppExceptionStatus.NOT_FOUND -> makeResponse(
            Status.NOT_FOUND, Json.encodeToString(Error(ErrorCode.NOT_FOUND, message))
        )
        AppExceptionStatus.INTERNAL -> makeResponse(
            Status.INTERNAL_SERVER_ERROR, Json.encodeToString(Error(ErrorCode.INTERNAL_ERROR, message))
        )
    }
}

/**
 * Fabricate a response with the given status and response body
 * @param status the status of the [Response]
 * @param body the body of the [Response]
 */
fun makeResponse(status: Status, body: String) =
    Response(status)
        .header("content-type", "application/json")
        .body(body)

/**
 * Converts order from the query string to [Order]
 */
fun String?.toOrder() =
    if (this == "descending") Order.DESCENDING
    else Order.ASCENDING

/**
 * Converts date from string to [LocalDate] or null
 */
fun String.toLocalDateOrNull(): LocalDate? {
    return try {
        this.toLocalDate()
    } catch (error: IllegalArgumentException) {
        null
    }
}

/**
 * To ignore request body properties that don't exist
 */
val json = Json { ignoreUnknownKeys = true }
