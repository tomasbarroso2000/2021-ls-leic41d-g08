package pt.isel.ls.sports.data.comp.routes

import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Route
import pt.isel.ls.sports.domain.RouteUpdateInput

interface RoutesData {

    /**
     * Adds a route
     * @param transaction the current [Transaction]
     * @param userNumber the number of the user creating it
     * @param startLocation the route's start location
     * @param endLocation the route's end location
     * @param distance the distance of the route
     * @return [DataOutput] the route number if it was successful
     */
    fun addRoute(transaction: Transaction, userNumber: Int, startLocation: String, endLocation: String, distance: Double): DataOutput?

    /**
     * Gets a route by its number
     * @param transaction the current [Transaction]
     * @param routeNumber the route number
     * @return [Route] the route if it exists
     */
    fun getRouteByNumber(transaction: Transaction, routeNumber: Int): Route?

    /**
     * Gets a list of routes
     * @param transaction the current [Transaction]
     * @param limit the number of routes desired
     * @param skip the skip value
     * @return [ListOfData<Route>] a list with the routes
     */
    fun getRoutes(transaction: Transaction, limit: Int, skip: Int): ListOfData<Route>

    /**
     * Updates the route identified by the routeNumber
     * @param transaction the current [Transaction]
     * @param routeNumber the route number
     * @param updates the updates to be made
     * @return [DataOutput] the number of the updated route
     */
    fun updateRoute(transaction: Transaction, routeNumber: Int, updates: RouteUpdateInput): DataOutput

    /**
     * Searches for routes with a startLocation, endLocation or distance similar to the searchQuery
     * @param transaction the current [Transaction]
     * @param searchQuery the search query
     * @param limit the number of routes desired
     * @param skip the skip value
     * @return [ListOfData<Route>] a list with the routes
     */
    fun searchRoutes(transaction: Transaction, searchQuery: String, limit: Int, skip: Int): ListOfData<Route>
}
