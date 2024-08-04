package pt.isel.ls.sports.server

import org.http4k.routing.ResourceLoader
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.isel.ls.sports.data.Db
import pt.isel.ls.sports.services.Services
import pt.isel.ls.sports.webapi.WebApi

val logger: Logger = LoggerFactory.getLogger("pt.isel.ls.sports.server.Server")

/**
 * Builds the app with the API routes
 */
fun buildApp(): RoutingHttpHandler {
    val data = Db()
    val services = Services(data)
    val webApi = WebApi(services)
    val singlePageApp = singlePageApp(ResourceLoader.Directory("static-content"))
    return routes(webApi.routes, singlePageApp)
}

/**
 * Main function that starts the server
 */
fun main() {
    val app = buildApp()
    val port = System.getenv("PORT").toInt()
    app.asServer(Jetty(port)).start()
    logger.info("Server started listening on port $port")
}
