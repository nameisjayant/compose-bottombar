package com.nameisjayant.composebottombar.gable

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.gable.GableBarItem
import com.nameisjayant.bottombar.gable.GableBottomBar
import com.nameisjayant.bottombar.gable.GableBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    GableBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    GableBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    GableBarItem("Shop", ShopIcons.Store, ShopIcons.StoreFilled),
    GableBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    GableBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun GableBottomBarLightPreview() {
    GableBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = GableBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun GableBottomBarDarkPreview() {
    GableBottomBar(
        items = PreviewItems,
        selectedIndex = 2,
        onItemClick = {},
        modifier = Modifier.padding(16.dp),
        colors = GableBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
