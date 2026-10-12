package com.nameisjayant.composebottombar.spotlight

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.spotlight.SpotlightBarItem
import com.nameisjayant.bottombar.spotlight.SpotlightBottomBar
import com.nameisjayant.bottombar.spotlight.SpotlightBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// The selected tab shows its label under the icon, so the bar uses the four tabs it was designed with.
private val SpotlightTabs = ShopTabs.filter { it.label != "Store" }
private val TabItems = SpotlightTabs.map { SpotlightBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun SpotlightBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = SpotlightTabs) { selectedIndex, onItemClick ->
        SpotlightBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            // The bar floats, so keep it off the screen edges.
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            colors = SpotlightBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
