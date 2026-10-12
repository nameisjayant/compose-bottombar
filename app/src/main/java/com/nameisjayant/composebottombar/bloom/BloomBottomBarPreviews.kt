package com.nameisjayant.composebottombar.bloom

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.bloom.BloomBarItem
import com.nameisjayant.bottombar.bloom.BloomBottomBar
import com.nameisjayant.bottombar.bloom.BloomBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    BloomBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    BloomBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    BloomBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    BloomBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun BloomBottomBarLightPreview() {
    BloomBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = BloomBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun BloomBottomBarDarkPreview() {
    BloomBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = BloomBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
