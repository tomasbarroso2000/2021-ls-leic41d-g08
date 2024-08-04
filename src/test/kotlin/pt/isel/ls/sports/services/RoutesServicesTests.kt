package pt.isel.ls.sports.services

import org.junit.Test
import pt.isel.ls.sports.data.DataMem
import pt.isel.ls.sports.domain.RouteInput
import pt.isel.ls.sports.domain.RouteUpdateInput
import pt.isel.ls.sports.services.comp.AppException
import pt.isel.ls.sports.services.comp.AppExceptionStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RoutesServicesTests {

    private val data = DataMem()
    private val services = Services(data).routesServices
    private val tokenOfUser1 = "e2a6cf7c-7125-4a88-858a-2196d24e8ead"
    private val tokenOfUser2 = "ffc0b3b2-8684-4d16-bccf-331a93a982c2"

    @Test
    fun create_route() {
        val newRoute = RouteInput("Lisboa", "Lagos", 300.0)
        val routeOutput = services.createRoute(tokenOfUser1, newRoute)
        assertEquals(3, routeOutput.number)
    }

    @Test
    fun create_route_with_empty_token() {
        val appException =
            assertFailsWith<AppException> {
                val newRoute = RouteInput("Lisboa", "Lagos", 300.0)
                services.createRoute("", newRoute)
            }
        assertEquals("No token provided", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun create_route_with_invalid_token() {
        val appException =
            assertFailsWith<AppException> {
                val newRoute = RouteInput("Lisboa", "Lagos", 300.0)
                services.createRoute("1234", newRoute)
            }
        assertEquals("Invalid token", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun create_route_with_empty_start() {
        val appException =
            assertFailsWith<AppException> {
                val newRoute = RouteInput("", "Porto", 20.3)
                services.createRoute(tokenOfUser1, newRoute)
            }
        assertEquals("Empty start location", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun create_route_with_empty_end() {
        val appException =
            assertFailsWith<AppException> {
                val newRoute = RouteInput("Lisboa", "", 20.3)
                services.createRoute(tokenOfUser1, newRoute)
            }
        assertEquals("Empty end location", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun create_route_with_invalid_distance() {
        val appException =
            assertFailsWith<AppException> {
                val newRoute = RouteInput("Lisboa", "Porto", -10.0)
                services.createRoute(tokenOfUser1, newRoute)
            }
        assertEquals("Invalid distance", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_route_details() {
        val route = services.getRouteDetails(1)
        assertEquals("Lisboa", route.startLocation)
        assertEquals("Porto", route.endLocation)
        assertEquals(300.0, route.distance)
        assertEquals(1, route.user.number)
    }

    @Test
    fun get_route_details_of_non_existing_route() {
        val appException =
            assertFailsWith<AppException> {
                services.getRouteDetails(10)
            }
        assertEquals("Route doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun get_route_details_of_invalid_route_number() {
        val appException =
            assertFailsWith<AppException> {
                services.getRouteDetails(-10)
            }
        assertEquals("Invalid route number", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_routes() {
        val routes = services.getRoutes(2, 0)
        assertEquals(2, routes.list.size)
        assertEquals(1, routes.list[0].number)
        assertEquals(2, routes.list[1].number)
    }

    @Test
    fun get_routes_with_invalid_limit() {
        val appException =
            assertFailsWith<AppException> {
                services.getRoutes(-1, 0)
            }
        assertEquals("Invalid limit", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_routes_with_invalid_skip() {
        val appException =
            assertFailsWith<AppException> {
                services.getRoutes(4, -10)
            }
        assertEquals("Invalid skip", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun update_route() {
        assertEquals("Lisboa", services.getRouteDetails(1).startLocation)
        services.updateRoute(
            tokenOfUser1,
            1,
            RouteUpdateInput("Lagos", "Lisboa")
        )
        assertEquals("Lagos", services.getRouteDetails(1).startLocation)
        assertEquals("Lisboa", services.getRouteDetails(1).endLocation)
    }

    @Test
    fun update_non_exisiting_route() {
        val appException =
            assertFailsWith<AppException> {
                services.updateRoute(
                    tokenOfUser1,
                    10,
                    RouteUpdateInput("Lagos", "Lisboa")
                )
            }
        assertEquals("Route doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun update_invalid_route_number() {
        val appException =
            assertFailsWith<AppException> {
                services.updateRoute(
                    tokenOfUser1,
                    -11,
                    RouteUpdateInput("Lagos", "Lisboa")
                )
            }
        assertEquals("Invalid route number", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun update_route_that_is_not_yours() {
        val appException =
            assertFailsWith<AppException> {
                services.updateRoute(tokenOfUser2, 1, RouteUpdateInput("Failed"))
            }
        assertEquals("Route is not yours to update", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun search_routes() {
        val routes = services.searchRoutes("Guarda", 2, 0)
        assertEquals(1, routes.list.size)
        assertEquals(2, routes.list[0].number)
    }

    @Test
    fun search_routes_with_invalid_limit() {
        val appException =
            assertFailsWith<AppException> {
                services.searchRoutes("Guarda", -1, 0)
            }
        assertEquals("Invalid limit", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun search_routes_with_invalid_skip() {
        val appException =
            assertFailsWith<AppException> {
                services.searchRoutes("Guarda", 1, -10)
            }
        assertEquals("Invalid skip", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }
}
