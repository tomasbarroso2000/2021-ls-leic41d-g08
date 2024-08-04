package pt.isel.ls.sports.data.comp.activities

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.ActivitiesOutput
import pt.isel.ls.sports.domain.Activity
import pt.isel.ls.sports.domain.ActivityUpdateInput
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Order

interface ActivitiesData {

    /**
     * Gets a list of activities created by the user
     * @param transaction the current [Transaction]
     * @param userNumber the user's number
     * @param limit the number of activities desired
     * @param skip the skip value
     * @return [ListOfData<Activity>] a list with the activities
     */
    fun getUserActivities(transaction: Transaction, userNumber: Int, limit: Int, skip: Int): ListOfData<Activity>

    /**
     * Gets a list of activities linked to the sport
     * @param transaction the current [Transaction]
     * @param sportNumber the sport's number
     * @param limit the number of activities desired
     * @param skip the skip value
     * @return [ListOfData<Activity>] a list with the activities
     */
    fun getSportActivities(transaction: Transaction, sportNumber: Int, limit: Int, skip: Int): ListOfData<Activity>

    /**
     * Adds an activity
     * @param transaction the current [Transaction]
     * @param userNumber the number of the user creating it
     * @param sportNumber the number of the sport linked to it
     * @param date the date of the activity
     * @param duration the duration of the activity
     * @return [DataOutput] the activity number if it was successful
     */
    fun addActivity(
        transaction: Transaction,
        userNumber: Int,
        sportNumber: Int,
        date: LocalDate,
        duration: DateTimePeriod,
        routeNumber: Int?
    ): DataOutput?

    /**
     * Gets an activity by its number
     * @param transaction the current [Transaction]
     * @param activityNumber the activity number
     * @return [Activity] the activity if it exists
     */
    fun getActivityByNumber(transaction: Transaction, activityNumber: Int): Activity?

    /**
     * Deletes an activity
     * @param transaction the current [Transaction]
     * @param activityNumber the activity's number
     * @return [DataOutput] the activity number if it was successfully deleted
     */
    fun deleteActivityByNumber(transaction: Transaction, activityNumber: Int): DataOutput

    /**
     * Gets a list of activities
     * @param transaction the current [Transaction]
     * @param sportNumber the number of the sport it is linked to
     * @param orderBy the order the user wants the activities to be displayed
     * @param date the date of the activities
     * @param routeNumber the number of the route linked to it
     * @param limit the number of activities desired
     * @param skip the skip value
     * @return [ListOfData<Activity>] a list with the activities
     */
    fun getActivities(transaction: Transaction, sportNumber: Int, orderBy: Order, date: LocalDate?, routeNumber: Int?, limit: Int, skip: Int): ListOfData<Activity>

    /**
     * Deletes a list of activities
     * @param transaction the current [Transaction]
     * @param activitiesNumbers the list of activities numbers
     * @return [ActivitiesOutput] the deleted activities' numbers if they were successfully deleted
     */
    fun deleteActivities(transaction: Transaction, activitiesNumbers: List<Int>): ActivitiesOutput

    /**
     * Updates the activity identified by the activityNumber
     * @param transaction the current [Transaction]
     * @param activityNumber the activity number
     * @param updates the updates to be made
     * @return [DataOutput] the number of the activity that was updated
     */
    fun updateActivity(transaction: Transaction, activityNumber: Int, updates: ActivityUpdateInput): DataOutput
}
