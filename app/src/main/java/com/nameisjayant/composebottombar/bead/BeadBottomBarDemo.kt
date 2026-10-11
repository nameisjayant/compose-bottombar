package com.nameisjayant.composebottombar.bead

import androidx.compose.runtime.Composable
import com.nameisjayant.bottombar.bead.BeadBarItem
import com.nameisjayant.bottombar.bead.BeadBottomBar
import com.nameisjayant.bottombar.bead.BeadBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { BeadBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun BeadBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        BeadBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = BeadBottomBarDefaults.colors(darkTheme),
        )
    }
}
