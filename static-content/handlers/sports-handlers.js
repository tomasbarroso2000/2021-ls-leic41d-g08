import {div, ul, li, h1, input, button, label, a} from "../elements.js"
import {
    API_BASE_URL,
    LISTED,
    handleResponse,
    getUpdatePage,
    getCreatePage,
    changeTitle,
    loadNewContent,
    makeList,
    makeSportDiv, handleClickNextInput, handleSubmitWithEnter, SESSION
} from "./handlers-utils.js"
import {handleClickOnUpdateSport, handleClickOnCreateSport} from "./events.js"

/**
 * Presents the sports
 */
function getSports(mainContent) {
    changeTitle("Sports")
    const sports = div({className: "scroll"})
    let newDiv
    if (SESSION.number) {
        const createButton = button({className: "btn"}, "Create")
        createButton.addEventListener("click", () => getCreatePage("sports"))
        newDiv = div({}, h1({}, "Sports"), div({}, createButton), sports)
    } else {
        newDiv = div({}, h1({}, "Sports"), sports)
    }
    const sportsPath = API_BASE_URL + "sports"
    makeList(sportsPath, 0, sports, makeSportDiv)
    loadNewContent(mainContent, newDiv)
}

/**
 * Presents the details of a sport
 */
function getSport(mainContent, pathObj) {
    changeTitle("Sport Details")
    fetch(API_BASE_URL + "sports/" + pathObj.sportNumber)
        .then(res => handleResponse(res))
        .then(sport => {
            if (!sport) {
                window.location.hash = "sports?limit=" + LISTED + "&skip=0"
            } else {
                const items = [
                    li({}, "Number: " + sport.number),
                    li({}, "Name: " + sport.name)
                ]
                if (sport.description) {
                    items.push(li({}, "Description: " + sport.description))
                }
                const creator = a(
                    {href: "#users/" + sport.user.number},
                    "Creator: " + sport.user.name
                )
                items.push(li({}, creator))
                const list = ul({}, ...items)
                let textDiv
                if (sport.user.number === SESSION.number) {
                    const updateButton = button({}, "Update")
                    updateButton.addEventListener("click", () => getUpdatePage("sports", sport.number))
                    textDiv = div({className: "text"}, list, updateButton)
                } else {
                    textDiv = div({className: "text"}, list)
                }
                loadNewContent(mainContent, textDiv)
            }
        })
}

/**
 * Updates a sport
 */
function updateSport(mainContent, pathObj) {
    changeTitle("Edit Sport")
    const nameInputLabel = label({for: "nameInput"}, "Sport Name")
    const nameInput = input({type: "text", id: "nameInput"})
    const descriptionInputLabel = label({for: "descriptionInput"}, "Sport Description")
    const descriptionInput = input({type: "text", id: "descriptionInput"})
    const update = button({}, "Update")

    handleClickNextInput(nameInput, descriptionInput)
    handleSubmitWithEnter(descriptionInput, () => handleClickOnUpdateSport(pathObj.sportNumber))

    update.addEventListener('click', () => handleClickOnUpdateSport(pathObj.sportNumber))

    const newDiv = div(
        {},
        h1({}, "Edit Sport"),
        nameInputLabel,
        nameInput,
        descriptionInputLabel,
        descriptionInput,
        update
    )
    loadNewContent(mainContent, newDiv)
}

/**
 * Creates a sport
 */
function createSport(mainContent) {
    changeTitle("New Sport")
    const nameInputLabel = label({for: "nameInput"}, "Sport Name")
    const nameInput = input({type: "text", id: "nameInput"})
    const descriptionInputLabel = label({for: "descriptionInput"}, "Sport Description")
    const descriptionInput = input({type: "text", id: "descriptionInput"})
    const create = button({}, "Create")

    handleClickNextInput(nameInput, descriptionInput)
    handleSubmitWithEnter(descriptionInput, handleClickOnCreateSport)

    create.addEventListener('click', handleClickOnCreateSport)

    const newDiv = div(
        {},
        h1({}, "New Sport Details"),
        nameInputLabel,
        nameInput,
        descriptionInputLabel,
        descriptionInput,
        create
    )
    loadNewContent(mainContent, newDiv)
}

const handlers = {
    getSports,
    getSport,
    updateSport,
    createSport
}

export default handlers;