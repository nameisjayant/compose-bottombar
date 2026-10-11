package com.nameisjayant.composebottombar.gable

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.gable.GableBarItem
import com.nameisjayant.bottombar.gable.GableBottomBar
import com.nameisjayant.bottombar.gable.GableBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { GableBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun GableBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        GableBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            // The bar floats, so keep it off the screen edges.
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            colors = GableBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
