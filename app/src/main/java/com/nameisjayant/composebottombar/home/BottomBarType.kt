package com.nameisjayant.composebottombar.home

/** Every bottom bar the library ships. Add an entry here (and a route in AppNavigation) for each new bar. */
enum class BottomBarType(val title: String, val description: String) {
    Halo(
        title = "HaloBottomBar",
        description = "Flat bar with a raised halo around the center item. Selection animates the icon and label.",
    ),
}
