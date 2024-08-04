package pt.isel.ls.sports.data

import pt.isel.ls.sports.data.comp.MockData
import pt.isel.ls.sports.data.comp.activities.MemActivitiesData
import pt.isel.ls.sports.data.comp.routes.MemRoutesData
import pt.isel.ls.sports.data.comp.sports.MemSportsData
import pt.isel.ls.sports.data.comp.transactions.MemTransaction
import pt.isel.ls.sports.data.comp.users.MemUsersData

class DataMem : Data {
    private val data = MockData()

    override val usersData = MemUsersData(data)
    override val routesData = MemRoutesData(data)
    override val sportsData = MemSportsData(data)
    override val activitiesData = MemActivitiesData(data)

    override fun getTransaction() = MemTransaction()
}
