package com.nameisjayant.composebottombar.peak

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.peak.PeakBarItem
import com.nameisjayant.bottombar.peak.PeakBottomBar
import com.nameisjayant.bottombar.peak.PeakBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    PeakBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    PeakBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    PeakBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    PeakBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun PeakBottomBarLightPreview() {
    PeakBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = PeakBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun PeakBottomBarDarkPreview() {
    PeakBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = PeakBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
