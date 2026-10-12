package com.nameisjayant.composebottombar.arch

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.arch.ArchBarItem
import com.nameisjayant.bottombar.arch.ArchBottomBar
import com.nameisjayant.bottombar.arch.ArchBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { ArchBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun ArchBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        ArchBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = ArchBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
