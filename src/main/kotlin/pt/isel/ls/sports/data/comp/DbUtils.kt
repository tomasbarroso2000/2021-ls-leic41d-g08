package pt.isel.ls.sports.data.comp

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.sports.domain.Activity
import pt.isel.ls.sports.domain.InteriorData
import pt.isel.ls.sports.domain.Route
import pt.isel.ls.sports.domain.Session
import pt.isel.ls.sports.domain.Sport
import pt.isel.ls.sports.domain.User
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet

/**
 * Initialize the connection to the data source
 */
fun connectionInit(): Connection {
    val dataSource = PGSimpleDataSource()
    val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
    dataSource.setURL(jdbcDatabaseURL)
    return dataSource.connection
}

/**
 * Converts a date in [DateTimePeriod] to milliseconds
 */
fun DateTimePeriod.toMillis() =
    hours * 60 * 60 * 1_000 +
        minutes * 60 * 1_000 +
        seconds * 1_000 +
        nanoseconds / 1_000_000

/**
 * Converts [LocalDate] to [Date]
 */
fun LocalDate.toDate(): Date = Date.valueOf(this.toJavaLocalDate())

/**
 * Converts [Int] to [DateTimePeriod]
 */
fun Int.toDateTimePeriod(): DateTimePeriod {
    var current = this
    val hours = current / (60 * 60 * 1_000)
    current -= hours * (60 * 60 * 1_000)
    val minutes = current / (60 * 1_000)
    current -= minutes * (60 * 1_000)
    val seconds = current / 1_000
    current -= seconds * 1_000
    val nanoseconds = current.toLong() * 1_000_000
    return DateTimePeriod(
        hours = hours,
        minutes = minutes,
        seconds = seconds,
        nanoseconds = nanoseconds
    )
}

/**
 * Gets a User from the Result Set
 */
fun ResultSet.toUser() = User(
    getInt("number"),
    getString("name"),
    getString("email")
)

/**
 * Gets a Sport from the Result Set
 */
fun ResultSet.toSport() = Sport(
    getInt("number"),
    getString("name"),
    getString("description"),
    InteriorData(getInt("user_number"), getString("user_name"))
)

/**
 * Gets a Route from the Result Set
 */
fun ResultSet.toRoute() = Route(
    getInt("number"),
    getString("start_location"),
    getString("end_location"),
    getDouble("distance"),
    InteriorData(getInt("user_number"), getString("user_name"))

)

/**
 * Gets an Activity from the Result Set
 */
fun ResultSet.toActivity(): Activity {
    val routeName =
        getRouteName(
            getString("start_location"),
            getString("end_location"),
            getDouble("distance")
        )
    return Activity(
        getInt("number"),
        getDate("date").toLocalDate().toKotlinLocalDate(),
        getInt("duration").toDateTimePeriod(),
        InteriorData(getInt("user_number"), getString("user_name")),
        InteriorData(getInt("sport_number"), getString("sport_name")),
        getInt("route_number").let {
            if (!wasNull()) InteriorData(it, routeName)
            else null
        }
    )
}

/**
 * Gets a Session from the Result Set
 */
fun ResultSet.toSession() = Session(
    getInt("number"),
    getString("name"),
    getString("token")
)
