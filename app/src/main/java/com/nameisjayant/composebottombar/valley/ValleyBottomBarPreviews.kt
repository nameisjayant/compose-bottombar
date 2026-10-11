package com.nameisjayant.composebottombar.valley

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.valley.ValleyBarItem
import com.nameisjayant.bottombar.valley.ValleyBottomBar
import com.nameisjayant.bottombar.valley.ValleyBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    ValleyBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    ValleyBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    ValleyBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    ValleyBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    ValleyBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

@Preview(name = "Light", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun ValleyBottomBarLightPreview() {
    ValleyBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = ValleyBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 380, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun ValleyBottomBarDarkPreview() {
    ValleyBottomBar(
        items = PreviewItems,
        selectedIndex = 3,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = ValleyBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
