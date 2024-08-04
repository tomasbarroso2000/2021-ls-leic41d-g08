import {div, ul, li, a, h1, input, label, button, select} from "../elements.js"
import {
    API_BASE_URL,
    handleResponse,
    getUpdatePage,
    getCreatePage,
    changeTitle,
    loadNewContent,
    makeActivityDiv,
    makeList,
    handleSubmitWithEnter, handleClickNextInput, SESSION
} from "./handlers-utils.js"
import {
    handleClickActivities,
    handleKeyPressedSport,
    handleKeyPressedRoute,
    handleClickOnCreateActivity,
    handleClickOnUpdateActivity,
    handleClickOnRemoveActivity
} from "./events.js"

/**
 * Filters the activities that are going to be shown
 */
function activitiesSearch(mainContent) {
    changeTitle("Activities Search")
    const sportList = select({id: "sportsSearchMenu"})
    const sportInfoInputLabel = label({for: "sportInfo"}, "Search Sport")
    const sportInfoInput = input({type: "text", id: "sportInfo"})
    const sportResults = div({className: "activitiesSearchBox"}, sportInfoInputLabel, sportInfoInput, sportList)

    const routeList = select({id: "routesSearchMenu"})
    const routeInfoInputLabel = label({for: "routeInfo"}, "Search Route")
    const routeInfoInput = input({type: "text", id: "routeInfo"})
    const routeResults = div({className: "activitiesSearchBox"}, routeInfoInputLabel, routeInfoInput, routeList)

    const dateInputLabel = label({for: "date"}, "Date")
    const dateInput = input({type: "date", id: "date"})
    const submit = button({}, "Search")
    let contentSearch
    if (SESSION.number) {
        const createButton = button({}, "Create")
        createButton.addEventListener('click', () => getCreatePage("activities"))
        contentSearch = div({className: "contentSearch"}, createButton, sportResults, routeResults, dateInputLabel, dateInput, submit)
    } else {
        contentSearch = div({className: "contentSearch"}, sportResults, routeResults, dateInputLabel, dateInput, submit)
    }
    handleSubmitWithEnter(dateInput, handleClickActivities)

    submit.addEventListener('click', handleClickActivities)
    sportInfoInput.addEventListener('input', handleKeyPressedSport)
    routeInfoInput.addEventListener('input', handleKeyPressedRoute)

    const newDiv = div({}, h1({}, "Activities Search"), contentSearch)

    loadNewContent(mainContent, newDiv)
}

/**
 * Creates an activity
 */
function createActivity(mainContent) {
    changeTitle("New Activity")
    const sportList = select({id: "sportsSearchMenu"})
    const sportInfoInputLabel = label({for: "sportInfo"}, "Search Sport")
    const sportInfoInput = input({type: "text", id: "sportInfo"})
    const sportResults = div({className: "activitiesSearchBox"}, sportInfoInputLabel, sportInfoInput, sportList)

    const routeList = select({id: "routesSearchMenu"})
    const routeInfoInputLabel = label({for: "routeInfo"}, "Search Route")
    const routeInfoInput = input({type: "text", id: "routeInfo"})
    const routeResults = div({className: "activitiesSearchBox"}, routeInfoInputLabel, routeInfoInput, routeList)

    const dateInputLabel = label({for: "date"}, "Date")
    const dateInput = input({type: "date", id: "date"})

    const durationInputLabel = label({for: "duration"}, "Duration")
    const durationInput = input({type: "time", step: 0.1, id: "duration"})

    const create = button({}, "Create")

    handleClickNextInput(sportInfoInput, routeInfoInput)
    handleClickNextInput(routeInfoInput, dateInput)
    handleClickNextInput(dateInput, durationInput)
    handleSubmitWithEnter(durationInput, handleClickOnCreateActivity)

    create.addEventListener('click', handleClickOnCreateActivity)
    sportInfoInput.addEventListener('input', handleKeyPressedSport)
    routeInfoInput.addEventListener('input', handleKeyPressedRoute)

    const contentSearch = div({className: "contentSearch"}, sportResults, routeResults, dateInputLabel, dateInput, durationInputLabel, durationInput, create)
    const newDiv = div({}, h1({}, "New Activity Details"), contentSearch)

    loadNewContent(mainContent, newDiv)
}

