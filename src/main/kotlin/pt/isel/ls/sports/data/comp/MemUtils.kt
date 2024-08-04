package pt.isel.ls.sports.data.comp

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import pt.isel.ls.sports.domain.Activity
import pt.isel.ls.sports.domain.InteriorData
import pt.isel.ls.sports.domain.Route
import pt.isel.ls.sports.domain.Sport
import pt.isel.ls.sports.domain.User

/**
 * To be thrown by the DataMem module when an error is detected
 * @property message the error message
 */
data class DataException(
    override val message: String? = null
) : Exception()

/**
 * Get a sublist with given limit and skip values
 * @param list the initial list
 * @param limit the size of the sublist
 * @param skip the first index of the initial list to be considered
 * @return the sublist
 */
fun <T> getSublist(list: List<T>, limit: Int, skip: Int): List<T> {
    val newList = mutableListOf<T>()
    for (i in skip until list.size) {
        if (i < skip + limit) {
            newList.add(list[i])
        } else {
            break
        }
    }
    return newList
}

data class StoredUser(
    val number: Int,
    val name: String,
    val email: String,
    val password: Int,
    val token: String
)

data class StoredRoute(
    val number: Int,
    val startLocation: String,
    val endLocation: String,
    val distance: Double,
    val user: Int
)

data class StoredSport(
    val number: Int,
    val name: String,
    val description: String?,
    val user: Int
)

data class StoredActivity(
    val number: Int,
    val date: LocalDate,
    val duration: DateTimePeriod,
    val user: Int,
    val sport: Int,
    val route: Int? = null
)

/**
 * The mock data used for tests
 * @property users the list of [User]
 * @property routes the list of [Route]
 * @property sports the list of [Sport]
 * @property activities the list of [Activity]
 */
data class MockData(
    val users: MutableList<StoredUser> = mutableListOf(
        StoredUser(1, "Shrek", "shrek@gmail.com", "ilovefiona".hashCode(), "e2a6cf7c-7125-4a88-858a-2196d24e8ead"),
        StoredUser(2, "Bob", "bob@gmail.com", "burgers".hashCode(), "ffc0b3b2-8684-4d16-bccf-331a93a982c2"),
        StoredUser(3, "Duck", "duck@gmail.com", "#imaduck".hashCode(), "59caeb1f-426e-4882-b050-5ef388df3069")
    ),
    val routes: MutableList<StoredRoute> = mutableListOf(
        StoredRoute(1, "Lisboa", "Porto", 300.0, 1),
        StoredRoute(2, "Guarda", "Lisboa", 300.0, 2)
    ),
    val sports: MutableList<StoredSport> = mutableListOf(
        StoredSport(1, "Voleibol", "Melhor desporto para engate", 1),
        StoredSport(2, "Badminton", "Desporto individual ou de pares", 2)
    ),
    val activities: MutableList<StoredActivity> = mutableListOf(
        StoredActivity(1, LocalDate(2022, 3, 26), DateTimePeriod(hours = 12, minutes = 50, seconds = 20), 1, 1),
        StoredActivity(2, LocalDate(2022, 6, 1), DateTimePeriod(hours = 12, minutes = 30, seconds = 0), 2, 2),
        StoredActivity(3, LocalDate(2022, 5, 1), DateTimePeriod(hours = 12, minutes = 20, seconds = 0), 2, 2, 1),
        StoredActivity(4, LocalDate(2022, 5, 1), DateTimePeriod(hours = 12, minutes = 20, seconds = 0), 3, 2, 1)
    )
)

/**
 * Converts a [StoredUser] to a [User]
 */
fun StoredUser.toUser(): User = User(number, name, email)

/**
 * Converts a [StoredRoute] to a [Route]
 */
fun StoredRoute.toRoute(data: MockData): Route {
    val user = data.users.find { it.number == user }
    return Route(
        number, startLocation, endLocation, distance,
        InteriorData(user?.number, user?.name)
    )
}

/**
 * Converts a [StoredSport] to a [Sport]
 */
fun StoredSport.toSport(data: MockData): Sport {
    val user = data.users.find { it.number == user }
    return Sport(
        number, name, description,
        InteriorData(user?.number, user?.name)
    )
}

/**
 * Converts a [StoredActivity] to a [Activity]
 */
fun StoredActivity.toActivity(data: MockData): Activity {
    val user = data.users.find { it.number == user }
    val sport = data.sports.find { it.number == sport }
    val route = data.routes.find { it.number == route }
    return Activity(
        number, date, duration,
        InteriorData(user?.number, user?.name),
        InteriorData(sport?.number, sport?.name),
        InteriorData(route?.number, getRouteName(route?.startLocation, route?.endLocation, route?.distance))
    )
}

/**
 * Checks if there are more tuples
 * @param count the total number of tuples found
 * @param limit the limit of tuples defined
 * @param skip the skip value defined
 * @return true if there are more tuples
 */
fun hasMore(count: Int, limit: Int, skip: Int) = count > skip + limit
