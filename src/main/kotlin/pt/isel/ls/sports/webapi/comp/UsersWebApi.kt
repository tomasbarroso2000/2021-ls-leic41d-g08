package pt.isel.ls.sports.webapi.comp

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sports.domain.UserCredentials
import pt.isel.ls.sports.domain.UserInput
import pt.isel.ls.sports.services.Services

class UsersWebApi(private val services: Services) {

    val routes = routes(
        "api/users" bind Method.POST to ::createUser,
        "api/users/{number}" bind Method.GET to ::getUserDetails,
        "api/users" bind Method.GET to ::getUsers,
        "api/rankings/{sport}/{route}" bind Method.GET to ::getUserRankings,
        "api/session" bind Method.GET to ::getSession
    )

    /**
     * Decodes a [UserInput] from the request and sends it to createUser in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun createUser(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val userOutput = services.usersServices.createUser(request.getBodyObject())
            makeResponse(Status.CREATED, Json.encodeToString(userOutput))
        }
    }

    /**
     * Reads the user number from the request query and sends it to getUserDetails in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getUserDetails(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val user = services.usersServices.getUserDetails(request.id)
            makeResponse(Status.OK, Json.encodeToString(user))
        }
    }

    /**
     * Reads the limit and skip values desired from the request query and sends it to getUsers in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getUsers(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val users = services.usersServices.getUsers(request.limit, request.skip)
            makeResponse(Status.OK, Json.encodeToString(users))
        }
    }

    /**
     * Reads the sport, route, limit and skip values desired from the request query
     * and sends them to getCreators in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getUserRankings(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val users = services.usersServices.getUserRankings(
                request.paramToInt("sport"),
                request.paramToInt("route"),
                request.limit, request.skip
            )
            makeResponse(Status.OK, Json.encodeToString(users))
        }
    }

    /**
     * Decodes a [UserCredentials] from the request and sends it to getUserToken in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getSession(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val token = services.usersServices.getUserToken(request.credentials)
            makeResponse(Status.OK, Json.encodeToString(token))
        }
    }
}
