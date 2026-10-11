package com.nameisjayant.composebottombar.peak

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.peak.PeakBarItem
import com.nameisjayant.bottombar.peak.PeakBottomBar
import com.nameisjayant.bottombar.peak.PeakBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// The selected tab shows its label under the icon, so the bar uses the four tabs it was designed with.
private val PeakTabs = ShopTabs.filter { it.label != "Store" }
private val TabItems = PeakTabs.map { PeakBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun PeakBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = PeakTabs) { selectedIndex, onItemClick ->
        PeakBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            // The bar is a floating pill, so keep it off the screen edges.
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            colors = PeakBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
