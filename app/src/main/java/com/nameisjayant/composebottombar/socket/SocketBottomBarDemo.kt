package com.nameisjayant.composebottombar.socket

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.socket.SocketBarItem
import com.nameisjayant.bottombar.socket.SocketBottomBar
import com.nameisjayant.bottombar.socket.SocketBottomBarDefaults
import com.nameisjayant.composebottombar.demo.ShopTabs
import com.nameisjayant.composebottombar.demo.ShopTabsDemo

private val TabItems = ShopTabs.map { SocketBarItem(it.label, it.icon, it.selectedIcon) }

@Composable
fun SocketBottomBarDemo(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    ShopTabsDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange) { selectedIndex, onItemClick ->
        SocketBottomBar(
            items = TabItems,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            // The bar is a floating pill, so keep it off the screen edges.
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            colors = SocketBottomBarDefaults.colors(darkTheme),
            shadowElevation = if (darkTheme) 0.dp else 8.dp,
        )
    }
}
