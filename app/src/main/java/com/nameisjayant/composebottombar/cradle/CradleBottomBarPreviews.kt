package com.nameisjayant.composebottombar.cradle

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.cradle.CradleBarItem
import com.nameisjayant.bottombar.cradle.CradleBottomBar
import com.nameisjayant.bottombar.cradle.CradleBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    CradleBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    CradleBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    CradleBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    CradleBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    CradleBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun CradleBottomBarLightPreview() {
    CradleBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = CradleBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun CradleBottomBarDarkPreview() {
    CradleBottomBar(
        items = PreviewItems,
        selectedIndex = 3,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = CradleBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
