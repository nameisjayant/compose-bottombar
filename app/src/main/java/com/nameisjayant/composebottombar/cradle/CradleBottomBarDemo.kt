package com.nameisjayant.composebottombar.cradle

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.cradle.CradleBarItem
import com.nameisjayant.bottombar.cradle.CradleBottomBar
import com.nameisjayant.bottombar.cradle.CradleBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { CradleBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun CradleBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        CradleBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            colors = CradleBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
