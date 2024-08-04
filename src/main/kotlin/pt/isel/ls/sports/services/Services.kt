package pt.isel.ls.sports.services

import pt.isel.ls.sports.data.Data
import pt.isel.ls.sports.services.comp.ActivitiesServices
import pt.isel.ls.sports.services.comp.RoutesServices
import pt.isel.ls.sports.services.comp.SportsServices
import pt.isel.ls.sports.services.comp.UsersServices

/**
 * Represents the services module of the app
 * @property usersServices the users section
 * @property routesServices the routes section
 * @property sportsServices the sports section
 * @property activitiesServices the activities section
 */
class Services(data: Data) {
    val usersServices = UsersServices(data)
    val routesServices = RoutesServices(data)
    val sportsServices = SportsServices(data)
    val activitiesServices = ActivitiesServices(data)
}
