package pt.isel.ls.sports.services.comp

import pt.isel.ls.sports.data.Data
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Sport
import pt.isel.ls.sports.domain.SportInput
import pt.isel.ls.sports.domain.SportUpdateInput

class SportsServices(private val data: Data) {

    /**
     * Checks if the sport properties are not null and sends it to addSport in the data module
     * @param token the user's token
     * @param sport the sport to create
     * @return a [DataOutput] with the return of the data function
     */
    fun createSport(token: String?, sport: SportInput): DataOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                val userNumber = getUserNumber(transaction, token, data)
                if (sport.name == null || sport.name.isEmpty())
                    throw AppException("Empty sport name", AppExceptionStatus.BAD_REQUEST)
                data.sportsData.addSport(transaction, userNumber, sport.name, sport.description)
                    ?: throw AppException("Could not create sport", AppExceptionStatus.INTERNAL)
            }
        }
    }

    /**
     * Checks if the sport number is not null and sends it to getSportByNumber in the data module
     * @param sportNumber the sport number
     * @return a [Sport] with the return of the data function
     */
    fun getSportDetails(sportNumber: Int?): Sport {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (sportNumber == null || sportNumber < 0)
                    throw AppException("Invalid sport number", AppExceptionStatus.BAD_REQUEST)
                data.sportsData.getSportByNumber(transaction, sportNumber)
                    ?: throw AppException("Sport doesn't exist", AppExceptionStatus.NOT_FOUND)
            }
        }
    }

    /**
     * Checks if the sports limit is greater than 0 and the skip value is greater or equal to 0
     * and sends it to getSports in the data module
     * @param limit the limit of sports desired
     * @param skip the skip value
     * @return a [ListOfData] with the return of the data function
     */
    fun getSports(limit: Int, skip: Int): ListOfData<Sport> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                checkLimitAndSkip(limit, skip)
                data.sportsData.getSports(transaction, limit, skip)
            }
        }
    }

    /**
     * Checks if the sport number is not null and greater than 0
     * and the sport exists
     * and sends it to updateSport in the data module
     * @param token the user's token
     * @param sportNumber the number of the sport
     * @param updates the updates to be made
     * @return a [DataOutput] with the return of the data function
     */
    fun updateSport(token: String?, sportNumber: Int?, updates: SportUpdateInput): DataOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                val userNumber = getUserNumber(transaction, token, data)
                if (sportNumber == null || sportNumber <= 0)
                    throw AppException("Invalid sport number", AppExceptionStatus.BAD_REQUEST)
                val sport = data.sportsData.getSportByNumber(transaction, sportNumber)
                    ?: throw AppException("Sport doesn't exist", AppExceptionStatus.NOT_FOUND)
                if (userNumber != sport.user.number)
                    throw AppException("Sport is not yours to update", AppExceptionStatus.UNAUTHORIZED)
                if (updates.name == null && updates.description == null)
                    throw AppException("No updates requested", AppExceptionStatus.BAD_REQUEST)
                data.sportsData.updateSport(transaction, sportNumber, updates)
            }
        }
    }

    /**
     * Checks if the search query is not null and
     * the limit is greater than 0 and the skip value is greater or equal to 0
     * and sends it to searchSports in the data module
     * @param searchQuery the search query
     * @param limit the limit of sports desired
     * @param skip the skip value
     * @return a [ListOfData] with the return of the data function
     */
    fun searchSports(searchQuery: String?, limit: Int, skip: Int): ListOfData<Sport> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (searchQuery == null)
                    throw AppException("No search query provided", AppExceptionStatus.BAD_REQUEST)
                checkLimitAndSkip(limit, skip)
                data.sportsData.searchSports(transaction, searchQuery, limit, skip)
            }
        }
    }
}
