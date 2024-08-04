const routes = []
let notFoundRouteHandler = () => {
    throw "Route handler for unknown routes not defined"
}

/**
 * Adds a route to the routes list
 */
function addRouteHandler(pathTemplate, handler) {
    const pathTemplateArray = pathTemplate.split('/')
    routes.push({pathTemplateArray, handler})
}

/**
 * Adds a default route to the routes list if the route handler was not found
 */
function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

/**
 * Gets the route object using the given path
 */
function getRouteObj(path) {
    const routeObj = findPath(path)
    return routeObj || {handler: notFoundRouteHandler}
}

/**
 * Searches for the given path in the routes list
 */
function findPath(path) {
    const pathArray = path.split('/')
    const pathObj = {}
    let availableRoutes = routes.filter((route) => route.pathTemplateArray.length == pathArray.length)
    for (let i = 0; i < pathArray.length; i++) {
        let element = pathArray[i]
        if (element.includes('?')) {
            const query = element.split('?')[1]
            query.split('&').forEach(q => {
                const splitQ = q.split('=')
                pathObj[splitQ[0]] = splitQ[1]
            });
            element = element.split('?')[0]
        }
        let filteredRoutes = availableRoutes.filter((route) => route.pathTemplateArray[i] == element);
        if (filteredRoutes.length == 0) {
            filteredRoutes = availableRoutes.filter((route) => {
                if (route.pathTemplateArray[i]) {
                    const isParam = route.pathTemplateArray[i].includes('{') && route.pathTemplateArray[i].includes('}')
                    if (isParam) {
                        const prop = route.pathTemplateArray[i].replace('{', "").replace('}', "")
                        pathObj[prop] = element
                    }
                    return isParam
                }
                return false
            });
        }
        availableRoutes = filteredRoutes
    }
    if (availableRoutes.length == 0) {
        return undefined;
    } else {
        const route = availableRoutes[0]
        return {pathObj, handler: route.handler}
    }
}


const router = {
    addRouteHandler,
    getRouteObj,
    addDefaultNotFoundRouteHandler
}

export default router