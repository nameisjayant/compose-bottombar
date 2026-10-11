package com.nameisjayant.composebottombar.diamond

import androidx.compose.runtime.Composable
import com.nameisjayant.bottombar.diamond.DiamondBarItem
import com.nameisjayant.bottombar.diamond.DiamondBottomBar
import com.nameisjayant.bottombar.diamond.DiamondBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { DiamondBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun DiamondBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        DiamondBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = DiamondBottomBarDefaults.colors(darkTheme),
        )
    }
}
