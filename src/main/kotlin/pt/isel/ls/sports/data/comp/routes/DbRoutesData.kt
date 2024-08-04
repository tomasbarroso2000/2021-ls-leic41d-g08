package pt.isel.ls.sports.data.comp.routes

import pt.isel.ls.sports.data.comp.toRoute
import pt.isel.ls.sports.data.comp.transactions.DbTransaction
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Route
import pt.isel.ls.sports.domain.RouteUpdateInput
import java.sql.Statement

class DbRoutesData : RoutesData {

    override fun addRoute(transaction: Transaction, userNumber: Int, startLocation: String, endLocation: String, distance: Double): DataOutput? {
        var routeOutput: DataOutput? = null
        (transaction as DbTransaction).withConnection {
            val pstmtRoute =
                it.prepareStatement(
                    "insert into routes (start_location, end_location, distance, user_number) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
            pstmtRoute.setString(1, startLocation)
            pstmtRoute.setString(2, endLocation)
            pstmtRoute.setDouble(3, distance)
            pstmtRoute.setInt(4, userNumber)
            pstmtRoute.executeUpdate()

            val generatedKeys = pstmtRoute.generatedKeys
            if (generatedKeys.next()) {
                val routeNumber = generatedKeys.getInt("number")
                routeOutput = DataOutput(routeNumber)
            }
        }
        return routeOutput
    }

    override fun getRouteByNumber(transaction: Transaction, routeNumber: Int): Route? {
        var route: Route? = null
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select routes.number, start_location, end_location, " +
                    "distance, user_number, users.name as user_name " +
                    "from routes join users on routes.user_number = users.number where routes.number = ?"
            )
            select.setInt(1, routeNumber)
            val rs = select.executeQuery()
            if (rs.next()) {
                route = rs.toRoute()
            }
        }
        return route
    }

    override fun getRoutes(transaction: Transaction, limit: Int, skip: Int): ListOfData<Route> {
        val routes = mutableListOf<Route>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select routes.number, start_location, end_location, " +
                    "distance, user_number, users.name as user_name " +
                    "from routes join users on routes.user_number = users.number " +
                    "order by routes.number asc offset ? limit ?"
            )
            select.setInt(1, skip)
            select.setInt(2, limit + 1)
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit) {
                    routes.add(rs.toRoute())
                } else {
                    hasMore = true
                }
            }
        }
        return ListOfData(routes, hasMore)
    }

    override fun updateRoute(transaction: Transaction, routeNumber: Int, updates: RouteUpdateInput): DataOutput {
        (transaction as DbTransaction).withConnection {
            val update = it.prepareStatement(buildQueryStringForUpdateRoute(updates))
            var curr = 1
            if (updates.startLocation != null) {
                update.setString(curr, updates.startLocation)
                curr++
            }
            if (updates.endLocation != null) {
                update.setString(curr, updates.endLocation)
                curr++
            }
            if (updates.distance != null) {
                update.setDouble(curr, updates.distance)
                curr++
            }
            update.setInt(curr, routeNumber)
            update.executeUpdate()
        }
        return DataOutput(routeNumber)
    }

    /**
     * Builds the query string depending on the nullability of the startLocation, endLocation  and distance
     * @param updates the updated attributes of the route
     * @return [String] the query string
     */
    private fun buildQueryStringForUpdateRoute(updates: RouteUpdateInput): String {
        var queryString = "update routes set "
        updates.startLocation?.let { queryString += "start_location = ?, " }
        updates.endLocation?.let { queryString += "end_location = ?, " }
        updates.distance?.let { queryString += "distance = ?" }
        queryString = queryString.removeSuffix(", ")
        queryString += " where number = ?"
        return queryString
    }

    override fun searchRoutes(transaction: Transaction, searchQuery: String, limit: Int, skip: Int): ListOfData<Route> {
        val routes = mutableListOf<Route>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val select =
                it.prepareStatement(
                    "select routes.number, start_location, end_location, " +
                        "distance, user_number, users.name as user_name " +
                        "from routes join users on routes.user_number = users.number " +
                        "where lower (start_location) like lower ('%' || ? || '%') or " +
                        "lower (end_location) like lower ('%' || ? || '%') or " +
                        "distance::varchar(255) like '%' || ? || '%' " +
                        "order by routes.number asc " +
                        "offset ? limit ?"
                )
            select.setString(1, searchQuery)
            select.setString(2, searchQuery)
            select.setString(3, searchQuery)
            select.setInt(4, skip)
            select.setInt(5, limit + 1)
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit) {
                    routes.add(rs.toRoute())
                } else {
                    hasMore = true
                }
            }
        }
        return ListOfData(routes, hasMore)
    }
}
