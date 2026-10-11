package com.nameisjayant.composebottombar.wave

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.wave.WaveBarItem
import com.nameisjayant.bottombar.wave.WaveBottomBar
import com.nameisjayant.bottombar.wave.WaveBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    WaveBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    WaveBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    WaveBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun WaveBottomBarLightPreview() {
    WaveBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = WaveBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun WaveBottomBarDarkPreview() {
    WaveBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = WaveBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
