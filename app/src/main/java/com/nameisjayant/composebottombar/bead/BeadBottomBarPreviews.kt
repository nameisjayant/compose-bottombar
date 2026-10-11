package com.nameisjayant.composebottombar.bead

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.bead.BeadBarItem
import com.nameisjayant.bottombar.bead.BeadBottomBar
import com.nameisjayant.bottombar.bead.BeadBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    BeadBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    BeadBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    BeadBarItem("Shop", ShopIcons.Store, ShopIcons.StoreFilled),
    BeadBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    BeadBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun BeadBottomBarLightPreview() {
    BeadBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = BeadBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun BeadBottomBarDarkPreview() {
    BeadBottomBar(
        items = PreviewItems,
        selectedIndex = 2,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = BeadBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
