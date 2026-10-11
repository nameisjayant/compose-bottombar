package com.nameisjayant.composebottombar.rail

import androidx.compose.runtime.Composable
import com.nameisjayant.bottombar.rail.RailBarItem
import com.nameisjayant.bottombar.rail.RailBottomBar
import com.nameisjayant.bottombar.rail.RailBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// The selected tab widens to show its label, so the bar uses the four tabs it was designed with.
private val RailTabs = ShopTabs.filter { it.label != "Store" }
private val TabItems = RailTabs.map { RailBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun RailBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = RailTabs) { selectedIndex, onItemClick ->
        RailBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = RailBottomBarDefaults.colors(darkTheme),
        )
    }
}
