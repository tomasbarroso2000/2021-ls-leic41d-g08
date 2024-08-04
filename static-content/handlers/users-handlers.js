import {div, ul, li, h1, select, label, input, button} from "../elements.js"
import {
    handleClickOnCreateUser,
    handleClickOnLogin,
    handleClickRankings,
    handleKeyPressedRoute,
    handleKeyPressedSport
} from "./events.js"
import {
    API_BASE_URL,
    LISTED,
    handleResponse,
    changeTitle,
    makeList,
    loadNewContent,
    makeUserActivityDiv,
    makeUserDiv, handleSubmitWithEnter, handleClickNextInput, getCreatePage, SESSION, updateLoginButton
} from "./handlers-utils.js"

/**
 * Presents the users
 */
function getUsers(mainContent) {
    changeTitle("Users")
    const users = div({className: "scroll"})
    const newDiv = div({}, h1({}, "Users"), users)
    const usersPath = API_BASE_URL + "users"
    makeList(usersPath, 0, users, makeUserDiv)
    loadNewContent(mainContent, newDiv)
}

/**
 * Presents the details of a user
 */
function getUser(mainContent, pathObj) {
    changeTitle("User Details")
    fetch(API_BASE_URL + "users/" + pathObj.userNumber)
        .then(res => handleResponse(res))
        .then(user => {
            if (!user) {
                window.location.hash = "users?limit=" + LISTED + "&skip=0"
            } else {
                const activitiesDiv = div({className: "innerScroll"})
                const activitiesPath = API_BASE_URL + "users/" + user.number + "/activities"
                makeList(activitiesPath, 0, activitiesDiv, makeUserActivityDiv)

                const textDiv = div(
                    {className: "text"},
                    ul({},
                        li({}, "Number: " + user.number),
                        li({}, "Name: " + user.name),
                        li({}, "Email: " + user.email),
                        activitiesDiv
                    )
                )
                loadNewContent(mainContent, textDiv)
            }
        })
}

/**
 * Searches for user rankings
 */
function userRankingsSearch(mainContent) {
    changeTitle("User Rankings Search")
    const sportList = select({id: "sportsSearchMenu"})
    const sportInfoInputLabel = label({for: "sportInfo"}, "Search Sport")
    const sportInfoInput = input({type: "text", id: "sportInfo"})
    const sportResults = div({className: "activitiesSearchBox"}, sportInfoInputLabel, sportInfoInput, sportList)

    const routeList = select({id: "routesSearchMenu"})
    const routeInfoInputLabel = label({for: "routeInfo"}, "Search Route")
    const routeInfoInput = input({type: "text", id: "routeInfo"})
    const routeResults = div({className: "activitiesSearchBox"}, routeInfoInputLabel, routeInfoInput, routeList)

    const submit = button({}, "Search")

    const contentSearch = div({className: "contentSearch"}, sportResults, routeResults, submit)

    handleSubmitWithEnter(routeInfoInput, handleClickRankings)
    submit.addEventListener('click', handleClickRankings)
    sportInfoInput.addEventListener('input', handleKeyPressedSport)
    routeInfoInput.addEventListener('input', handleKeyPressedRoute)

    const newDiv = div({}, h1({}, "User Rankings Search"), contentSearch)

    loadNewContent(mainContent, newDiv)

}

/**
 * Presents the user rankings
 */
function getUserRankings(mainContent, pathObj) {
    changeTitle("User Rankings")
    const users = div({className: "scroll"})
    const newDiv = div({}, h1({}, "User Rankings"), users)
    const userRankingsPath = API_BASE_URL + "rankings/" + pathObj.sportNumber + "/" + pathObj.routeNumber
    makeList(userRankingsPath, 0, users, makeUserDiv)
    loadNewContent(mainContent, newDiv)
}

/**
 * Presents the login page
 */
function getLogin(mainContent) {
    changeTitle("Login")
    const title = h1({}, "Login")
    const emailInputLabel = label({for: "email"}, "Email")
    const emailInput = input({type: "email", id: "email"})

    const passwordInputLabel = label({for: "password"}, "Password")
    const passwordInput = input({type: "password", id: "password"})

    handleClickNextInput(emailInput, passwordInput)

    const divEmail = div({}, emailInputLabel, emailInput)
    const divPassword = div({}, passwordInputLabel, passwordInput)

    const loginButton = button({id: "login"}, "Login")
    const createButton = button({id: "createUser"}, "Sign Up")

    handleSubmitWithEnter(passwordInput, handleClickOnLogin)
    loginButton.addEventListener("click", handleClickOnLogin)
    createButton.addEventListener("click", () => getCreatePage("users"))

    const divButtons = div({}, loginButton, createButton)

    const loginDiv = div({id: "login"}, title, divEmail, divPassword, divButtons)
    loadNewContent(mainContent, loginDiv)
}

/**
 * Presents the sign up page
 */
function getSignUp(mainContent) {
    changeTitle("Sign Up")
    const title = h1({}, "Sign Up")
    const nameInputLabel = label({for: "name"}, "Name")
    const nameInput = input({type: "text", id: "name"})

    const emailInputLabel = label({for: "email"}, "Email")
    const emailInput = input({type: "email", id: "email"})

    const passwordInputLabel = label({for: "password"}, "Password")
    const passwordInput = input({type: "password", id: "password"})

    handleClickNextInput(nameInput, emailInput)
    handleClickNextInput(emailInput, passwordInput)

    const divUsername = div({}, nameInputLabel, nameInput)
    const divEmail = div({}, emailInputLabel, emailInput)
    const divPassword = div({}, passwordInputLabel, passwordInput)

    const createButton = button({id: "createUser"}, "Sign Up")

    handleSubmitWithEnter(passwordInput, handleClickOnCreateUser)
    createButton.addEventListener("click", handleClickOnCreateUser)

    const divButtons = div({}, createButton)

    const signUpDiv = div({id: "signup"}, title, divUsername, divEmail, divPassword, divButtons)
    loadNewContent(mainContent, signUpDiv)
}

/**
 * Executes the logout operation
 */
function getLogout() {
    SESSION.number = undefined
    SESSION.name = undefined
    SESSION.token = undefined
    window.location.hash = "home"
    updateLoginButton()
}

const handlers = {
    getUsers,
    getUser,
    userRankingsSearch,
    getUserRankings,
    getLogin,
    getSignUp,
    getLogout,
}

export default handlers;