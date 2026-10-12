package com.nameisjayant.composebottombar.collar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.collar.CollarBarItem
import com.nameisjayant.bottombar.collar.CollarBottomBar
import com.nameisjayant.bottombar.collar.CollarBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    CollarBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    CollarBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    CollarBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    CollarBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    CollarBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun CollarBottomBarLightPreview() {
    CollarBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        colors = CollarBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun CollarBottomBarDarkPreview() {
    CollarBottomBar(
        items = PreviewItems,
        selectedIndex = 2,
        onItemClick = {},
        colors = CollarBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
