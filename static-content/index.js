import router from "./router.js";
import handlers from "./handlers.js";
import {updateLoginButton} from "./handlers/handlers-utils.js"

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

/**
 * Loads the routes handlers for the different pages
 */
function loadHandler() {

    router.addRouteHandler("home", handlers.getHome)
    router.addRouteHandler("login", handlers.getLogin)
    router.addRouteHandler("logout", handlers.getLogout)
    router.addRouteHandler("users/new", handlers.getSignUp)
    router.addRouteHandler("users", handlers.getUsers)
    router.addRouteHandler("users/rankings/search", handlers.userRankingsSearch)
    router.addRouteHandler("users/rankings/{sportNumber}/{routeNumber}", handlers.getUserRankings)
    router.addRouteHandler("users/{userNumber}", handlers.getUser)
    router.addRouteHandler("routes", handlers.getRoutes)
    router.addRouteHandler("routes/new", handlers.createRoute)
    router.addRouteHandler("routes/{routeNumber}", handlers.getRoute)
    router.addRouteHandler("routes/{routeNumber}/update", handlers.updateRoute)
    router.addRouteHandler("sports", handlers.getSports)
    router.addRouteHandler("sports/new", handlers.createSport)
    router.addRouteHandler("sports/{sportNumber}", handlers.getSport)
    router.addRouteHandler("sports/{sportNumber}/update", handlers.updateSport)
    router.addRouteHandler("activities", handlers.getActivities)
    router.addRouteHandler("activities/search", handlers.activitiesSearch)
    router.addRouteHandler("activities/new", handlers.createActivity)
    router.addRouteHandler("activities/{activityNumber}", handlers.getActivity)
    router.addRouteHandler("activities/{activityNumber}/update", handlers.updateActivity)
    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")

    hashChangeHandler()
    updateLoginButton()
}

/**
 * Handles a change to the fragment
 */
function hashChangeHandler() {
    const mainContent = document.getElementById("mainContent")
    const path = window.location.hash.replace("#", "")

    const routeObj = router.getRouteObj(path)
    const handler = routeObj.handler
    const pathObj = routeObj.pathObj
    handler(mainContent, pathObj)
    loadPageLocation()
}

/**
 * Highlights the current location in the navigation menu
 */
function loadPageLocation() {
    const location = window.location.hash.replace("#", "").split('?')[0].split('/')[0]
    document.querySelectorAll(".link-primary").forEach(item => item.style.color = "white")
    document.querySelectorAll(".link-secondary").forEach(item => item.style.color = "white")
    document.getElementById(location).style.color = "#F55353"
}