/**
 * Updates an activity
 */
function updateActivity(mainContent, pathObj) {
    changeTitle("Edit Activity")
    const dateInputLabel = label({for: "date"}, "Date")
    const dateInput = input({type: "date", id: "date"})
    const durationInputLabel = label({for: "duration"}, "Duration")
    const durationInput = input({type: "time", step: 0.1, id: "duration"})
    const update = button({}, "Update")
    const newDiv = div(
        {},
        h1({}, "Edit Activity"),
        dateInputLabel,
        dateInput,
        durationInputLabel,
        durationInput,
        update
    )
    loadNewContent(mainContent, newDiv)

    handleClickNextInput(dateInput, durationInput)
    handleSubmitWithEnter(durationInput, () => handleClickOnUpdateActivity(pathObj.activityNumber))
    update.addEventListener('click', () => handleClickOnUpdateActivity(pathObj.activityNumber))
}

/**
 * Presents the searched activities
 */
function getActivities(mainContent, pathObj) {
    changeTitle("Activities")
    let apiPath = "activities?sport=" + pathObj.sport
    if (pathObj.order) {
        apiPath += "&order=" + pathObj.order
    }
    if (pathObj.route) {
        apiPath += "&route=" + pathObj.route
    }
    if (pathObj.date) {
        apiPath += "&date=" + pathObj.date
    }
    const activities = div({className: "scroll"})
    let newDiv
    if (SESSION.number) {
        const createButton = button({className: "btn"}, "Create")
        createButton.addEventListener("click", () => getCreatePage("activities"))
        newDiv = div({}, h1({}, "Activities"), div({}, createButton), activities)
    } else {
        newDiv = div({}, h1({}, "Activities"), activities)
    }
    const activitiesPath = API_BASE_URL + apiPath
    makeList(activitiesPath, 0, activities, makeActivityDiv)
    loadNewContent(mainContent, newDiv)
}

/**
 * Presents the details of an activity
 */
function getActivity(mainContent, pathObj) {
    changeTitle("Activity Details")
    fetch(API_BASE_URL + "activities/" + pathObj.activityNumber)
        .then(res => handleResponse(res))
        .then(activity => {
            if (!activity) {
                window.location.hash = "activities/search"
            } else {
                const items = []
                const sport = a(
                    {href: "#sports/" + activity.sport.number},
                    "Sport: " + activity.sport.name
                )
                items.push(li({}, sport))
                if (activity.route) {
                    const route = a(
                        {href: "#routes/" + activity.route.number},
                        "Route: " + activity.route.name
                    )
                    items.push(li({}, route))
                }
                const creator = a(
                    {href: "#users/" + activity.user.number},
                    "Creator: " + activity.user.name
                )
                items.push(li({}, creator))
                const list = ul({},
                    li({}, "Number: " + activity.number),
                    li({}, "Date: " + activity.date),
                    li({}, "Duration: " + parseDuration(activity.duration)),
                    ...items
                )
                let textDiv
                if (activity.user.number === SESSION.number) {
                    const updateButton = button({}, "Update")
                    updateButton.addEventListener("click", () => getUpdatePage("activities", activity.number))
                    const removeButton = button({}, "Remove")
                    removeButton.addEventListener("click", () => handleClickOnRemoveActivity(pathObj.activityNumber))
                    textDiv = div({className: "text"}, list, updateButton, removeButton)
                } else {
                    textDiv = div({className: "text"}, list)
                }
                loadNewContent(mainContent, textDiv)
            }
        })
}

/**
 * Reformats the duration of the activity presented
 */
function parseDuration(duration) {
    return duration.replace("PT", "")
        .replace("H", "h ")
        .replace("M", "m ")
        .replace("S", "s")
}

const handlers = {
    activitiesSearch,
    getActivities,
    getActivity,
    updateActivity,
    createActivity
}

export default handlers;