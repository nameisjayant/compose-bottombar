package com.nameisjayant.composebottombar.socket

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.socket.SocketBarItem
import com.nameisjayant.bottombar.socket.SocketBottomBar
import com.nameisjayant.bottombar.socket.SocketBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    SocketBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    SocketBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    SocketBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    SocketBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun SocketBottomBarLightPreview() {
    SocketBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = SocketBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun SocketBottomBarDarkPreview() {
    SocketBottomBar(
        items = PreviewItems,
        selectedIndex = 2,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = SocketBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
