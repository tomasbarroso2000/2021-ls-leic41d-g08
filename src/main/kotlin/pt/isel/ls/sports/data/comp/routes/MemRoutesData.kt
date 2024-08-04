package pt.isel.ls.sports.data.comp.routes

import pt.isel.ls.sports.data.comp.MockData
import pt.isel.ls.sports.data.comp.StoredRoute
import pt.isel.ls.sports.data.comp.getSublist
import pt.isel.ls.sports.data.comp.hasMore
import pt.isel.ls.sports.data.comp.toRoute
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Route
import pt.isel.ls.sports.domain.RouteUpdateInput

class MemRoutesData(private val data: MockData) : RoutesData {

    /**
     * Get the next number for a new route to use
     */
    private fun getNextRouteNumber() = data.routes.last().number + 1

    override fun addRoute(transaction: Transaction, userNumber: Int, startLocation: String, endLocation: String, distance: Double): DataOutput {
        val nextNumber = getNextRouteNumber()
        val newRoute = StoredRoute(
            nextNumber,
            startLocation,
            endLocation,
            distance,
            userNumber
        )
        data.routes.add(newRoute)
        return DataOutput(newRoute.number)
    }

    override fun getRouteByNumber(transaction: Transaction, routeNumber: Int) =
        data.routes.find { it.number == routeNumber }?.toRoute(data)

    override fun getRoutes(transaction: Transaction, limit: Int, skip: Int): ListOfData<Route> {
        val routes = data.routes.map { it.toRoute(data) }
        return ListOfData(getSublist(routes, limit, skip), hasMore(routes.size, limit, skip))
    }

    override fun updateRoute(transaction: Transaction, routeNumber: Int, updates: RouteUpdateInput): DataOutput {
        data.routes.replaceAll {
            if (it.number == routeNumber)
                StoredRoute(
                    number = it.number,
                    startLocation = updates.startLocation ?: it.startLocation,
                    endLocation = updates.endLocation ?: it.endLocation,
                    distance = updates.distance ?: it.distance,
                    user = it.user
                )
            else it
        }
        return DataOutput(routeNumber)
    }

    override fun searchRoutes(transaction: Transaction, searchQuery: String, limit: Int, skip: Int): ListOfData<Route> {
        val routes =
            data.routes
                .filter {
                    it.startLocation.lowercase().contains(searchQuery.lowercase()) ||
                        it.endLocation.lowercase().contains(searchQuery.lowercase()) ||
                        it.distance.toString().contains(searchQuery)
                }
                .map { it.toRoute(data) }
        return ListOfData(getSublist(routes, limit, skip), hasMore(routes.size, limit, skip))
    }
}
