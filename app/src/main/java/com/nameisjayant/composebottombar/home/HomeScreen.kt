package com.nameisjayant.composebottombar.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import com.nameisjayant.bottombar.cradle.CradleBarItem
import com.nameisjayant.bottombar.cradle.CradleBottomBar
import com.nameisjayant.bottombar.cradle.CradleBottomBarDefaults
import com.nameisjayant.bottombar.diamond.DiamondBarItem
import com.nameisjayant.bottombar.diamond.DiamondBottomBar
import com.nameisjayant.bottombar.diamond.DiamondBottomBarDefaults
import com.nameisjayant.bottombar.dome.DomeBarItem
import com.nameisjayant.bottombar.dome.DomeBottomBar
import com.nameisjayant.bottombar.dome.DomeBottomBarDefaults
import com.nameisjayant.bottombar.gable.GableBarItem
import com.nameisjayant.bottombar.gable.GableBottomBar
import com.nameisjayant.bottombar.gable.GableBottomBarDefaults
import com.nameisjayant.bottombar.halo.HaloBarItem
import com.nameisjayant.bottombar.halo.HaloBottomBar
import com.nameisjayant.bottombar.halo.HaloBottomBarDefaults
import com.nameisjayant.bottombar.socket.SocketBarItem
import com.nameisjayant.bottombar.socket.SocketBottomBar
import com.nameisjayant.bottombar.socket.SocketBottomBarDefaults
import com.nameisjayant.bottombar.valley.ValleyBarItem
import com.nameisjayant.bottombar.valley.ValleyBottomBar
import com.nameisjayant.bottombar.valley.ValleyBottomBarDefaults
import com.nameisjayant.composebottombar.halo.ShopIcons

/** Catalog of every bottom bar. Tapping a card opens that bar's full demo. */
@Composable
fun HomeScreen(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onBottomBarClick: (BottomBarType) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Bottom bars",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground,
                    )
                    Text(
                        "Pick one to try it out",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onBackground.copy(alpha = 0.6f),
                    )
                }
                ThemeToggle(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
        }
        items(BottomBarType.entries) { type ->
            BottomBarCard(type = type, darkTheme = darkTheme, onClick = dropUnlessResumed { onBottomBarClick(type) })
        }
        item { Spacer(Modifier.navigationBarsPadding()) }
    }
}

@Composable
private fun ThemeToggle(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    IconButton(onClick = { onDarkThemeChange(!darkTheme) }) {
        AnimatedContent(
            targetState = darkTheme,
            transitionSpec = { (fadeIn() + scaleIn(initialScale = 0.6f)) togetherWith fadeOut() },
            label = "themeIcon",
        ) { dark ->
            Icon(
                imageVector = if (dark) ThemeIcons.Sun else ThemeIcons.Moon,
                contentDescription = if (dark) "Switch to light theme" else "Switch to dark theme",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun BottomBarCard(type: BottomBarType, darkTheme: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(type.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = colors.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(type.description, style = MaterialTheme.typography.bodyMedium, color = colors.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(16.dp))
        // Live preview of the bar; tapping it opens the demo just like the rest of the card.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.background)
                .padding(top = 12.dp),
        ) {
            BottomBarPreview(type = type, darkTheme = darkTheme, onClick = onClick)
        }
    }
}

@Composable
private fun BottomBarPreview(type: BottomBarType, darkTheme: Boolean, onClick: () -> Unit) {
    when (type) {
        BottomBarType.Halo -> HaloBottomBar(
            items = HaloPreviewItems,
            selectedIndex = 2,
            onItemClick = { onClick() },
            colors = HaloBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Dome -> DomeBottomBar(
            items = DomePreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            colors = DomeBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Cradle -> CradleBottomBar(
            items = CradlePreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = CradleBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Valley -> ValleyBottomBar(
            items = ValleyPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = ValleyBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Socket -> SocketBottomBar(
            items = SocketPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = SocketBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Gable -> GableBottomBar(
            items = GablePreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = GableBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Diamond -> DiamondBottomBar(
            items = DiamondPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = DiamondBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
    }
}

private val HaloPreviewItems = listOf(
    HaloBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
    HaloBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
    HaloBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
    HaloBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
    HaloBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
)

private val DomePreviewItems = HaloPreviewItems.map { DomeBarItem(it.label, it.icon, it.selectedIcon) }

private val CradlePreviewItems = HaloPreviewItems.map { CradleBarItem(it.label, it.icon, it.selectedIcon) }

private val ValleyPreviewItems = HaloPreviewItems.map { ValleyBarItem(it.label, it.icon, it.selectedIcon) }

// Socket has no center orb, so it skips Store and shows the four tabs it was designed with.
private val SocketPreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { SocketBarItem(it.label, it.icon, it.selectedIcon) }

private val GablePreviewItems = HaloPreviewItems.map { GableBarItem(it.label, it.icon, it.selectedIcon) }

private val DiamondPreviewItems = HaloPreviewItems.map { DiamondBarItem(it.label, it.icon, it.selectedIcon) }
