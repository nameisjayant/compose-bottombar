package com.nameisjayant.composebottombar.collar

import androidx.compose.runtime.Composable
import com.nameisjayant.bottombar.collar.CollarBarItem
import com.nameisjayant.bottombar.collar.CollarBottomBar
import com.nameisjayant.bottombar.collar.CollarBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { CollarBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun CollarBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        CollarBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = CollarBottomBarDefaults.colors(darkTheme),
        )
    }
}
