package com.nameisjayant.composebottombar.diamond

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.diamond.DiamondBarItem
import com.nameisjayant.bottombar.diamond.DiamondBottomBar
import com.nameisjayant.bottombar.diamond.DiamondBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    DiamondBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    DiamondBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    DiamondBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    DiamondBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    DiamondBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun DiamondBottomBarLightPreview() {
    DiamondBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = DiamondBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun DiamondBottomBarDarkPreview() {
    DiamondBottomBar(
        items = PreviewItems,
        selectedIndex = 3,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = DiamondBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
