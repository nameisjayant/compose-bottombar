package com.nameisjayant.composebottombar.rail

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.rail.RailBarItem
import com.nameisjayant.bottombar.rail.RailBottomBar
import com.nameisjayant.bottombar.rail.RailBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    RailBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    RailBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    RailBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    RailBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun RailBottomBarLightPreview() {
    RailBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = RailBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun RailBottomBarDarkPreview() {
    RailBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = RailBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
