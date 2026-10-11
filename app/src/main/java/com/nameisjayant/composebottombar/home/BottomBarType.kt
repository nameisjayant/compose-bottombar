package com.nameisjayant.composebottombar.home

/** Every bottom bar the library ships. Add an entry here (and a route in AppNavigation) for each new bar. */
enum class BottomBarType(val title: String, val description: String) {
    Halo(
        title = "HaloBottomBar",
        description = "Flat bar with a raised halo around the center item. Selection animates the icon and label.",
    ),
    Dome(
        title = "DomeBottomBar",
        description = "Rounded bar whose top swells into a dome around a glowing orb. Labels slide in under the selected tab.",
    ),
}
