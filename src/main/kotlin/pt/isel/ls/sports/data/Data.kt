package pt.isel.ls.sports.data

import pt.isel.ls.sports.data.comp.activities.ActivitiesData
import pt.isel.ls.sports.data.comp.routes.RoutesData
import pt.isel.ls.sports.data.comp.sports.SportsData
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.data.comp.users.UsersData

/**
 * Represents the data module of the app
 * @property usersData the users section
 * @property routesData the routes section
 * @property sportsData the sports section
 * @property activitiesData the activities section
 */
interface Data {

    val usersData: UsersData
    val routesData: RoutesData
    val sportsData: SportsData
    val activitiesData: ActivitiesData

    /**
     * Create a new [Transaction]
     */
    fun getTransaction(): Transaction
}
