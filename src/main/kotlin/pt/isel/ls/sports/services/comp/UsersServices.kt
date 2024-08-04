package pt.isel.ls.sports.services.comp

import pt.isel.ls.sports.data.Data
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Session
import pt.isel.ls.sports.domain.User
import pt.isel.ls.sports.domain.UserCredentials
import pt.isel.ls.sports.domain.UserInput
import pt.isel.ls.sports.domain.UserOutput

class UsersServices(private val data: Data) {

    /**
     * Checks if the user properties are not null and sends them to addUser in the data module
     * @param user the user to create
     * @return a [UserOutput] with the return of the data function
     */
    fun createUser(user: UserInput): UserOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (user.name == null || user.name.isEmpty())
                    throw AppException("Empty name", AppExceptionStatus.BAD_REQUEST)
                if (user.email == null || user.email.isEmpty())
                    throw AppException("Empty email", AppExceptionStatus.BAD_REQUEST)
                if (!isEmailValid(user.email))
                    throw AppException("Invalid email", AppExceptionStatus.BAD_REQUEST)
                if (user.password == null || user.password.isEmpty())
                    throw AppException("Empty password", AppExceptionStatus.BAD_REQUEST)
                data.usersData.addUser(transaction, user.name, user.email, user.password)
                    ?: throw AppException("Could not create user", AppExceptionStatus.INTERNAL)
            }
        }
    }

    /**
     * Checks if the user number is not null and sends it to getUserByNumber in the data module
     * @param userNumber the user number
     * @return a [User] with the return of the data function
     */
    fun getUserDetails(userNumber: Int?): User {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (userNumber == null || userNumber < 0)
                    throw AppException("Invalid user number", AppExceptionStatus.BAD_REQUEST)
                data.usersData.getUserByNumber(transaction, userNumber)
                    ?: throw AppException("User doesn't exist", AppExceptionStatus.NOT_FOUND)
            }
        }
    }

    /**
     * Checks if the user limit is greater than 0 and the skip value is greater or equal to 0
     * and sends it to getUsers in the data module
     * @param limit the limit of users desired
     * @param skip the skip value
     * @return a [ListOfData] with the return of the data function
     */
    fun getUsers(limit: Int, skip: Int): ListOfData<User> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                checkLimitAndSkip(limit, skip)
                data.usersData.getUsers(transaction, limit, skip)
            }
        }
    }

    /**
     * Checks if the user limit is greater than 0, the skip value is greater or equal to 0,
     * the sport number is valid and the route number is valid
     * and sends it to getCreators in the data module
     * @param sportNumber the number of the sport
     * @param routeNumber the number of the route
     * @param limit the limit of users desired
     * @param skip the skip value
     * @return a [ListOfData] with the return of the data function
     */
    fun getUserRankings(sportNumber: Int?, routeNumber: Int?, limit: Int, skip: Int): ListOfData<User> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (sportNumber == null || sportNumber <= 0)
                    throw AppException("Invalid sport number", AppExceptionStatus.BAD_REQUEST)
                data.sportsData.getSportByNumber(transaction, sportNumber)
                    ?: throw AppException("Sport doesn't exist", AppExceptionStatus.NOT_FOUND)
                if (routeNumber == null || routeNumber <= 0)
                    throw AppException("Invalid route number", AppExceptionStatus.BAD_REQUEST)
                data.routesData.getRouteByNumber(transaction, routeNumber)
                    ?: throw AppException("Route doesn't exist", AppExceptionStatus.NOT_FOUND)
                checkLimitAndSkip(limit, skip)
                data.usersData.getUserRankings(transaction, sportNumber, routeNumber, limit, skip)
            }
        }
    }

    /**
     * Checks if the email and password are valid not null and sends them to getUserToken
     * in the data module
     * @param userCredentials the user's credencials
     * @return the user's token
     */
    fun getUserToken(userCredentials: UserCredentials?): Session {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (userCredentials == null)
                    throw AppException("Invalid credentials", AppExceptionStatus.BAD_REQUEST)
                if (userCredentials.email == "")
                    throw AppException("Empty email", AppExceptionStatus.BAD_REQUEST)
                if (userCredentials.password == "")
                    throw AppException("Empty password", AppExceptionStatus.BAD_REQUEST)
                data.usersData.getUserToken(transaction, userCredentials.email, userCredentials.password)
                    ?: throw AppException("Invalid credentials", AppExceptionStatus.UNAUTHORIZED)
            }
        }
    }
}
