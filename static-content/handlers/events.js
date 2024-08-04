import {
    updateLoginButton,
    API_BASE_URL,
    handleResponse,
    LISTED,
    SESSION,
    handleClickNextInput,
    createAlert
} from "./handlers-utils.js";
import {option} from "../elements.js";

/**
 * Handles click on activities search
 */
export function handleClickActivities() {
    const inputSportValue = document.querySelector("#sportsSearchMenu").value
    const inputRouteValue = document.querySelector("#routesSearchMenu").value
    const inputDateValue = document.querySelector("#date").value

    if (inputSportValue) {
        let uri = "activities?sport=" + inputSportValue + "&order=descending"
        if (inputRouteValue) {
            uri += "&route=" + inputRouteValue
        }

        if (inputDateValue) {
            uri += "&date=" + inputDateValue
        }
        window.location.hash = uri
    } else {
        createAlert("Sport can't be empty")
    }
}

/**
 * Handles click on user rankings
 */
export function handleClickRankings() {
    const inputSportValue = document.querySelector("#sportsSearchMenu").value
    const inputRouteValue = document.querySelector("#routesSearchMenu").value

    if (!inputSportValue || !inputRouteValue) {
        window.location.hash = "users"
    } else {
        window.location.hash = "users/rankings/" + inputSportValue + "/" + inputRouteValue + "?limit=" + LISTED + "&skip=0"
    }
}

/**
 * Handles key pressed on sports search
 */
export function handleKeyPressedSport() {
    const inputSportInfo = document.querySelector("#sportInfo")

    handleClickNextInput(inputSportInfo, document.querySelector("#routeInfo"))

    const sportList = document.querySelector("#sportsSearchMenu")
    sportList.innerHTML = "";
    if (inputSportInfo.value.length !== 0) {
        fetch(API_BASE_URL + "search/sports?q=" + inputSportInfo.value + "&limit=10")
            .then(res => res.json())
            .then(sportsObj => {
                sportsObj.list.forEach(sport => {
                    const sportOption = option({}, sport.name)
                    sportOption.value = sport.number
                    sportList.appendChild(sportOption)
                })
            })
    }
}

/**
 * Handles key pressed on routes search
 */
export function handleKeyPressedRoute() {
    const inputRouteInfo = document.querySelector("#routeInfo")

    handleClickNextInput(inputRouteInfo, document.querySelector("#date"))

    const routeList = document.querySelector("#routesSearchMenu")
    routeList.innerHTML = "";
    if (inputRouteInfo.value.length !== 0) {
        fetch(API_BASE_URL + "search/routes?q=" + inputRouteInfo.value + "&limit=10")
            .then(res => res.json())
            .then(routesObj => {
                routesObj.list.forEach(route => {
                    const content = route.startLocation.slice(0, 3) + "-" + route.endLocation.slice(0, 3) + " (" + route.distance + " km)"
                    const routeOption = option({value: route.number}, content)
                    routeList.appendChild(routeOption)
                })
            })
    }
}

/**
 * Handles click on create activity button
 */
export function handleClickOnCreateActivity() {
    const dateInput = document.querySelector("#date")
    const durationInput = document.querySelector("#duration")
    const routeList = document.querySelector("#routesSearchMenu")
    const sportList = document.querySelector("#sportsSearchMenu")
    const body = {}

    if (dateInput.value) {
        body.date = dateInput.value
    }

    if (durationInput.value) {
        body.duration = reverseParseDuration(durationInput.value)
    }

    if (routeList.value) {
        body.routeNumber = parseInt(routeList.value)
    }

    fetch(
        API_BASE_URL + "sports/" + sportList.value + "/activities",
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + SESSION.token
            },
            body: JSON.stringify(body)
        }
    )
        .then(res => handleResponse(res))
        .then(res => {
            window.location.hash = "activities/" + res.number
        })
}

/**
 * Handles click on update activity button
 */
export function handleClickOnUpdateActivity(activityNumber) {
    const dateInput = document.querySelector("#date")
    const durationInput = document.querySelector("#duration")
    const body = {}

    if (dateInput.value) {
        body.date = dateInput.value
    }
    if (durationInput.value) {
        body.duration = reverseParseDuration(durationInput.value)
    }

    fetch(
        API_BASE_URL + "activities/" + activityNumber,
        {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + SESSION.token
            },
            body: JSON.stringify(body)
        }
    )
        .then(res => handleResponse(res))
        .then(res => {
            window.location.hash = "activities/" + res.number
        })
}

/**
 * Handles click on remove activity button
 */
export function handleClickOnRemoveActivity(activityNumber) {
    fetch(
        API_BASE_URL + "activities/" + activityNumber,
        {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + SESSION.token
            }
        }
    )
        .then(res => handleResponse(res))
        .then(() => {
            window.location.hash = "activities/search"
        })
}

/**
 * Handles click on update route button
 */
