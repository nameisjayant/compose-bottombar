package com.nameisjayant.composebottombar.crest

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.crest.CrestBarItem
import com.nameisjayant.bottombar.crest.CrestBottomBar
import com.nameisjayant.bottombar.crest.CrestBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    CrestBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    CrestBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    CrestBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    CrestBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    CrestBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun CrestBottomBarLightPreview() {
    CrestBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = CrestBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun CrestBottomBarDarkPreview() {
    CrestBottomBar(
        items = PreviewItems,
        selectedIndex = 2,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = CrestBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
