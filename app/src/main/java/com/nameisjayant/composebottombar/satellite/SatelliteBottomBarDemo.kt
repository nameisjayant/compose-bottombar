package com.nameisjayant.composebottombar.satellite

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.satellite.SatelliteBarItem
import com.nameisjayant.bottombar.satellite.SatelliteBottomBar
import com.nameisjayant.bottombar.satellite.SatelliteBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// The bar was designed with four tabs, so it skips Store.
private val SatelliteTabs = ShopTabs.filter { it.label != "Store" }
private val TabItems = SatelliteTabs.map { SatelliteBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun SatelliteBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = SatelliteTabs) { selectedIndex, onItemClick ->
        SatelliteBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = SatelliteBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
