import {div, ul, li, h1, input, button, label, a} from "../elements.js"
import {
    API_BASE_URL,
    LISTED,
    handleResponse,
    getUpdatePage,
    getCreatePage,
    changeTitle,
    loadNewContent,
    makeRouteDiv,
    makeList, handleClickNextInput, handleSubmitWithEnter, SESSION
} from "./handlers-utils.js"
import {handleClickOnUpdateRoute, handleClickOnCreateRoute} from "./events.js"

/**
 * Presents the routes
 */
function getRoutes(mainContent) {
    changeTitle("Routes")
    const routes = div({className: "scroll"})
    let newDiv
    if (SESSION.number) {
        const createButton = button({className: "btn"}, "Create")
        createButton.addEventListener("click", () => getCreatePage("routes"))
        newDiv = div({}, h1({}, "Routes"), div({}, createButton), routes)
    } else {
        newDiv = div({}, h1({}, "Routes"), routes)
    }
    const routesPath = API_BASE_URL + "routes"
    makeList(routesPath, 0, routes, makeRouteDiv)
    loadNewContent(mainContent, newDiv)
}

/**
 * Presents the details of a route
 */
function getRoute(mainContent, pathObj) {
    changeTitle("Route Details")
    fetch(API_BASE_URL + "routes/" + pathObj.routeNumber)
        .then(res => handleResponse(res))
        .then(route => {
            if (!route) {
                window.location.hash = "routes?limit=" + LISTED + "&skip=0"
            } else {
                const items = [
                    li({}, "Number: " + route.number),
                    li({}, "Start location: " + route.startLocation),
                    li({}, "End location: " + route.endLocation),
                    li({}, "Distance: " + route.distance)
                ]
                const creator = a(
                    {href: "#users/" + route.user.number},
                    "Creator: " + route.user.name
                )
                items.push(li({}, creator))
                const list = ul({}, ...items)
                let textDiv
                if (route.user.number === SESSION.number) {
                    const updateButton = button({}, "Update")
                    updateButton.addEventListener("click", () => getUpdatePage("routes", route.number))
                    textDiv = div({className: "text"}, list, updateButton)
                } else {
                    textDiv = div({className: "text"}, list)
                }
                loadNewContent(mainContent, textDiv)
            }
        })
}

/**
 * Updates a route
 */
function updateRoute(mainContent, pathObj) {
    changeTitle("Edit Route")
    const startLocationInputLabel = label({for: "startLocationInfo"}, "Start Location")
    const startLocationInput = input({type: "text", id: "startLocationInfo"})
    const endLocationInputLabel = label({for: "endLocationInfo"}, "End Location")
    const endLocationInput = input({type: "text", id: "endLocationInfo"})
    const distanceInputLabel = label({for: "distanceInfo"}, "Distance")
    const distanceInput = input({type: "number", min: 0, id: "distanceInfo"})
    const update = button({}, "Update")
    const newDiv = div({},
        h1({}, "Edit Route"),
        startLocationInputLabel,
        startLocationInput,
        endLocationInputLabel,
        endLocationInput,
        distanceInputLabel,
        distanceInput,
        update
    )
    loadNewContent(mainContent, newDiv)
    handleClickNextInput(startLocationInput, endLocationInput)
    handleClickNextInput(endLocationInput, distanceInput)
    handleSubmitWithEnter(distanceInput, () => handleClickOnUpdateRoute(pathObj.routeNumber))
    update.addEventListener('click', () => handleClickOnUpdateRoute(pathObj.routeNumber))
}

/**
 * Creates a route
 */
function createRoute(mainContent) {
    changeTitle("New Route")
    const startLocationInputLabel = label({for: "startLocationInfo"}, "Start Location")
    const startLocationInput = input({type: "text", id: "startLocationInfo"})
    const endLocationInputLabel = label({for: "endLocationInfo"}, "End Location")
    const endLocationInput = input({type: "text", id: "endLocationInfo"})
    const distanceInputLabel = label({for: "distanceInfo"}, "Distance")
    const distanceInput = input({type: "number", min: 0, id: "distanceInfo"})
    const create = button({}, "Create")
    handleClickNextInput(startLocationInput, endLocationInput)
    handleClickNextInput(endLocationInput, distanceInput)
    handleSubmitWithEnter(distanceInput, handleClickOnCreateRoute)
    create.addEventListener('click', handleClickOnCreateRoute)
    const newDiv = div({},
        h1({}, "New Route Details"),
        startLocationInputLabel,
        startLocationInput,
        endLocationInputLabel,
        endLocationInput,
        distanceInputLabel,
        distanceInput,
        create
    )

    loadNewContent(mainContent, newDiv)
}

const handlers = {
    getRoutes,
    getRoute,
    updateRoute,
    createRoute
}

export default handlers;