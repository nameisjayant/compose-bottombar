package com.nameisjayant.composebottombar.wave

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.wave.WaveBarItem
import com.nameisjayant.bottombar.wave.WaveBottomBar
import com.nameisjayant.bottombar.wave.WaveBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// Every tab sits in its own dip, so the bar uses the three tabs it was designed with.
private val WaveTabs = ShopTabs.filter { it.label in setOf("Home", "Cart", "Profile") }
private val TabItems = WaveTabs.map { WaveBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun WaveBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = WaveTabs) { selectedIndex, onItemClick ->
        WaveBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = WaveBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
