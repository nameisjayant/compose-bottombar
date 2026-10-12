package com.nameisjayant.composebottombar.arch

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.arch.ArchBarItem
import com.nameisjayant.bottombar.arch.ArchBottomBar
import com.nameisjayant.bottombar.arch.ArchBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    ArchBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    ArchBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    ArchBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    ArchBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    ArchBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun ArchBottomBarLightPreview() {
    ArchBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = ArchBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun ArchBottomBarDarkPreview() {
    ArchBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = ArchBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
