package pt.isel.ls.sports.webapi

import org.http4k.routing.routes
import pt.isel.ls.sports.services.Services
import pt.isel.ls.sports.webapi.comp.ActivitiesWebApi
import pt.isel.ls.sports.webapi.comp.RoutesWebApi
import pt.isel.ls.sports.webapi.comp.SportsWebApi
import pt.isel.ls.sports.webapi.comp.UsersWebApi

/**
 * Represents the api module of the app
 * @property routes the available routes
 */
class WebApi(services: Services) {

    private val usersRoutes = UsersWebApi(services).routes
    private val routesRoutes = RoutesWebApi(services).routes
    private val sportsRoutes = SportsWebApi(services).routes
    private val activitiesRoutes = ActivitiesWebApi(services).routes

    val routes = routes(
        usersRoutes,
        routesRoutes,
        sportsRoutes,
        activitiesRoutes
    )
}
