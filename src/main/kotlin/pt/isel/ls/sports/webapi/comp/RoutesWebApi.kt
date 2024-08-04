package pt.isel.ls.sports.webapi.comp

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sports.domain.RouteInput
import pt.isel.ls.sports.services.Services

class RoutesWebApi(private val services: Services) {

    val routes = routes(
        "api/routes" bind Method.POST to ::createRoute,
        "api/routes/{number}" bind Method.GET to ::getRouteDetails,
        "api/routes" bind Method.GET to ::getRoutes,
        "api/routes/{number}" bind Method.PUT to ::updateRoute,
        "api/search/routes" bind Method.GET to ::searchRoute
    )

    /**
     * Decodes a [RouteInput] from the request and sends it to createRoute in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun createRoute(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val routeOutput = services.routesServices.createRoute(request.token, request.getBodyObject())
            makeResponse(Status.CREATED, Json.encodeToString(routeOutput))
        }
    }

    /**
     * Reads the route number from the request query and sends it to getRouteDetails in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getRouteDetails(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val route = services.routesServices.getRouteDetails(request.id)
            makeResponse(Status.OK, Json.encodeToString(route))
        }
    }

    /**
     * Reads the limit and skip values desired from the request query and sends it to getRoutes in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getRoutes(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val routes = services.routesServices.getRoutes(request.limit, request.skip)
            makeResponse(Status.OK, Json.encodeToString(routes))
        }
    }

    /**
     * Reads the routeNumber desired from the request and sends it to updateRoute in services
     * along with the object containing the requested updates
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun updateRoute(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val updatedRoute =
                services.routesServices.updateRoute(
                    request.token, request.id, request.getBodyObject()
                )
            makeResponse(Status.OK, Json.encodeToString(updatedRoute))
        }
    }

    /**
     * Reads the search query, limit and skip values desired from the request query and sends it to searchRoutes in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun searchRoute(request: Request): Response {
        return doApiTask {
            val searchResults =
                services.routesServices.searchRoutes(request.searchQuery, request.limit, request.skip)
            makeResponse(Status.OK, Json.encodeToString(searchResults))
        }
    }
}
