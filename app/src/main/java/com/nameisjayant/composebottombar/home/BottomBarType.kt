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
    Cradle(
        title = "CradleBottomBar",
        description = "Flat bar whose top edge dips into a notch that cradles a raised orb. Icons fill in on selection.",
    ),
    Valley(
        title = "ValleyBottomBar",
        description = "Bar whose whole top edge sags in one curve under a glowing orb, which presses into the valley when selected.",
    ),
    Socket(
        title = "SocketBottomBar",
        description = "Floating pill whose selected icon lifts into a bubble, resting in a socket that glides from tab to tab.",
    ),
}
