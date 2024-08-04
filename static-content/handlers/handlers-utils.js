import {div, a, h1, p, button} from "../elements.js"
import {infiniteScrollHandler} from "./events.js";

export const API_BASE_URL = "/api/"
export const LISTED = 4
export const SESSION = {}

/**
 * Handles a response and throws alerts when necessary
 */
export function handleResponse(res) {
    switch (res.status) {
        case 200:
        case 201: {
            return res.json()
        }
        case 401: {
            if (window.location.hash === "#login")
                res.json()
                    .then(() => createAlert("Invalid credentials"))
            else
                res.json()
                    .then(() => createAlert("Login required"))
            break
        }
        case 400:
        case 404: {
            res.json()
                .then(jsonRes => createAlert(jsonRes.errorMessage))
            break
        }
        default: {
            createAlert("Something went wrong")
        }
    }
}

/**
 * Creates an alert for errors
 */
export function createAlert(message) {
    const btn = button({}, "OK")
    const alert = div(
        {className: "alert"},
        h1({}, "Ups!"),
        p({}, message),
        btn
    )
    if (!document.querySelector(".alert")) {
        document.body.appendChild(alert)
        btn.focus()
    }
    btn.addEventListener("click", () => document.body.removeChild(document.getElementsByClassName("alert")[0]))

    setTimeout(function () {
        const alert = document.getElementsByClassName("alert")[0]
        if (alert) {
            document.body.removeChild(alert)
        }
    }, 5000)

}

/**
 * Gets an update page for the given type
 */
export function getUpdatePage(type, number) {
    window.location.hash = type + "/" + number + "/update"
}

/**
 * Gets a creation page for the given type
 */
export function getCreatePage(type) {
    window.location.hash = type + "/new"
}

/**
 * Changes the page title
 */
export function changeTitle(newTitle) {
    document.querySelector("title").innerText = "Sports - " + newTitle
}

/**
 * Builds and presents a list of items
 */
export function makeList(apiPath, skip, contentDiv, makeDiv) {
    let url
    if (apiPath.includes('?')) {
        url = apiPath + "&limit=" + LISTED + "&skip=" + skip
    } else {
        url = apiPath + "?limit=" + LISTED + "&skip=" + skip
    }
    fetch(url)
        .then(res => handleResponse(res))
        .then(items => {
            const itemsArray = []
            items.list.forEach((item => {
                itemsArray.push(makeDiv(item))
            }))
            const hasMore = {hasMore: items.hasMore}
            const skipValue = {skip}
            contentDiv.addEventListener(
                "scroll",
                () => infiniteScrollHandler(apiPath, contentDiv, skipValue, makeDiv, hasMore)
            )
            contentDiv.replaceChildren(...itemsArray)
        })
}

/**
 * Makes a user activity item div
 */
export function makeUserActivityDiv(activity) {
    return a(
        {href: "#activities/" + activity.number},
        div({}, "Activity " + activity.number)
    )
}

/**
 * Makes a sport item div
 */
export function makeSportDiv(sport) {
    return a(
        {href: "#sports/" + sport.number},
        div(
            {className: "content"},
            p({}, "Number: " + sport.number),
            p({}, "Name: " + sport.name)
        )
    )
}

/**
 * Makes a user item div
 */
export function makeUserDiv(user) {
    return a(
        {href: "#users/" + user.number},
        div(
            {className: "content"},
            p({}, "Number: " + user.number),
            p({}, "Name: " + user.name)
        )
    )
}

/**
 * Makes a route item div
 */
export function makeRouteDiv(route) {
    return a(
        {href: "#routes/" + route.number},
        div(
            {className: "content"},
            p({}, "Number: " + route.number),
            p({}, "Route: " + route.startLocation.slice(0, 3) + "-" + route.endLocation.slice(0, 3))
        )
    )
}

/**
 * Makes an activity item div
 */
export function makeActivityDiv(activity) {
    return a(
        {href: "#activities/" + activity.number},
        div(
            {className: "content"},
            p({}, "Number: " + activity.number),
            p({}, "Creator: " + activity.user.name)
        )
    )
}

/**
 * Loads the new content and fades it in
 */
export function loadNewContent(mainContent, newChildren) {
    mainContent.replaceChildren(newChildren)
    mainContent.childNodes.forEach((elem) => {
        elem.style.animation = "fadein 0.5s"
    })
}

/**
 * Updates appearence of the login section
 */
export function updateLoginButton() {
    const loginout = document.querySelector("#loginout")
    if (SESSION.token) {
        const userInfo =
            a(
                {id: "username", href: "#users/" + SESSION.number},
                p({}, SESSION.name)
            )
        document.getElementsByTagName("nav")[0].appendChild(userInfo)
        loginout.innerText = "Logout"
        loginout.addEventListener("click", () => window.location.hash = "logout")

    } else {
        const currUser = document.querySelector("#username")
        if (currUser) document.getElementsByTagName("nav")[0].removeChild(currUser)
        loginout.innerText = "Login"
        loginout.addEventListener("click", () => window.location.hash = "login")
    }
}

/**
 * Handles click on enter key
 */
export function handleClickNextInput(inputListener, element) {
    inputListener.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            event.preventDefault()
            if (element) element.focus()
        }
    })
}

/**
 * Handles submit with enter key
 */
export function handleSubmitWithEnter(element, eventHandler) {
    element.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            event.preventDefault()
            eventHandler()
        }
    })
}