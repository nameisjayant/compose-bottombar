package com.nameisjayant.composebottombar.tile

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.tile.TileBarItem
import com.nameisjayant.bottombar.tile.TileBottomBar
import com.nameisjayant.bottombar.tile.TileBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    TileBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    TileBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    TileBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    TileBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    TileBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun TileBottomBarLightPreview() {
    TileBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = TileBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun TileBottomBarDarkPreview() {
    TileBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = TileBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
