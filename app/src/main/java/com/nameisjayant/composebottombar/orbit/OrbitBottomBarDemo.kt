package com.nameisjayant.composebottombar.orbit

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.orbit.OrbitBarItem
import com.nameisjayant.bottombar.orbit.OrbitBottomBar
import com.nameisjayant.bottombar.orbit.OrbitBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { OrbitBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun OrbitBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        // The orbit bar has rounded bottom corners, so it floats a little above the screen edge.
        OrbitBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            colors = OrbitBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
