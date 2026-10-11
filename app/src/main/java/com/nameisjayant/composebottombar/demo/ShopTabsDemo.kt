package com.nameisjayant.composebottombar.demo

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nameisjayant.composebottombar.halo.ShopIcons
import com.nameisjayant.composebottombar.navigation.Navigator
import com.nameisjayant.composebottombar.navigation.rememberNavigationState
import kotlinx.serialization.Serializable

@Serializable data object HomeRoute : NavKey
@Serializable data object SearchRoute : NavKey
@Serializable data object StoreRoute : NavKey
@Serializable data object CartRoute : NavKey
@Serializable data object ProfileRoute : NavKey

/** A screen pushed on top of a tab, to show that every tab keeps its own back stack. */
@Serializable data class DetailRoute(val tab: String, val depth: Int) : NavKey

/** One shop tab, with the icon pair every bar's item class is built from. */
class ShopTab(val route: NavKey, val label: String, val icon: ImageVector, val selectedIcon: ImageVector)

// Store sits in the middle, so center-button bars draw it as their raised item.
val ShopTabs = listOf(
    ShopTab(HomeRoute, "Home", ShopIcons.Home, ShopIcons.HomeFilled),
    ShopTab(SearchRoute, "Search", ShopIcons.Search, ShopIcons.SearchFilled),
    ShopTab(StoreRoute, "Store", ShopIcons.Store, ShopIcons.StoreFilled),
    ShopTab(CartRoute, "Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    ShopTab(ProfileRoute, "Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)
private val TabRoutes = ShopTabs.map { it.route }

/**
 * A small shop app with one back stack per tab, used by every bar's demo. [bottomBar] draws the bar
 * for the given selected tab index and reports taps through `onItemClick`.
 */
@Composable
fun ShopTabsDemo(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    bottomBar: @Composable (selectedIndex: Int, onItemClick: (Int) -> Unit) -> Unit,
) {
    val navigationState = rememberNavigationState(startRoute = HomeRoute, topLevelRoutes = TabRoutes)
    val navigator = remember(navigationState) { Navigator(navigationState) }

    val entryProvider = entryProvider {
        ShopTabs.forEach { tab ->
            entry(tab.route) {
                DemoScreen(
                    title = tab.label,
                    subtitle = "Root of the ${tab.label} tab",
                    darkTheme = darkTheme,
                    onDarkThemeChange = onDarkThemeChange,
                    onOpenDetail = { navigator.navigate(DetailRoute(tab.label, depth = 1)) },
                )
            }
        }
        entry<DetailRoute> { route ->
            DemoScreen(
                title = "${route.tab} · ${route.depth}",
                subtitle = "Pushed on the ${route.tab} stack. Switch tabs and come back: it's still here.",
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onOpenDetail = { navigator.navigate(route.copy(depth = route.depth + 1)) },
                onBack = navigator::goBack,
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            bottomBar(TabRoutes.indexOf(navigationState.topLevelRoute)) { index -> navigator.navigate(TabRoutes[index]) }
        },
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toDecoratedEntries(entryProvider),
            onBack = navigator::goBack,
            modifier = Modifier.padding(innerPadding),
            // Fade-through: the outgoing screen fades out quickly, the new one fades and grows in.
            transitionSpec = {
                (fadeIn(tween(220, delayMillis = 90)) + scaleIn(tween(220, delayMillis = 90), initialScale = 0.94f)) togetherWith
                    fadeOut(tween(90))
            },
            popTransitionSpec = {
                (fadeIn(tween(220, delayMillis = 90)) + scaleIn(tween(220, delayMillis = 90), initialScale = 0.94f)) togetherWith
                    fadeOut(tween(90))
            },
        )
    }
}

@Composable
private fun DemoScreen(
    title: String,
    subtitle: String,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onOpenDetail: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) {
                    TextButton(onClick = dropUnlessResumed(block = onBack)) { Text("‹ Back") }
                }
                Spacer(Modifier.weight(1f))
                Text("Dark", style = MaterialTheme.typography.labelLarge, color = colors.onBackground)
                Spacer(Modifier.size(8.dp))
                Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary, RoundedCornerShape(24.dp))
                    .padding(24.dp),
            ) {
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(Modifier.height(6.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = dropUnlessResumed(block = onOpenDetail),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = colors.primary),
                ) { Text("Open details") }
            }
        }
        items(PlaceholderRows) { row ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 18.dp),
            ) {
                Text("$title item $row", style = MaterialTheme.typography.bodyLarge, color = colors.onSurface)
            }
        }
    }
}

private val PlaceholderRows = (1..12).toList()

