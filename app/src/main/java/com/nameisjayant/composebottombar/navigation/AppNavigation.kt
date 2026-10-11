package com.nameisjayant.composebottombar.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nameisjayant.composebottombar.cradle.CradleBottomBarDemo
import com.nameisjayant.composebottombar.dome.DomeBottomBarDemo
import com.nameisjayant.composebottombar.halo.HaloBottomBarDemo
import com.nameisjayant.composebottombar.home.BottomBarType
import com.nameisjayant.composebottombar.home.HomeScreen
import com.nameisjayant.composebottombar.valley.ValleyBottomBarDemo
import kotlinx.serialization.Serializable

/** Top-level routes: the catalog, and one full-screen demo per bottom bar. */
@Serializable data object CatalogRoute : NavKey
@Serializable data object HaloDemoRoute : NavKey
@Serializable data object DomeDemoRoute : NavKey
@Serializable data object CradleDemoRoute : NavKey
@Serializable data object ValleyDemoRoute : NavKey

private fun BottomBarType.route(): NavKey = when (this) {
    BottomBarType.Halo -> HaloDemoRoute
    BottomBarType.Dome -> DomeDemoRoute
    BottomBarType.Cradle -> CradleDemoRoute
    BottomBarType.Valley -> ValleyDemoRoute
}

@Composable
fun AppNavigation(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    val backStack = rememberNavBackStack(CatalogRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        transitionSpec = {
            slideInHorizontally(tween(300)) { it / 3 } + fadeIn(tween(300)) togetherWith fadeOut(tween(150))
        },
        popTransitionSpec = {
            fadeIn(tween(300)) togetherWith slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(300))
        },
        predictivePopTransitionSpec = {
            fadeIn(tween(300)) togetherWith slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(300))
        },
        entryProvider = entryProvider {
            entry<CatalogRoute> {
                HomeScreen(
                    darkTheme = darkTheme,
                    onDarkThemeChange = onDarkThemeChange,
                    onBottomBarClick = { type -> backStack.add(type.route()) },
                )
            }
            entry<HaloDemoRoute> {
                HaloBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<DomeDemoRoute> {
                DomeBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<CradleDemoRoute> {
                CradleBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<ValleyDemoRoute> {
                ValleyBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
        },
    )
}
