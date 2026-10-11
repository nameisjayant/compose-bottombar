package com.nameisjayant.composebottombar.scribble

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.scribble.ScribbleBarItem
import com.nameisjayant.bottombar.scribble.ScribbleBottomBar
import com.nameisjayant.bottombar.scribble.ScribbleBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

private val PreviewItems = listOf(
    ScribbleBarItem("Home", ShopIcons.Home),
    ScribbleBarItem("Search", ShopIcons.Search),
    ScribbleBarItem("Cart", ShopIcons.Cart),
    ScribbleBarItem("Profile", ShopIcons.Profile),
)

@Preview(name = "Light", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun ScribbleBottomBarLightPreview() {
    ScribbleBottomBar(
        items = PreviewItems,
        selectedIndex = 0,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = ScribbleBottomBarDefaults.lightColors(),
        windowInsets = WindowInsets(0.dp),
    )
}

@Preview(name = "Dark", widthDp = 384, backgroundColor = 0xFF000000, showBackground = true)
@Composable
private fun ScribbleBottomBarDarkPreview() {
    ScribbleBottomBar(
        items = PreviewItems,
        selectedIndex = 1,
        onItemClick = {},
        modifier = Modifier.padding(top = 16.dp),
        colors = ScribbleBottomBarDefaults.darkColors(),
        windowInsets = WindowInsets(0.dp),
    )
}
