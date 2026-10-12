package com.nameisjayant.composebottombar.bloom

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.bloom.BloomBarItem
import com.nameisjayant.bottombar.bloom.BloomBottomBar
import com.nameisjayant.bottombar.bloom.BloomBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// The selected tab shows its label beside the orb, so the bar uses the four tabs it was designed with.
private val BloomTabs = ShopTabs.filter { it.label != "Store" }
private val TabItems = BloomTabs.map { BloomBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun BloomBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = BloomTabs) { selectedIndex, onItemClick ->
        BloomBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = BloomBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 12.dp,
        )
    }
}
