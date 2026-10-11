package com.nameisjayant.composebottombar.orbit

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.orbit.OrbitBarItem
import com.nameisjayant.bottombar.orbit.OrbitBottomBar
import com.nameisjayant.bottombar.orbit.OrbitBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    OrbitBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    OrbitBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    OrbitBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    OrbitBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    OrbitBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun OrbitBottomBarLightPreview() {
    OrbitBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = OrbitBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun OrbitBottomBarDarkPreview() {
    OrbitBottomBar(
        items = PreviewItems,
        selectedIndex = 3,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = OrbitBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
