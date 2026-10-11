package com.nameisjayant.composebottombar.teardrop

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.teardrop.TeardropBarItem
import com.nameisjayant.bottombar.teardrop.TeardropBottomBar
import com.nameisjayant.bottombar.teardrop.TeardropBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    TeardropBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    TeardropBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    TeardropBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    TeardropBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun TeardropBottomBarLightPreview() {
    TeardropBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = TeardropBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun TeardropBottomBarDarkPreview() {
    TeardropBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = TeardropBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
