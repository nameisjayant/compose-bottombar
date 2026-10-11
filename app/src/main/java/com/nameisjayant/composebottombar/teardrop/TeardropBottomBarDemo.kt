package com.nameisjayant.composebottombar.teardrop

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.teardrop.TeardropBarItem
import com.nameisjayant.bottombar.teardrop.TeardropBottomBar
import com.nameisjayant.bottombar.teardrop.TeardropBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// The bar was designed with four tabs, so it skips Store.
private val TeardropTabs = ShopTabs.filter { it.label != "Store" }
private val TabItems = TeardropTabs.map { TeardropBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun TeardropBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = TeardropTabs) { selectedIndex, onItemClick ->
        TeardropBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = TeardropBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