export function handleClickOnUpdateRoute(routeNumber) {
    const startLocationInput = document.querySelector("#startLocationInfo")
    const endLocationInput = document.querySelector("#endLocationInfo")
    const distanceInput = document.querySelector("#distanceInfo")

    const body = {}
    if (startLocationInput.value) {
        body.startLocation = startLocationInput.value
    }
    if (endLocationInput.value) {
        body.endLocation = endLocationInput.value
    }
    if (distanceInput.value) {
        body.distance = distanceInput.value
    }
    fetch(
        API_BASE_URL + "routes/" + routeNumber,
        {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + SESSION.token
            },
            body: JSON.stringify(body)
        }
    )
        .then(res => handleResponse(res))
        .then(() => {
            window.location.hash = "routes/" + routeNumber
        })
}

/**
 * Handles click on create route button
 */
export function handleClickOnCreateRoute() {
    const startLocationInput = document.querySelector("#startLocationInfo")
    const endLocationInput = document.querySelector("#endLocationInfo")
    const distanceInput = document.querySelector("#distanceInfo")

    const body = {}
    if (startLocationInput.value) {
        body.startLocation = startLocationInput.value
    }
    if (endLocationInput.value) {
        body.endLocation = endLocationInput.value
    }
    if (distanceInput.value) {
        body.distance = distanceInput.value
    }

    fetch(
        API_BASE_URL + "routes",
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + SESSION.token
            },
            body: JSON.stringify(body)
        }
    )
        .then(res => handleResponse(res))
        .then(res => {
            window.location.hash = "routes/" + res.number
        })
}

/**
 * Handles click on update sport button
 */
export function handleClickOnUpdateSport(sportNumber) {
    const nameInput = document.querySelector("#nameInput")
    const descriptionInput = document.querySelector("#descriptionInput")

    const body = {}
    if (nameInput.value) {
        body.name = nameInput.value
    }
    if (descriptionInput.value) {
        body.description = descriptionInput.value
    }
    fetch(
        API_BASE_URL + "sports/" + sportNumber,
        {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + SESSION.token
            },
            body: JSON.stringify(body)
        }
    )
        .then(res => handleResponse(res))
        .then(() => {
            window.location.hash = "sports/" + sportNumber
        })
}

/**
 * Handles click on create sport button
 */
export function handleClickOnCreateSport() {
    const nameInput = document.querySelector("#nameInput")
    const descriptionInput = document.querySelector("#descriptionInput")

    const body = {}
    if (nameInput.value) {
        body.name = nameInput.value
    }
    if (descriptionInput.value) {
        body.description = descriptionInput.value
    }
    fetch(
        API_BASE_URL + "sports",
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + SESSION.token
            },
            body: JSON.stringify(body)
        }
    )
        .then(res => handleResponse(res))
        .then(res => {
            window.location.hash = "sports/" + res.number
        })
}

/**
 * Handles click on login button
 */
export function handleClickOnLogin() {
    const email = document.querySelector("#email").value
    const password = document.querySelector("#password").value
    fetch(
        API_BASE_URL + "session",
        {
            method: 'GET',
            headers: {
                'Authorization': 'Basic ' + btoa(email + ':' + password)
            }
        }
    )
        .then(res => handleResponse(res))
        .then((res) => {
            if (res) {
                SESSION.token = res.token
                SESSION.number = res.number
                SESSION.name = res.name
                window.location.hash = "home"
                updateLoginButton()
            }
        })
}

/**
 * Handles click on create user button
 */
export function handleClickOnCreateUser() {
    const name = document.querySelector("#name").value
    const email = document.querySelector("#email").value
    const password = document.querySelector("#password").value
    const body = {name, email, password}

    fetch(
        API_BASE_URL + "users",
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        }
    )
        .then(res => handleResponse(res))
        .then((res) => {
            SESSION.token = res.token
            SESSION.number = res.number
            SESSION.name = name
            window.location.hash = "home"
            updateLoginButton()
        })
}

/**
 * Handler for the infinite scroll
 */
export function infiniteScrollHandler(apiPath, contentDiv, skip, makeItem, hasMore) {
    if (Math.floor(contentDiv.scrollHeight - contentDiv.scrollTop) === contentDiv.clientHeight) {
        if (hasMore.hasMore) {
            skip.skip += LISTED
            let url
            if (apiPath.includes('?')) {
                url = apiPath + "&limit=" + LISTED + "&skip=" + skip.skip
            } else {
                url = apiPath + "?limit=" + LISTED + "&skip=" + skip.skip
            }
            fetch(url)
                .then(res => handleResponse(res))
                .then(items => {
                    items.list.forEach((item => {
                        contentDiv.appendChild(makeItem(item))
                    }))
                    hasMore.hasMore = items.hasMore
                })
        }
    }
}

/**
 * Parses the duration to be interpreted by the server api
 */
function reverseParseDuration(duration) {
    const splitDuration = duration.split(':')
    return "PT" + splitDuration[0] + "H" + splitDuration[1] + "M" + splitDuration[2] + "S"
}