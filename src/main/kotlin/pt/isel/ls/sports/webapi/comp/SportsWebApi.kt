package pt.isel.ls.sports.webapi.comp

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sports.domain.SportInput
import pt.isel.ls.sports.services.Services

class SportsWebApi(private val services: Services) {

    val routes = routes(
        "api/sports" bind Method.POST to ::createSport,
        "api/sports/{number}" bind Method.GET to ::getSportDetails,
        "api/sports" bind Method.GET to ::getSports,
        "api/sports/{number}" bind Method.PUT to ::updateSport,
        "api/search/sports" bind Method.GET to ::searchSports
    )

    /**
     * Decodes a [SportInput] from the request and sends it to createSport in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun createSport(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val outputSport = services.sportsServices.createSport(request.token, request.getBodyObject())
            makeResponse(Status.CREATED, Json.encodeToString(outputSport))
        }
    }

    /**
     * Reads the sport number from the request and sends it to getSportDetails in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getSportDetails(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val sport = services.sportsServices.getSportDetails(request.id)
            makeResponse(Status.OK, Json.encodeToString(sport))
        }
    }

    /**
     * Reads the limit and skip values desired from the request query and sends it to getSports in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getSports(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val sports = services.sportsServices.getSports(request.limit, request.skip)
            makeResponse(Status.OK, Json.encodeToString(sports))
        }
    }

    /**
     * Reads the sportNumber desired from the request and sends it to updateSport in services
     * along with the object containing the requested updates
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun updateSport(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val updatedSport =
                services.sportsServices.updateSport(
                    request.token, request.id, request.getBodyObject()
                )
            makeResponse(Status.OK, Json.encodeToString(updatedSport))
        }
    }

    /**
     * Reads the search query, limit and skip values desired from the request query and sends it to searchSports in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun searchSports(request: Request): Response {
        return doApiTask {
            val searchResults =
                services.sportsServices.searchSports(request.searchQuery, request.limit, request.skip)
            makeResponse(Status.OK, Json.encodeToString(searchResults))
        }
    }
}
