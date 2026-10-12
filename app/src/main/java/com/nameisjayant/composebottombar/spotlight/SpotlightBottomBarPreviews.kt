package com.nameisjayant.composebottombar.spotlight

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.spotlight.SpotlightBarItem
import com.nameisjayant.bottombar.spotlight.SpotlightBottomBar
import com.nameisjayant.bottombar.spotlight.SpotlightBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    SpotlightBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    SpotlightBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    SpotlightBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    SpotlightBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun SpotlightBottomBarLightPreview() {
    SpotlightBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = SpotlightBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun SpotlightBottomBarDarkPreview() {
    SpotlightBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = SpotlightBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
