import {div, h1, img} from "./elements.js"
import usersHandlers from "./handlers/users-handlers.js"
import routesHandlers from "./handlers/routes-handlers.js"
import sportsHandlers from "./handlers/sports-handlers.js"
import activitiesHandlers from "./handlers/activities-handlers.js"
import {changeTitle, loadNewContent} from "./handlers/handlers-utils.js"

/**
 * Presents the home page
 */
function getHome(mainContent) {
    changeTitle("Home")
    const par = h1({id: "homeImageText"}, "Laboratório de Software")
    const image = img({
        src: "./images/ls-bg1.jpg",
        alt: "https://www.pexels.com/pt-br/foto/campo-de-atletismo-marrom-e-branco-163444/",
        id: "homeImage"
    })
    const icon = img({
        src: "./images/icon.png",
        alt: "https://icons8.com/icon/set/sports/material",
        id: "icon"
    })
    const homeDiv = div({}, image, par, icon)
    loadNewContent(mainContent, homeDiv)
}

const handlers = {
    getHome,
    getLogin: usersHandlers.getLogin,
    getSignUp: usersHandlers.getSignUp,
    getLogout: usersHandlers.getLogout,
    getUsers: usersHandlers.getUsers,
    getUser: usersHandlers.getUser,
    userRankingsSearch: usersHandlers.userRankingsSearch,
    getUserRankings: usersHandlers.getUserRankings,
    getRoutes: routesHandlers.getRoutes,
    getRoute: routesHandlers.getRoute,
    updateRoute: routesHandlers.updateRoute,
    createRoute: routesHandlers.createRoute,
    getSports: sportsHandlers.getSports,
    getSport: sportsHandlers.getSport,
    updateSport: sportsHandlers.updateSport,
    createSport: sportsHandlers.createSport,
    activitiesSearch: activitiesHandlers.activitiesSearch,
    getActivities: activitiesHandlers.getActivities,
    getActivity: activitiesHandlers.getActivity,
    updateActivity: activitiesHandlers.updateActivity,
    createActivity: activitiesHandlers.createActivity
}

export default handlers