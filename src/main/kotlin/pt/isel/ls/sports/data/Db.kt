package pt.isel.ls.sports.data

import pt.isel.ls.sports.data.comp.activities.DbActivitiesData
import pt.isel.ls.sports.data.comp.connectionInit
import pt.isel.ls.sports.data.comp.routes.DbRoutesData
import pt.isel.ls.sports.data.comp.sports.DbSportsData
import pt.isel.ls.sports.data.comp.transactions.DbTransaction
import pt.isel.ls.sports.data.comp.users.DbUsersData

class Db : Data {
    override val usersData = DbUsersData()
    override val routesData = DbRoutesData()
    override val sportsData = DbSportsData()
    override val activitiesData = DbActivitiesData()

    override fun getTransaction() = DbTransaction(connectionInit())
}
