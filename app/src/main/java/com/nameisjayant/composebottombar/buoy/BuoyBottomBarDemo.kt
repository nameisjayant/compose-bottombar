package com.nameisjayant.composebottombar.buoy

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.buoy.BuoyBarItem
import com.nameisjayant.bottombar.buoy.BuoyBottomBar
import com.nameisjayant.bottombar.buoy.BuoyBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// The selected tab shows its label under the icon, so the bar uses the four tabs it was designed with.
private val BuoyTabs = ShopTabs.filter { it.label != "Store" }
private val TabItems = BuoyTabs.map { BuoyBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun BuoyBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = BuoyTabs) { selectedIndex, onItemClick ->
        BuoyBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            // The bar floats, so keep it off the screen edges.
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            colors = BuoyBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
