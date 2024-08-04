package pt.isel.ls.sports.data.comp.activities

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import pt.isel.ls.sports.data.comp.MockData
import pt.isel.ls.sports.data.comp.StoredActivity
import pt.isel.ls.sports.data.comp.getSublist
import pt.isel.ls.sports.data.comp.hasMore
import pt.isel.ls.sports.data.comp.toActivity
import pt.isel.ls.sports.data.comp.toMillis
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.ActivitiesOutput
import pt.isel.ls.sports.domain.Activity
import pt.isel.ls.sports.domain.ActivityUpdateInput
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Order

class MemActivitiesData(private val data: MockData) : ActivitiesData {

    /**
     * Get the next number for a new activity to use
     */
    private fun getNextActivityNumber() = data.activities.last().number + 1

    override fun getUserActivities(transaction: Transaction, userNumber: Int, limit: Int, skip: Int): ListOfData<Activity> {
        val activities =
            data.activities
                .filter { it.user == userNumber }
                .map { it.toActivity(data) }
        return ListOfData(getSublist(activities, limit, skip), hasMore(activities.size, limit, skip))
    }

    override fun getSportActivities(transaction: Transaction, sportNumber: Int, limit: Int, skip: Int): ListOfData<Activity> {
        val activities =
            data.activities
                .filter { it.sport == sportNumber }
                .map { it.toActivity(data) }
        return ListOfData(getSublist(activities, limit, skip), hasMore(activities.size, limit, skip))
    }

    override fun addActivity(
        transaction: Transaction,
        userNumber: Int,
        sportNumber: Int,
        date: LocalDate,
        duration: DateTimePeriod,
        routeNumber: Int?
    ): DataOutput {
        val nextNumber = getNextActivityNumber()
        val newActivity =
            StoredActivity(
                nextNumber,
                date,
                duration,
                userNumber,
                sportNumber,
                routeNumber
            )
        data.activities.add(newActivity)
        return DataOutput(newActivity.number)
    }

    override fun getActivityByNumber(transaction: Transaction, activityNumber: Int) =
        data.activities.find { it.number == activityNumber }?.toActivity(data)

    override fun deleteActivities(transaction: Transaction, activitiesNumbers: List<Int>): ActivitiesOutput {
        data.activities.removeAll { activitiesNumbers.contains(it.number) }
        return ActivitiesOutput(activitiesNumbers)
    }

    override fun deleteActivityByNumber(transaction: Transaction, activityNumber: Int): DataOutput {
        data.activities.removeIf { it.number == activityNumber }
        return DataOutput(activityNumber)
    }

    override fun getActivities(
        transaction: Transaction,
        sportNumber: Int,
        orderBy: Order,
        date: LocalDate?,
        routeNumber: Int?,
        limit: Int,
        skip: Int
    ): ListOfData<Activity> {
        val activities = data.activities
            .filter { it.sport == sportNumber }
            .let { activities ->
                if (date != null)
                    activities.filter { it.date == date }
                else activities
            }
            .let { activities ->
                if (routeNumber != null)
                    activities.filter { it.route == routeNumber }
                else activities
            }
            .let { list ->
                val sortedList =
                    list.sortedBy { activity ->
                        activity.duration.toMillis()
                    }
                if (orderBy == Order.ASCENDING)
                    sortedList
                else sortedList.reversed()
            }
            .map { it.toActivity(data) }
        return ListOfData(getSublist(activities, limit, skip), hasMore(activities.size, limit, skip))
    }

    override fun updateActivity(
        transaction: Transaction,
        activityNumber: Int,
        updates: ActivityUpdateInput
    ): DataOutput {
        data.activities.replaceAll {
            if (it.number == activityNumber)
                StoredActivity(
                    number = it.number,
                    date = updates.date ?: it.date,
                    duration = updates.duration ?: it.duration,
                    user = it.user,
                    sport = it.sport,
                    route = it.route
                )
            else it
        }
        return DataOutput(activityNumber)
    }
}
