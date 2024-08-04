import handlers from "../handlers.js";
import router from "../router.js";

const assert = chai.assert

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

describe('Router tests', function () {
    describe('Home test', function () {
        it('home', function () {
            const path = "home"
            assert.equal("getHome", router.getRouteObj(path).handler.name)
        });
    })
    describe('Users tests', function () {
        it('login', function () {
            const path = "login"
            assert.equal("getLogin", router.getRouteObj(path).handler.name)
        });
        it('logout', function () {
            const path = "logout"
            assert.equal("getLogout", router.getRouteObj(path).handler.name)
        });
        it('users/new', function () {
            const path = "users/new"
            assert.equal("getSignUp", router.getRouteObj(path).handler.name)
        });
        it('users', function () {
            const path = "users"
            assert.equal("getUsers", router.getRouteObj(path).handler.name)
        });
        it('users/rankings/search', function () {
            const path = "users/rankings/search"
            assert.equal("userRankingsSearch", router.getRouteObj(path).handler.name)
        });
        it('users/rankings/{sportNumber}/{routeNumber}', function () {
            const path = "users/rankings/1/1"
            assert.equal("getUserRankings", router.getRouteObj(path).handler.name)
        });
        it('users/{userNumber}', function () {
            const path = "users/1"
            assert.equal("getUser", router.getRouteObj(path).handler.name)
        });
    })
    describe('Routes tests', function () {
        it('routes', function () {
            const path = "routes"
            assert.equal("getRoutes", router.getRouteObj(path).handler.name)
        });
        it('routes/new', function () {
            const path = "routes/new"
            assert.equal("createRoute", router.getRouteObj(path).handler.name)
        });
        it('routes/{routeNumber}', function () {
            const path = "routes/1"
            assert.equal("getRoute", router.getRouteObj(path).handler.name)
        });
        it('routes/{routeNumber}/update', function () {
            const path = "routes/1/update"
            assert.equal("updateRoute", router.getRouteObj(path).handler.name)
        });
    })
    describe('Sports tests', function () {
        it('sports', function () {
            const path = "sports"
            assert.equal("getSports", router.getRouteObj(path).handler.name)
        });
        it('sports/new', function () {
            const path = "sports/new"
            assert.equal("createSport", router.getRouteObj(path).handler.name)
        });
        it('sports/{sportNumber}', function () {
            const path = "sports/1"
            assert.equal("getSport", router.getRouteObj(path).handler.name)
        });
        it('sports/{sportNumber}/update', function () {
            const path = "sports/1/update"
            assert.equal("updateSport", router.getRouteObj(path).handler.name)
        });
    })
    describe('Activities tests', function () {
        it('activities', function () {
            const path = "activities"
            assert.equal("getActivities", router.getRouteObj(path).handler.name)
        });
        it('activities/search', function () {
            const path = "activities/search"
            assert.equal("activitiesSearch", router.getRouteObj(path).handler.name)
        });
        it('activities/new', function () {
            const path = "activities/new"
            assert.equal("createActivity", router.getRouteObj(path).handler.name)
        });
        it('activities/{activityNumber}', function () {
            const path = "activities/1"
            assert.equal("getActivity", router.getRouteObj(path).handler.name)
        });
        it('activities/{activityNumber}/update', function () {
            const path = "activities/1/update"
            assert.equal("updateActivity", router.getRouteObj(path).handler.name)
        });
    })
    describe('Invalid path test', function () {
        it('invalid path', function () {
            const path = "invalid/path"
            assert.isNotOk(router.getRouteObj(path).handler.name)
        });
    })
});