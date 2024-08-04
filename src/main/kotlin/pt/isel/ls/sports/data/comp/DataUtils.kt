package pt.isel.ls.sports.data.comp

const val LOCATION_SIZE = 3

/**
 * Get the route name
 * @param start the start location of the route
 * @param end the end location of the route
 * @param distance the distance of the route
 */
fun getRouteName(start: String?, end: String?, distance: Double?) =
    "${start?.take(LOCATION_SIZE)}-${end?.take(LOCATION_SIZE)} ($distance km)"
