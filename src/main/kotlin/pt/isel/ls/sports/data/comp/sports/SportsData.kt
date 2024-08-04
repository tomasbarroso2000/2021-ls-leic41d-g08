package pt.isel.ls.sports.data.comp.sports

import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Sport
import pt.isel.ls.sports.domain.SportUpdateInput

interface SportsData {

    /**
     * Adds a sport
     * @param transaction the current transaction
     * @param userNumber the number of the user creating it
     * @param name the sports name
     * @param description a short description of the sport
     * @return [DataOutput] the sport number if it was successful
     */
    fun addSport(transaction: Transaction, userNumber: Int, name: String, description: String?): DataOutput?

    /**
     * Gets a list of sports
     * @param transaction the current transaction
     * @param limit the number of sports desired
     * @param skip the skip value
     * @return [ListOfData<Sport>] a list with the sports
     */
    fun getSports(transaction: Transaction, limit: Int, skip: Int): ListOfData<Sport>

    /**
     * Gets a sport by its number
     * @param transaction the current transaction
     * @param sportNumber the sport number
     * @return [Sport] the sport if it exists
     */
    fun getSportByNumber(transaction: Transaction, sportNumber: Int): Sport?

    /**
     * Updates the sport identified by the sportNumber
     * @param transaction the current transaction
     * @param sportNumber the sport number
     * @param updates the updates to be made
     * @return [DataOutput] the number of the updated sport
     */
    fun updateSport(transaction: Transaction, sportNumber: Int, updates: SportUpdateInput): DataOutput

    /**
     * Searches for routes with a name or description similar to the searchQuery
     * @param transaction the current transaction
     * @param searchQuery the search query
     * @param limit the number of sports desired
     * @param skip the skip value
     * @return [ListOfData<Sport>] a list with the routes
     */
    fun searchSports(transaction: Transaction, searchQuery: String, limit: Int, skip: Int): ListOfData<Sport>
}
