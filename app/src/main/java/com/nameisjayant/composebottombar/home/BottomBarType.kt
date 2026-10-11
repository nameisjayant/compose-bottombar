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
    Gable(
        title = "GableBottomBar",
        description = "Floating bar with a roof-like peak; the selected icon lifts into a bubble that glides along the slope.",
    ),
    Diamond(
        title = "DiamondBottomBar",
        description = "Flat bar with a glowing diamond on its top edge, which spins a quarter turn into place when selected.",
    ),
    Wave(
        title = "WaveBottomBar",
        description = "Bar with a rolling wave edge: every tab floats in its own dip, and the selected ring fills into a solid orb.",
    ),
    Rail(
        title = "RailBottomBar",
        description = "Flat bar of icon-only tabs; the selected one widens to show its label, framed by two rails above and below.",
    ),
    Crest(
        title = "CrestBottomBar",
        description = "Floating bar whose two wings climb towards a glowing orb resting in a socket between them.",
    ),
    Scribble(
        title = "ScribbleBottomBar",
        description = "Flat bar of icon-only tabs; a hand-drawn loop draws itself around the selected icon, with a dot underneath.",
    ),
    Bead(
        title = "BeadBottomBar",
        description = "Flat bar with a small notch over the selected tab, holding a bead that hops from tab to tab.",
    ),
}
