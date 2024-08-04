package pt.isel.ls.sports.services

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import org.junit.Test
import pt.isel.ls.sports.data.DataMem
import pt.isel.ls.sports.domain.ActivityDeleteInput
import pt.isel.ls.sports.domain.ActivityInput
import pt.isel.ls.sports.domain.ActivityUpdateInput
import pt.isel.ls.sports.domain.Order
import pt.isel.ls.sports.services.comp.AppException
import pt.isel.ls.sports.services.comp.AppExceptionStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ActivitiesServicesTests {

    private val data = DataMem()
    private val services = Services(data).activitiesServices
    private val tokenOfUser1 = "e2a6cf7c-7125-4a88-858a-2196d24e8ead"
    private val tokenOfUser2 = "ffc0b3b2-8684-4d16-bccf-331a93a982c2"

    @Test
    fun get_activities_of_sport() {
        val activities = services.getSportActivities(2, 2, 0)
        assertEquals(2, activities.list.size)
        assertEquals(2, activities.list[0].sport.number)
        assertEquals(2, activities.list[1].sport.number)
        assertEquals(2, activities.list[0].number)
        assertEquals(3, activities.list[1].number)
    }

    @Test
    fun get_activities_of_sport_with_invalid_limit() {
        val appException =
            assertFailsWith<AppException> {
                services.getSportActivities(1, -2, 0)
            }
        assertEquals("Invalid limit", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_activities_of_sport_with_invalid_skip() {
        val appException =
            assertFailsWith<AppException> {
                services.getSportActivities(1, 2, -10)
            }
        assertEquals("Invalid skip", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_activities_of_non_existing_sport() {
        val appException =
            assertFailsWith<AppException> {
                services.getSportActivities(10, 2, 0)
            }
        assertEquals("Sport doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun get_activities_of_user() {
        val activities = services.getUserActivities(2, 2, 0)
        assertEquals(2, activities.list.size)
        assertEquals(2, activities.list[0].sport.number)
        assertEquals(2, activities.list[1].sport.number)
        assertEquals(2, activities.list[0].number)
        assertEquals(3, activities.list[1].number)
    }

    @Test
    fun get_activities_user_with_invalid_limit() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserActivities(1, -2, 0)
            }
        assertEquals("Invalid limit", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_activities_user_with_invalid_skip() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserActivities(1, 2, -10)
            }
        assertEquals("Invalid skip", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_activities_of_non_existing_user() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserActivities(10, 2, 0)
            }
        assertEquals("User doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun create_activity() {
        val newActivity = ActivityInput(
            LocalDate(2022, 9, 9),
            DateTimePeriod(hours = 11, minutes = 19, seconds = 20),
            1
        )
        val activityOutput = services.createActivity(tokenOfUser1, 1, newActivity)
        assertEquals(5, activityOutput.number)
    }

    @Test
    fun create_activity_with_empty_token() {
        val appException =
            assertFailsWith<AppException> {
                val newActivity = ActivityInput(
                    LocalDate(2022, 9, 9),
                    DateTimePeriod(hours = 11, minutes = 19, seconds = 20),
                    1
                )
                services.createActivity("", 1, newActivity)
            }
        assertEquals("No token provided", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun create_activity_with_invalid_token() {
        val appException =
            assertFailsWith<AppException> {
                val newActivity = ActivityInput(
                    LocalDate(2022, 9, 9),
                    DateTimePeriod(hours = 11, minutes = 19, seconds = 20),
                    1
                )
                services.createActivity("1234", 1, newActivity)
            }
        assertEquals("Invalid token", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun create_activity_with_empty_date() {
        val appException =
            assertFailsWith<AppException> {
                val newActivity = ActivityInput(
                    null,
                    DateTimePeriod(hours = 11, minutes = 19, seconds = 20),
                    1
                )
                services.createActivity(tokenOfUser1, 1, newActivity)
            }
        assertEquals("Empty date", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun create_activity_with_empty_duration() {
        val appException =
            assertFailsWith<AppException> {
                val newActivity = ActivityInput(LocalDate(2022, 9, 9), null)
                services.createActivity(tokenOfUser1, 1, newActivity)
            }
        assertEquals("Empty duration", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun create_activity_with_non_existing_route() {
        val appException =
            assertFailsWith<AppException> {
                val newActivity = ActivityInput(
                    LocalDate(2022, 9, 9),
                    DateTimePeriod(hours = 11, minutes = 19, seconds = 20),
                    10
                )
                services.createActivity(tokenOfUser1, 1, newActivity)
            }
        assertEquals("Route doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun get_activity_details() {
        val activity = services.getActivityDetails(1)
        assertEquals(LocalDate(2022, 3, 26), activity.date)
        assertEquals(DateTimePeriod(hours = 12, minutes = 50, seconds = 20), activity.duration)
        assertEquals(1, activity.user.number)
        assertEquals(1, activity.sport.number)
    }

    @Test
    fun get_activity_details_of_non_existing_activity() {
        val appException =
            assertFailsWith<AppException> {
                services.getActivityDetails(10)
            }
        assertEquals("Activity doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun get_activity_details_of_invalid_activity_number() {
        val appException =
            assertFailsWith<AppException> {
                services.getActivityDetails(-10)
            }
        assertEquals("Invalid activity number", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun delete_activity() {
        val deletedActivity = services.deleteActivity(tokenOfUser1, 1)
        assertEquals(1, deletedActivity.number)
        val appException =
            assertFailsWith<AppException> {
                services.getActivityDetails(1)
            }
        assertEquals("Activity doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun delete_non_existing_activity() {
        val appException =
            assertFailsWith<AppException> {
                services.deleteActivity(tokenOfUser1, 10)
            }
        assertEquals("Activity doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun delete_invalid_activity_number() {
        val appException =
            assertFailsWith<AppException> {
                services.deleteActivity(tokenOfUser1, -10)
            }
        assertEquals("Invalid activity number", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun delete_activity_that_is_not_yours() {
        val appException =
            assertFailsWith<AppException> {
                services.deleteActivity(tokenOfUser2, 1)
            }
        assertEquals("Activity is not yours to delete", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun delete_activities() {
        services.deleteActivities(tokenOfUser2, ActivityDeleteInput(listOf(2, 3)))
        var appException =
            assertFailsWith<AppException> {
                services.getActivityDetails(2)
            }
        assertEquals("Activity doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
        appException =
            assertFailsWith {
                services.getActivityDetails(3)
            }
        assertEquals("Activity doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun delete_non_existing_activities() {
        val appException =
            assertFailsWith<AppException> {
                services.deleteActivities(tokenOfUser1, ActivityDeleteInput(listOf(20, 30)))
            }
        assertEquals("Activity doesn't exist: 20", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun delete_activities_with_invalid_activity_number() {
        val appException =
            assertFailsWith<AppException> {
                services.deleteActivities(tokenOfUser1, ActivityDeleteInput(listOf(1, -30)))
            }
        assertEquals("Invalid activity number: -30", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun delete_activities_that_are_not_yours() {
        val appException =
            assertFailsWith<AppException> {
                services.deleteActivities(tokenOfUser2, ActivityDeleteInput(listOf(2, 3, 1)))
            }
        assertEquals("Activity is not yours to delete: 1", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun get_activities() {
        val activities = services.getActivities(2, Order.ASCENDING, null, null, 2, 0)
        assertEquals(2, activities.list.size)
        assertEquals(3, activities.list[0].number)
        assertEquals(4, activities.list[1].number)
        assertEquals(2, activities.list[0].sport.number)
        assertEquals(2, activities.list[1].sport.number)
    }

    @Test
    fun get_activities_with_date() {
        val activities = services.getActivities(
            2,
            Order.ASCENDING,
            LocalDate(2022, 6, 1),
            null,
            2,
            0
        )
        assertEquals(1, activities.list.size)
        assertEquals(2, activities.list[0].number)
        assertEquals(2, activities.list[0].sport.number)
    }

    @Test
    fun get_activities_with_route() {
        val activities = services.getActivities(
            2,
            Order.ASCENDING,
            null,
            1,
            2,
            0
        )
        assertEquals(2, activities.list.size)
        assertEquals(3, activities.list[0].number)
        assertEquals(2, activities.list[0].sport.number)
    }

    @Test
    fun get_activities_with_non_existing_sport() {
        val appException =
            assertFailsWith<AppException> {
                services.getActivities(
                    10,
                    Order.ASCENDING,
                    null,
                    null,
                    10,
                    0
                )
            }
        assertEquals("Sport doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun get_activities_with_non_existing_route() {
        val appException =
            assertFailsWith<AppException> {
                services.getActivities(
                    1,
                    Order.ASCENDING,
                    null,
                    20,
                    10,
                    0
                )
            }
        assertEquals("Route doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun update_activity() {
        assertEquals(LocalDate(2022, 3, 26), services.getActivityDetails(1).date)
        services.updateActivity(
            tokenOfUser1,
            1,
            ActivityUpdateInput(LocalDate(2023, 3, 26))
        )
        assertEquals(LocalDate(2023, 3, 26), services.getActivityDetails(1).date)
    }

    @Test
    fun update_non_existing_activity() {
        val appException =
            assertFailsWith<AppException> {
                services.updateActivity(tokenOfUser2, 10, ActivityUpdateInput())
            }
        assertEquals("Activity doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun update_invalid_activity_number() {
        val appException =
            assertFailsWith<AppException> {
                services.updateActivity(tokenOfUser2, -10, ActivityUpdateInput())
            }
        assertEquals("Invalid activity number", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun update_activity_that_is_not_yours() {
        val appException =
            assertFailsWith<AppException> {
                services.updateActivity(tokenOfUser2, 1, ActivityUpdateInput())
            }
        assertEquals("Activity is not yours to update", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }
}
