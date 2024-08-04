package pt.isel.ls.sports.data.comp.users

import pt.isel.ls.sports.data.comp.DataException
import pt.isel.ls.sports.data.comp.MockData
import pt.isel.ls.sports.data.comp.StoredUser
import pt.isel.ls.sports.data.comp.getSublist
import pt.isel.ls.sports.data.comp.hasMore
import pt.isel.ls.sports.data.comp.toMillis
import pt.isel.ls.sports.data.comp.toUser
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Session
import pt.isel.ls.sports.domain.User
import pt.isel.ls.sports.domain.UserOutput
import java.util.UUID

class MemUsersData(
    private val data: MockData
) : UsersData {

    /**
     * Get the next number for a new user to use
     */
    private fun getNextUserNumber() = data.users.last().number + 1

    /**
     * Check if the email is already associated to a user
     */
    private fun isEmailTaken(email: String) = data.users.any { it.email == email }

    override fun getUserNumberByToken(transaction: Transaction, token: String?): Int? =
        data.users.find { it.token == token }?.number

    override fun addUser(transaction: Transaction, name: String, email: String, password: String): UserOutput {
        if (isEmailTaken(email))
            throw DataException("The email $email is already in use")
        val nextNumber = getNextUserNumber()
        val token = UUID.randomUUID().toString()
        val newUser = StoredUser(nextNumber, name, email, password.hashCode(), token)
        data.users.add(newUser)
        return UserOutput(token, newUser.number)
    }

    override fun getUsers(transaction: Transaction, limit: Int, skip: Int): ListOfData<User> {
        val users = data.users.map { it.toUser() }
        return ListOfData(getSublist(users, limit, skip), hasMore(users.size, limit, skip))
    }

    override fun getUserByNumber(transaction: Transaction, userNumber: Int) =
        data.users.find { it.number == userNumber }?.toUser()

    override fun getUserRankings(transaction: Transaction, sportNumber: Int, routeNumber: Int, limit: Int, skip: Int): ListOfData<User> {
        val creatorsSet = mutableSetOf<User>()
        data.activities
            .sortedBy { it.duration.toMillis() }
            .forEach { activity ->
                if (activity.sport == sportNumber && activity.route == routeNumber) {
                    val creator = data.users.find { it.number == activity.user }
                    creator?.let { creatorsSet.add(creator.toUser()) }
                }
            }
        return ListOfData(getSublist(creatorsSet.toList(), limit, skip), hasMore(creatorsSet.size, limit, skip))
    }

    override fun getUserToken(transaction: Transaction, email: String, password: String): Session? {
        val user = data.users
            .find { it.email == email && it.password == password.hashCode() }
            ?: return null
        return Session(user.number, user.name, user.token)
    }
}
