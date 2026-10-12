package com.nameisjayant.composebottombar.buoy

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.buoy.BuoyBarItem
import com.nameisjayant.bottombar.buoy.BuoyBottomBar
import com.nameisjayant.bottombar.buoy.BuoyBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    BuoyBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    BuoyBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    BuoyBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    BuoyBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun BuoyBottomBarLightPreview() {
    BuoyBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = BuoyBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun BuoyBottomBarDarkPreview() {
    BuoyBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = BuoyBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
