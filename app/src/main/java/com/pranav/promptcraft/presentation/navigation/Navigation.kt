package com.pranav.promptcraft.presentation.navigation

/**
 * Navigation destinations for the app
 */
object Destinations {
    const val LOGIN = "login"
    const val HOME = "home"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val RESULT = "result"
}

/**
 * Bottom navigation items
 */
enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: String // We'll use Material Icons
) {
    HOME(Destinations.HOME, "Home", "home"),
    HISTORY(Destinations.HISTORY, "History", "history")
}
