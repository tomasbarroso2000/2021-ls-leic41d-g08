package pt.isel.ls.sports.data.comp.users

import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Session
import pt.isel.ls.sports.domain.User
import pt.isel.ls.sports.domain.UserOutput

interface UsersData {

    /**
     * Gets the user number using the token linked to it
     * @param transaction the current [Transaction]
     * @param token the user's token
     * @return [Int] the user number, if it exists
     */
    fun getUserNumberByToken(transaction: Transaction, token: String?): Int?

    /**
     * Adds a user
     * @param transaction the current [Transaction]
     * @param name the user's name
     * @param email the user's email
     * @param password the user's password
     * @return [UserOutput] the user token and number if it was successful
     */
    fun addUser(transaction: Transaction, name: String, email: String, password: String): UserOutput?

    /**
     * Gets a list of users
     * @param transaction the current [Transaction]
     * @param limit the number of users desired
     * @param skip the skip value
     * @return [ListOfData<User>] a list with the users
     */
    fun getUsers(transaction: Transaction, limit: Int, skip: Int): ListOfData<User>

    /**
     * Gets a user by its number
     * @param transaction the current [Transaction]
     * @param userNumber the user number
     * @return [User] the user if it exists
     */
    fun getUserByNumber(transaction: Transaction, userNumber: Int): User?

    /**
     * Gets a list of user rankings
     * @param transaction the current [Transaction]
     * @param sportNumber the sport number
     * @param routeNumber the route number
     * @param limit the number of users desired
     * @param skip the skip value
     * @return [ListOfData<User>] a list with the users
     */
    fun getUserRankings(transaction: Transaction, sportNumber: Int, routeNumber: Int, limit: Int, skip: Int): ListOfData<User>

    /**
     * Gets the user token
     * @param transaction the current [Transaction]
     * @param email the user's email
     * @param password the user's password
     */
    fun getUserToken(transaction: Transaction, email: String, password: String): Session?
}
