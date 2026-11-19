package com.example.proyecto_final_eventia.navigation

object Screen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val EVENT_DETAIL = "event/{eventId}"
    const val VENUE_DETAIL = "venue/{venueId}"

    fun eventDetail(id: Int) = "event/$id"
    fun venueDetail(id: Int) = "venue/$id"

    // Login with optional return destination for deep-link back after auth
    fun loginWithReturn(returnRoute: String) = "login?return=$returnRoute"
    const val LOGIN_WITH_RETURN = "login?return={returnRoute}"
}
