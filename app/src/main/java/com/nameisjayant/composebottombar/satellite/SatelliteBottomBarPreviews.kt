package com.nameisjayant.composebottombar.satellite

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.satellite.SatelliteBarItem
import com.nameisjayant.bottombar.satellite.SatelliteBottomBar
import com.nameisjayant.bottombar.satellite.SatelliteBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    SatelliteBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    SatelliteBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    SatelliteBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    SatelliteBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun SatelliteBottomBarLightPreview() {
    SatelliteBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = SatelliteBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun SatelliteBottomBarDarkPreview() {
    SatelliteBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = SatelliteBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
