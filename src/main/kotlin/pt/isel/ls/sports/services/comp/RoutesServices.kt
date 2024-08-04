package pt.isel.ls.sports.services.comp

import pt.isel.ls.sports.data.Data
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Route
import pt.isel.ls.sports.domain.RouteInput
import pt.isel.ls.sports.domain.RouteUpdateInput

class RoutesServices(private val data: Data) {

    /**
     * Checks if the route properties are not null and sends it to addRoute in the data module
     * @param token the user's token
     * @param route the route to create
     * @return a [DataOutput] with the return of the data function
     */
    fun createRoute(token: String?, route: RouteInput): DataOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                val userNumber = getUserNumber(transaction, token, data)
                if (route.startLocation == null || route.startLocation.isEmpty())
                    throw AppException("Empty start location", AppExceptionStatus.BAD_REQUEST)
                if (route.endLocation == null || route.endLocation.isEmpty())
                    throw AppException("Empty end location", AppExceptionStatus.BAD_REQUEST)
                if (route.distance == null || route.distance <= 0)
                    throw AppException("Invalid distance", AppExceptionStatus.BAD_REQUEST)
                data.routesData.addRoute(transaction, userNumber, route.startLocation, route.endLocation, route.distance)
                    ?: throw AppException("Could not create route", AppExceptionStatus.INTERNAL)
            }
        }
    }

    /**
     * Checks if the route number is not null and sends it to getRouteByNumber in the data module
     * @param routeNumber the route number
     * @return a [Route] with the return of the data function
     */
    fun getRouteDetails(routeNumber: Int?): Route {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (routeNumber == null || routeNumber < 0)
                    throw AppException("Invalid route number", AppExceptionStatus.BAD_REQUEST)
                data.routesData.getRouteByNumber(transaction, routeNumber)
                    ?: throw AppException("Route doesn't exist", AppExceptionStatus.NOT_FOUND)
            }
        }
    }

    /**
     * Checks if the routes limit is greater than 0 and the skip value is greater or equal to 0
     * and sends it to getRoutes in the data module
     * @param limit the limit of routes desired
     * @param skip the skip value
     * @return a [ListOfData] with the return of the data function
     */
    fun getRoutes(limit: Int, skip: Int): ListOfData<Route> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                checkLimitAndSkip(limit, skip)
                data.routesData.getRoutes(transaction, limit, skip)
            }
        }
    }

    /**
     * Checks if the route number is not null and greater than 0
     * and the route exists
     * and sends it to updateRoute in the data module
     * @param token the user's token
     * @param routeNumber the number of the route
     * @param updates the updates to be made
     * @return a [DataOutput] with the return of the data function
     */
    fun updateRoute(token: String?, routeNumber: Int?, updates: RouteUpdateInput): DataOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                val userNumber = getUserNumber(transaction, token, data)
                if (routeNumber == null || routeNumber <= 0)
                    throw AppException("Invalid route number", AppExceptionStatus.BAD_REQUEST)
                val route = data.routesData.getRouteByNumber(transaction, routeNumber)
                    ?: throw AppException("Route doesn't exist", AppExceptionStatus.NOT_FOUND)
                if (userNumber != route.user.number)
                    throw AppException("Route is not yours to update", AppExceptionStatus.UNAUTHORIZED)
                if (updates.startLocation == null && updates.endLocation == null && updates.distance == null)
                    throw AppException("No updates requested", AppExceptionStatus.BAD_REQUEST)
                data.routesData.updateRoute(transaction, routeNumber, updates)
            }
        }
    }

    /**
     * Checks if the search query is not null and
     * the limit is greater than 0 and the skip value is greater or equal to 0
     * and sends it to searchRoutes in the data module
     * @param searchQuery the search query
     * @param limit the limit of routes desired
     * @param skip the skip value
     * @return a [ListOfData] with the return of the data function
     */
    fun searchRoutes(searchQuery: String?, limit: Int, skip: Int): ListOfData<Route> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (searchQuery == null)
                    throw AppException("No search query provided", AppExceptionStatus.BAD_REQUEST)
                checkLimitAndSkip(limit, skip)
                data.routesData.searchRoutes(transaction, searchQuery, limit, skip)
            }
        }
    }
}
