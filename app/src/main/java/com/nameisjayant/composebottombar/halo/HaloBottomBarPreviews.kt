package com.nameisjayant.composebottombar.halo

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.halo.HaloBarItem
import com.nameisjayant.bottombar.halo.HaloBottomBar
import com.nameisjayant.bottombar.halo.HaloBottomBarDefaults

private val PreviewItems = listOf(
    HaloBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    HaloBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    HaloBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    HaloBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    HaloBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun HaloBottomBarLightPreview() {
    HaloBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        colors = HaloBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun HaloBottomBarDarkPreview() {
    HaloBottomBar(
        items = PreviewItems,
        selectedIndex = 2,
        onItemClick = {},
        colors = HaloBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
