package pt.isel.ls.sports.webapi.comp

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.sports.domain.ActivityDeleteInput
import pt.isel.ls.sports.domain.ActivityInput
import pt.isel.ls.sports.services.Services

class ActivitiesWebApi(private val services: Services) {

    val routes = routes(
        "api/users/{number}/activities" bind Method.GET to ::getUserActivities,
        "api/sports/{number}/activities" bind Method.GET to ::getSportActivities,
        "api/sports/{number}/activities" bind Method.POST to ::createActivity,
        "api/activities/{number}" bind Method.GET to ::getActivityDetails,
        "api/activities/{number}" bind Method.DELETE to ::deleteActivity,
        "api/activities/delete" bind Method.POST to ::deleteActivities,
        "api/activities" bind Method.GET to ::getActivities,
        "api/activities/{number}" bind Method.PUT to ::updateActivity
    )

    /**
     * Reads the user number, the limit and skip values desired
     * from the request and sends it to getUserActivities in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getUserActivities(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val activities =
                services.activitiesServices.getUserActivities(
                    request.id, request.limit, request.skip
                )
            makeResponse(Status.OK, Json.encodeToString(activities))
        }
    }

    /**
     * Reads the sport number, the limit and skip values desired
     * from the request and sends it to getSportActivities in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getSportActivities(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val activities =
                services.activitiesServices.getSportActivities(request.id, request.limit, request.skip)
            makeResponse(Status.OK, Json.encodeToString(activities))
        }
    }

    /**
     * Decodes a [ActivityInput] and reads the sport number from the
     * request and sends it to createActivity in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun createActivity(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val outputActivity =
                services.activitiesServices.createActivity(
                    request.token, request.id, request.getBodyObject()
                )
            makeResponse(Status.CREATED, Json.encodeToString(outputActivity))
        }
    }

    /**
     * Reads the activity number from the request query and sends it to
     * getActivityDetails in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getActivityDetails(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val activity =
                services.activitiesServices.getActivityDetails(request.id)
            makeResponse(Status.OK, Json.encodeToString(activity))
        }
    }

    /**
     * Reads the user number from the request query and sends it to deleteActivity in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun deleteActivity(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val activity =
                services.activitiesServices.deleteActivity(request.token, request.id)
            makeResponse(Status.OK, Json.encodeToString(activity))
        }
    }

    /**
     * Decodes a [ActivityDeleteInput] from the request sends it to deleteActivities in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun deleteActivities(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val activitiesOutput =
                services.activitiesServices.deleteActivities(request.token, request.getBodyObject())
            makeResponse(Status.OK, Json.encodeToString(activitiesOutput))
        }
    }

    /**
     * Reads the sport and route numbers, the limit and skip values desired, the date and the order
     * from the request query and sends it to getActivities in services
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun getActivities(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val activities =
                services.activitiesServices.getActivities(
                    request.queryToInt("sport"),
                    request.order, request.date,
                    request.queryToInt("route"),
                    request.limit, request.skip
                )
            makeResponse(Status.OK, Json.encodeToString(activities))
        }
    }

    /**
     * Reads the activityNumber desired from the request and sends it to updateActivity in services
     * along with the object containing the requested updates
     * @param request the request made
     * @return a response informing if the request was successful or not
     */
    private fun updateActivity(request: Request): Response {
        printRequest(request)
        return doApiTask {
            val updatedActivity =
                services.activitiesServices.updateActivity(
                    request.token, request.id, request.getBodyObject()
                )
            makeResponse(Status.OK, Json.encodeToString(updatedActivity))
        }
    }
}
