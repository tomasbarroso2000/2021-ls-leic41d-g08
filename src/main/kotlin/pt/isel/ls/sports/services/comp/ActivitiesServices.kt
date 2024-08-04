package pt.isel.ls.sports.services.comp

import kotlinx.datetime.LocalDate
import pt.isel.ls.sports.data.Data
import pt.isel.ls.sports.domain.ActivitiesOutput
import pt.isel.ls.sports.domain.Activity
import pt.isel.ls.sports.domain.ActivityDeleteInput
import pt.isel.ls.sports.domain.ActivityInput
import pt.isel.ls.sports.domain.ActivityUpdateInput
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Order

class ActivitiesServices(private val data: Data) {

    /**
     * Checks if the user number is not null, the limit is greater than 0
     * and the skip value is greater or equal to 0
     * and sends it to getUserActivities in the data module
     * @param userNumber the user's number
     * @param limit the limit of activities desired
     * @param skip the skip value
     * @return [ListOfData] with the return of the data function
     */
    fun getUserActivities(userNumber: Int?, limit: Int, skip: Int): ListOfData<Activity> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (userNumber == null || userNumber <= 0)
                    throw AppException("Invalid user number", AppExceptionStatus.BAD_REQUEST)
                data.usersData.getUserByNumber(transaction, userNumber)
                    ?: throw AppException("User doesn't exist", AppExceptionStatus.NOT_FOUND)
                checkLimitAndSkip(limit, skip)
                data.activitiesData.getUserActivities(transaction, userNumber, limit, skip)
            }
        }
    }

    /**
     * Checks if the sport number is not null, the limit is greater than 0
     * and the skip value is greater or equal to 0
     * and sends it to getSportActivities in the data module
     * @param sportNumber the sport's number
     * @param limit the limit of activities desired
     * @param skip the skip value
     * @return [ListOfData] with the return of the data function
     */
    fun getSportActivities(sportNumber: Int?, limit: Int, skip: Int): ListOfData<Activity> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (sportNumber == null || sportNumber <= 0)
                    throw AppException("Invalid sport number", AppExceptionStatus.BAD_REQUEST)
                data.sportsData.getSportByNumber(transaction, sportNumber)
                    ?: throw AppException("Sport doesn't exist", AppExceptionStatus.NOT_FOUND)
                checkLimitAndSkip(limit, skip)
                data.activitiesData.getSportActivities(transaction, sportNumber, limit, skip)
            }
        }
    }

    /**
     * Checks if the Activity properties are not null and sends it to addActivity in the data module
     * @param token the user's token
     * @param sportNumber the sport the activity is linked to
     * @param activity the activity to create
     * @return a [DataOutput] with the return of the data function
     */
    fun createActivity(token: String?, sportNumber: Int?, activity: ActivityInput): DataOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                val userNumber = getUserNumber(transaction, token, data)
                if (sportNumber == null || sportNumber <= 0)
                    throw AppException("Invalid sport number", AppExceptionStatus.BAD_REQUEST)
                data.sportsData.getSportByNumber(transaction, sportNumber)
                    ?: throw AppException("Sport doesn't exist", AppExceptionStatus.NOT_FOUND)
                if (activity.date == null)
                    throw AppException("Empty date", AppExceptionStatus.BAD_REQUEST)
                if (activity.duration == null)
                    throw AppException("Empty duration", AppExceptionStatus.BAD_REQUEST)
                if (activity.routeNumber != null) {
                    if (activity.routeNumber <= 0)
                        throw AppException("Invalid route number", AppExceptionStatus.BAD_REQUEST)
                    data.routesData.getRouteByNumber(transaction, activity.routeNumber)
                        ?: throw AppException("Route doesn't exist", AppExceptionStatus.NOT_FOUND)
                }
                data.activitiesData.addActivity(
                    transaction,
                    userNumber,
                    sportNumber,
                    activity.date,
                    activity.duration,
                    activity.routeNumber
                ) ?: throw AppException("Could not create sport", AppExceptionStatus.INTERNAL)
            }
        }
    }

    /**
     * Checks if the activity number is not null and sends it to getActivityByNumber in the data module
     * @param activityNumber the activity number
     * @return a [Activity] with the return of the data function
     */
    fun getActivityDetails(activityNumber: Int?): Activity {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (activityNumber == null || activityNumber < 0)
                    throw AppException("Invalid activity number", AppExceptionStatus.BAD_REQUEST)
                data.activitiesData.getActivityByNumber(transaction, activityNumber)
                    ?: throw AppException("Activity doesn't exist", AppExceptionStatus.NOT_FOUND)
            }
        }
    }

    /**
     * Checks if the activity number is not null and if the activity exists
     * and sends it to getActivityByNumber in the data module
     * @param token the user's token
     * @param activityNumber the activity number
     * @return a [DataOutput] with the return of the data function
     */
    fun deleteActivity(token: String?, activityNumber: Int?): DataOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                val userNumber = getUserNumber(transaction, token, data)
                if (activityNumber == null || activityNumber < 0)
                    throw AppException("Invalid activity number", AppExceptionStatus.BAD_REQUEST)
                val activity = data.activitiesData.getActivityByNumber(transaction, activityNumber)
                    ?: throw AppException("Activity doesn't exist", AppExceptionStatus.NOT_FOUND)
                if (userNumber != activity.user.number)
                    throw AppException("Activity is not yours to delete", AppExceptionStatus.UNAUTHORIZED)

                data.activitiesData.deleteActivityByNumber(transaction, activityNumber)
            }
        }
    }

    /**
     * Checks if the activities numbers are valid
     * and sends it to getActivityByNumber in the data module
     * @param token the user's token
     * @param activitiesNumbers the numbers of the activities to be deleted
     * @return a [ActivitiesOutput] with the return of the data function
     */
    fun deleteActivities(token: String?, activitiesNumbers: ActivityDeleteInput): ActivitiesOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                val userNumber = getUserNumber(transaction, token, data)
                if (activitiesNumbers.activities == null)
                    throw AppException("Invalid list of activities", AppExceptionStatus.BAD_REQUEST)
                activitiesNumbers.activities.forEach { activityNumber ->
                    if (activityNumber < 0)
                        throw AppException("Invalid activity number: $activityNumber", AppExceptionStatus.BAD_REQUEST)
                    val activity = data.activitiesData.getActivityByNumber(transaction, activityNumber)
                        ?: throw AppException("Activity doesn't exist: $activityNumber", AppExceptionStatus.NOT_FOUND)
                    if (userNumber != activity.user.number)
                        throw AppException("Activity is not yours to delete: $activityNumber", AppExceptionStatus.UNAUTHORIZED)
                }
                data.activitiesData.deleteActivities(transaction, activitiesNumbers.activities)
            }
        }
    }

    /**
     * Checks if the sport number, route number, limit are not null and greater than 0
     * and the skip value is greater or equal to 0
     * and sends it to getActivities in the data module
     * @param orderBy the order the user wants the activities to be displayed
     * @param date the activity date
     * @param routeNumber the route's number
     * @param limit the limit of routes desired
     * @param skip the skip value
     * @return a [ListOfData] with the return of the data function
     */
    fun getActivities(sportNumber: Int?, orderBy: Order, date: LocalDate?, routeNumber: Int?, limit: Int, skip: Int): ListOfData<Activity> {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                if (sportNumber == null || sportNumber <= 0)
                    throw AppException("Invalid sport number", AppExceptionStatus.BAD_REQUEST)
                data.sportsData.getSportByNumber(transaction, sportNumber)
                    ?: throw AppException("Sport doesn't exist", AppExceptionStatus.NOT_FOUND)
                if (routeNumber != null) {
                    if (routeNumber <= 0)
                        throw AppException("Invalid route number", AppExceptionStatus.BAD_REQUEST)
                    data.routesData.getRouteByNumber(transaction, routeNumber)
                        ?: throw AppException("Route doesn't exist", AppExceptionStatus.NOT_FOUND)
                }
                checkLimitAndSkip(limit, skip)
                data.activitiesData.getActivities(transaction, sportNumber, orderBy, date, routeNumber, limit, skip)
            }
        }
    }

    /**
     * Checks if the activity number is not null and greater than 0
     * and the activity exists
     * and sends it to updateActivity in the data module
     * @param token the user's token
     * @param activityNumber the number of the activity
     * @param updates the updates to be made
     * @return a [DataOutput] with the return of the data function
     */
    fun updateActivity(token: String?, activityNumber: Int?, updates: ActivityUpdateInput): DataOutput {
        val transaction = data.getTransaction()
        return doService {
            executeTransaction(transaction) {
                val userNumber = getUserNumber(transaction, token, data)
                if (activityNumber == null || activityNumber <= 0)
                    throw AppException("Invalid activity number", AppExceptionStatus.BAD_REQUEST)
                val activity = data.activitiesData.getActivityByNumber(transaction, activityNumber)
                    ?: throw AppException("Activity doesn't exist", AppExceptionStatus.NOT_FOUND)
                if (userNumber != activity.user.number)
                    throw AppException("Activity is not yours to update", AppExceptionStatus.UNAUTHORIZED)
                if (updates.date == null && updates.duration == null)
                    throw AppException("No updates requested", AppExceptionStatus.BAD_REQUEST)
                data.activitiesData.updateActivity(transaction, activityNumber, updates)
            }
        }
    }
}
