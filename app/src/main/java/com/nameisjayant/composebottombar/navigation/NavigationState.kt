package com.nameisjayant.composebottombar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

/**
 * One back stack per bottom-bar tab, plus the selected tab. Everything survives configuration changes
 * and process death because the keys are serializable [NavKey]s.
 */
@Composable
fun rememberNavigationState(startRoute: NavKey, topLevelRoutes: List<NavKey>): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer()),
    ) {
        mutableStateOf(startRoute)
    }
    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(startRoute = startRoute, topLevelRoute = topLevelRoute, backStacks = backStacks)
    }
}

class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var topLevelRoute: NavKey by topLevelRoute

    /**
     * Entries for NavDisplay. Each tab gets its own saveable state holder so scroll positions and
     * rememberSaveable values live on while another tab is showing.
     */
    @Composable
    fun toDecoratedEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): List<NavEntry<NavKey>> {
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                entryProvider = entryProvider,
            )
        }
        // "Exit through home": the start tab always sits underneath whichever tab is selected.
        val routesInUse = if (topLevelRoute == startRoute) listOf(startRoute) else listOf(startRoute, topLevelRoute)
        return routesInUse.flatMap { decoratedEntries[it].orEmpty() }
    }
}

class Navigator(private val state: NavigationState) {

    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            // Re-tapping the selected tab pops it back to its root, like most tab bars do.
            if (route == state.topLevelRoute) {
                val stack = state.backStacks.getValue(route)
                while (stack.size > 1) stack.removeLastOrNull()
            }
            state.topLevelRoute = route
        } else {
            state.backStacks.getValue(state.topLevelRoute).add(route)
        }
    }

    fun goBack() {
        val stack = state.backStacks.getValue(state.topLevelRoute)
        // At the root of a tab, back returns to the start tab instead of popping the root.
        if (stack.last() == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            stack.removeLastOrNull()
        }
    }
}
