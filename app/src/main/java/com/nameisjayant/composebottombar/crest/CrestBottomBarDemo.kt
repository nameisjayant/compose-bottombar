package com.nameisjayant.composebottombar.crest

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.crest.CrestBarItem
import com.nameisjayant.bottombar.crest.CrestBottomBar
import com.nameisjayant.bottombar.crest.CrestBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { CrestBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun CrestBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        CrestBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            // The bar floats, so keep it off the screen edges.
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            colors = CrestBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
