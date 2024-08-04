package pt.isel.ls.sports.data.comp.activities

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import pt.isel.ls.sports.data.comp.toActivity
import pt.isel.ls.sports.data.comp.toDate
import pt.isel.ls.sports.data.comp.toMillis
import pt.isel.ls.sports.data.comp.transactions.DbTransaction
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.ActivitiesOutput
import pt.isel.ls.sports.domain.Activity
import pt.isel.ls.sports.domain.ActivityUpdateInput
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Order
import java.sql.PreparedStatement
import java.sql.Statement
import java.sql.Types

class DbActivitiesData : ActivitiesData {

    override fun getUserActivities(transaction: Transaction, userNumber: Int, limit: Int, skip: Int): ListOfData<Activity> {
        val activities = mutableListOf<Activity>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select activities.number, activities.duration, activities.date, activities.user_number, " +
                    "users.name as user_name, activities.sport_number, sports.name as sport_name, activities.route_number, " +
                    "routes.start_location, routes.end_location, routes.distance " +
                    "from activities join users on activities.user_number = users.number " +
                    "join sports on activities.sport_number = sports.number " +
                    "left join routes on activities.route_number = routes.number " +
                    "where activities.user_number = ? order by activities.number asc offset ? limit ?"
            )
            select.setInt(1, userNumber)
            select.setInt(2, skip)
            select.setInt(3, limit + 1)
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit) {
                    activities.add(rs.toActivity())
                } else {
                    hasMore = true
                }
            }
        }
        return ListOfData(activities, hasMore)
    }

    override fun getSportActivities(transaction: Transaction, sportNumber: Int, limit: Int, skip: Int): ListOfData<Activity> {
        val activities = mutableListOf<Activity>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select activities.number, activities.duration, activities.date, activities.user_number, " +
                    "users.name as user_name, activities.sport_number, sports.name as sport_name, activities.route_number, " +
                    "routes.start_location, routes.end_location, routes.distance " +
                    "from activities join users on activities.user_number = users.number " +
                    "join sports on activities.sport_number = sports.number " +
                    "left join routes on activities.route_number = routes.number " +
                    "where activities.sport_number = ? order by activities.number asc offset ? limit ?"
            )
            select.setInt(1, sportNumber)
            select.setInt(2, skip)
            select.setInt(3, limit + 1)
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit) {
                    activities.add(rs.toActivity())
                } else {
                    hasMore = true
                }
            }
        }
        return ListOfData(activities, hasMore)
    }

    override fun addActivity(
        transaction: Transaction,
        userNumber: Int,
        sportNumber: Int,
        date: LocalDate,
        duration: DateTimePeriod,
        routeNumber: Int?
    ): DataOutput? {
        var activityOutput: DataOutput? = null
        (transaction as DbTransaction).withConnection {
            val pstmtActivity =
                it.prepareStatement(
                    "insert into activities (date, duration, user_number, sport_number, route_number) values (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
            pstmtActivity.setDate(1, date.toDate())
            pstmtActivity.setInt(2, duration.toMillis())
            pstmtActivity.setInt(3, userNumber)
            pstmtActivity.setInt(4, sportNumber)
            if (routeNumber == null)
                pstmtActivity.setNull(5, Types.INTEGER)
            else pstmtActivity.setInt(5, routeNumber)
            pstmtActivity.executeUpdate()

            val generatedKeys = pstmtActivity.generatedKeys
            if (generatedKeys.next()) {
                val activityNumber = generatedKeys.getInt("number")
                activityOutput = DataOutput(activityNumber)
            }
        }
        return activityOutput
    }

    override fun getActivityByNumber(transaction: Transaction, activityNumber: Int): Activity? {
        var activity: Activity? = null
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select activities.number, activities.duration, activities.date, activities.user_number, " +
                    "users.name as user_name, activities.sport_number, sports.name as sport_name, activities.route_number, " +
                    "routes.start_location, routes.end_location, routes.distance " +
                    "from activities join users on activities.user_number = users.number " +
                    "join sports on activities.sport_number = sports.number " +
                    "left join routes on activities.route_number = routes.number " +
                    "where activities.number = ?"
            )
            select.setInt(1, activityNumber)
            val rs = select.executeQuery()
            if (rs.next()) {
                activity = rs.toActivity()
            }
        }
        return activity
    }

    override fun getActivities(
        transaction: Transaction,
        sportNumber: Int,
        orderBy: Order,
        date: LocalDate?,
        routeNumber: Int?,
        limit: Int,
        skip: Int
    ): ListOfData<Activity> {
        val activities = mutableListOf<Activity>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val queryString = buildQueryStringForGetActivities(orderBy, routeNumber, date)
            val select = prepareStatementForGetActivities(
                select = it.prepareStatement(queryString),
                sportNumber = sportNumber,
                date = date,
                routeNumber = routeNumber,
                skip = skip,
                limit = limit + 1
            )
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit) {
                    activities.add(rs.toActivity())
                } else {
                    hasMore = true
                }
            }
        }
        return ListOfData(activities, hasMore)
    }

    override fun deleteActivities(transaction: Transaction, activitiesNumbers: List<Int>): ActivitiesOutput {
        (transaction as DbTransaction).withConnection {
            val delete = it.prepareStatement(
                "delete from activities where number = any (?)"
            )
            val activitiesArray = it.createArrayOf("INTEGER", activitiesNumbers.toTypedArray())
            delete.setArray(1, activitiesArray)
            delete.executeUpdate()
        }
        return ActivitiesOutput(activitiesNumbers)
    }

    /**
     * Builds the query string depending on the nullability of the routeNumber and date
     * @param orderBy how the list should be sorted
     * @param routeNumber the route's number
     * @param date the route's date
     * @return [String] the query string
     */
    private fun buildQueryStringForGetActivities(orderBy: Order, routeNumber: Int?, date: LocalDate?): String {
        var queryString =
            "select activities.number, activities.duration, activities.date, activities.user_number, " +
                "users.name as user_name, activities.sport_number, sports.name as sport_name, activities.route_number, " +
                "routes.start_location, routes.end_location, routes.distance " +
                "from activities join users on activities.user_number = users.number " +
                "join sports on activities.sport_number = sports.number " +
                "left join routes on activities.route_number = routes.number " +
                "where sport_number = ? "
        routeNumber?.let { queryString += "and route_number = ? " }
        date?.let { queryString += "and date = ? " }
        queryString += if (orderBy == Order.DESCENDING)
            "order by duration desc "
        else
            "order by duration asc "
        queryString += "offset ? limit ?"
        return queryString
    }

    /**
     * Prepares the statement with the not null parameters
     * @param select the prepared statement to be  modified
     * @param sportNumber the sport's number
     * @param date the activity's date
     * @param routeNumber the routeNumber
     * @param skip the skip value
     * @param limit the number of activities desired by the user
     * @return [PreparedStatement] the preparedStatement to be executed
     */
    private fun prepareStatementForGetActivities(
        select: PreparedStatement,
        sportNumber: Int,
        date: LocalDate?,
        routeNumber: Int?,
        skip: Int,
        limit: Int
    ): PreparedStatement {
        select.setInt(1, sportNumber)
        var curr = 2
        routeNumber?.let {
            select.setInt(curr, routeNumber)
            curr++
        }
        date?.let {
            select.setDate(curr, date.toDate())
            curr++
        }
        select.setInt(curr, skip)
        select.setInt(curr + 1, limit)
        return select
    }

    override fun deleteActivityByNumber(transaction: Transaction, activityNumber: Int): DataOutput {
        (transaction as DbTransaction).withConnection {
            val delete = it.prepareStatement(
                "delete from activities where number = ?"
            )
            delete.setInt(1, activityNumber)
            delete.executeUpdate()
        }
        return DataOutput(activityNumber)
    }

    override fun updateActivity(
        transaction: Transaction,
        activityNumber: Int,
        updates: ActivityUpdateInput
    ): DataOutput {
        (transaction as DbTransaction).withConnection {
            val update = it.prepareStatement(buildQueryStringForUpdateActivity(updates))
            var curr = 1
            if (updates.date != null) {
                update.setDate(curr, updates.date.toDate())
                curr++
            }
            if (updates.duration != null) {
                update.setInt(curr, updates.duration.toMillis())
                curr++
            }
            update.setInt(curr, activityNumber)
            update.executeUpdate()
        }
        return DataOutput(activityNumber)
    }

    /**
     * Builds the query string depending on the nullability of the date and duration
     * @param updates the updated attributes of the activity
     * @return [String] the query string
     */
    private fun buildQueryStringForUpdateActivity(updates: ActivityUpdateInput): String {
        var queryString = "update activities set "
        updates.date?.let { queryString += "date = ?, " }
        updates.duration?.let { queryString += "duration = ?" }
        queryString = queryString.removeSuffix(", ")
        queryString += "where number = ?"
        return queryString
    }
}
