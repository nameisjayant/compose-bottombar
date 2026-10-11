package com.nameisjayant.composebottombar.dome

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.dome.DomeBarItem
import com.nameisjayant.bottombar.dome.DomeBottomBar
import com.nameisjayant.bottombar.dome.DomeBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    DomeBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    DomeBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    DomeBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    DomeBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    DomeBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun DomeBottomBarLightPreview() {
    DomeBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = DomeBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun DomeBottomBarDarkPreview() {
    DomeBottomBar(
        items = PreviewItems,
        selectedIndex = 3,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = DomeBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
