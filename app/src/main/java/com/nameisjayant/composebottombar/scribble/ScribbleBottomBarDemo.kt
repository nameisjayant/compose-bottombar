package com.nameisjayant.composebottombar.scribble

import androidx.compose.runtime.Composable
import com.nameisjayant.bottombar.scribble.ScribbleBarItem
import com.nameisjayant.bottombar.scribble.ScribbleBottomBar
import com.nameisjayant.bottombar.scribble.ScribbleBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

// The bar was designed with four outline-only tabs, so it skips Store and the filled icons.
private val ScribbleTabs = ShopTabs.filter { it.label != "Store" }
private val TabItems = ScribbleTabs.map { ScribbleBarItem(it.label, it.icon) }

@Composable
fun ScribbleBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange, tabs = ScribbleTabs) { selectedIndex, onItemClick ->
        ScribbleBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = ScribbleBottomBarDefaults.colors(darkTheme),
        )
    }
}
