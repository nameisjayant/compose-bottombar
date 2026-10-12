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
import com.nameisjayant.bottombar.bead.BeadBarItem
import com.nameisjayant.bottombar.bead.BeadBottomBar
import com.nameisjayant.bottombar.bead.BeadBottomBarDefaults
import com.nameisjayant.bottombar.bloom.BloomBarItem
import com.nameisjayant.bottombar.bloom.BloomBottomBar
import com.nameisjayant.bottombar.bloom.BloomBottomBarDefaults
import com.nameisjayant.bottombar.buoy.BuoyBarItem
import com.nameisjayant.bottombar.buoy.BuoyBottomBar
import com.nameisjayant.bottombar.buoy.BuoyBottomBarDefaults
import com.nameisjayant.bottombar.collar.CollarBarItem
import com.nameisjayant.bottombar.collar.CollarBottomBar
import com.nameisjayant.bottombar.collar.CollarBottomBarDefaults
import com.nameisjayant.bottombar.cradle.CradleBarItem
import com.nameisjayant.bottombar.cradle.CradleBottomBar
import com.nameisjayant.bottombar.cradle.CradleBottomBarDefaults
import com.nameisjayant.bottombar.crest.CrestBarItem
import com.nameisjayant.bottombar.crest.CrestBottomBar
import com.nameisjayant.bottombar.crest.CrestBottomBarDefaults
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
import com.nameisjayant.bottombar.orbit.OrbitBarItem
import com.nameisjayant.bottombar.orbit.OrbitBottomBar
import com.nameisjayant.bottombar.orbit.OrbitBottomBarDefaults
import com.nameisjayant.bottombar.peak.PeakBarItem
import com.nameisjayant.bottombar.peak.PeakBottomBar
import com.nameisjayant.bottombar.peak.PeakBottomBarDefaults
import com.nameisjayant.bottombar.rail.RailBarItem
import com.nameisjayant.bottombar.rail.RailBottomBar
import com.nameisjayant.bottombar.rail.RailBottomBarDefaults
import com.nameisjayant.bottombar.scribble.ScribbleBarItem
import com.nameisjayant.bottombar.scribble.ScribbleBottomBar
import com.nameisjayant.bottombar.scribble.ScribbleBottomBarDefaults
import com.nameisjayant.bottombar.socket.SocketBarItem
import com.nameisjayant.bottombar.socket.SocketBottomBar
import com.nameisjayant.bottombar.socket.SocketBottomBarDefaults
import com.nameisjayant.bottombar.valley.ValleyBarItem
import com.nameisjayant.bottombar.valley.ValleyBottomBar
import com.nameisjayant.bottombar.valley.ValleyBottomBarDefaults
import com.nameisjayant.bottombar.wave.WaveBarItem
import com.nameisjayant.bottombar.wave.WaveBottomBar
import com.nameisjayant.bottombar.wave.WaveBottomBarDefaults
import com.nameisjayant.bottombar.teardrop.TeardropBarItem
import com.nameisjayant.bottombar.teardrop.TeardropBottomBar
import com.nameisjayant.bottombar.teardrop.TeardropBottomBarDefaults
import com.nameisjayant.bottombar.tile.TileBarItem
import com.nameisjayant.bottombar.tile.TileBottomBar
import com.nameisjayant.bottombar.tile.TileBottomBarDefaults
import com.nameisjayant.bottombar.arch.ArchBarItem
import com.nameisjayant.bottombar.arch.ArchBottomBar
import com.nameisjayant.bottombar.arch.ArchBottomBarDefaults
import com.nameisjayant.bottombar.satellite.SatelliteBarItem
import com.nameisjayant.bottombar.satellite.SatelliteBottomBar
import com.nameisjayant.bottombar.satellite.SatelliteBottomBarDefaults
import com.nameisjayant.bottombar.spotlight.SpotlightBarItem
import com.nameisjayant.bottombar.spotlight.SpotlightBottomBar
import com.nameisjayant.bottombar.spotlight.SpotlightBottomBarDefaults
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
        BottomBarType.Wave -> WaveBottomBar(
            items = WavePreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = WaveBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Rail -> RailBottomBar(
            items = RailPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = RailBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Crest -> CrestBottomBar(
            items = CrestPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            colors = CrestBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Scribble -> ScribbleBottomBar(
            items = ScribblePreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = ScribbleBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Bead -> BeadBottomBar(
            items = BeadPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = BeadBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Peak -> PeakBottomBar(
            items = PeakPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            colors = PeakBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Teardrop -> TeardropBottomBar(
            items = TeardropPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = TeardropBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Orbit -> OrbitBottomBar(
            items = OrbitPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            colors = OrbitBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Tile -> TileBottomBar(
            items = TilePreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = TileBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Arch -> ArchBottomBar(
            items = ArchPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = ArchBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Satellite -> SatelliteBottomBar(
            items = SatellitePreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = SatelliteBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Spotlight -> SpotlightBottomBar(
            items = SpotlightPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            colors = SpotlightBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Collar -> CollarBottomBar(
            items = CollarPreviewItems,
            selectedIndex = 2,
            onItemClick = { onClick() },
            colors = CollarBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Buoy -> BuoyBottomBar(
            items = BuoyPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            colors = BuoyBottomBarDefaults.colors(darkTheme),
            windowInsets = WindowInsets(0.dp),
        )
        BottomBarType.Bloom -> BloomBottomBar(
            items = BloomPreviewItems,
            selectedIndex = 0,
            onItemClick = { onClick() },
            colors = BloomBottomBarDefaults.colors(darkTheme),
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

// Wave gives every tab its own dip, so it uses the three tabs it was designed with.
private val WavePreviewItems = HaloPreviewItems
    .filter { it.label in setOf("Home", "Cart", "Profile") }
    .map { WaveBarItem(it.label, it.icon, it.selectedIcon) }

// Rail widens the selected tab to fit its label, so it skips Store and shows the four tabs it was designed with.
private val RailPreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { RailBarItem(it.label, it.icon, it.selectedIcon) }

private val CrestPreviewItems = HaloPreviewItems.map { CrestBarItem(it.label, it.icon, it.selectedIcon) }

// Scribble was designed with four outline-only tabs, so it skips Store and the filled icons.
private val ScribblePreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { ScribbleBarItem(it.label, it.icon) }

private val BeadPreviewItems = HaloPreviewItems.map { BeadBarItem(it.label, it.icon, it.selectedIcon) }

// Peak shows the selected label under its icon, so it skips Store and shows the four tabs it was designed with.
private val PeakPreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { PeakBarItem(it.label, it.icon, it.selectedIcon) }

// Teardrop was designed with four tabs, so it skips Store.
private val TeardropPreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { TeardropBarItem(it.label, it.icon, it.selectedIcon) }

private val OrbitPreviewItems = HaloPreviewItems.map { OrbitBarItem(it.label, it.icon, it.selectedIcon) }

private val TilePreviewItems = HaloPreviewItems.map { TileBarItem(it.label, it.icon, it.selectedIcon) }

private val ArchPreviewItems = HaloPreviewItems.map { ArchBarItem(it.label, it.icon, it.selectedIcon) }

// Satellite was designed with four tabs, so it skips Store.
private val SatellitePreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { SatelliteBarItem(it.label, it.icon, it.selectedIcon) }

// Spotlight shows the selected label under its icon, so it uses the four tabs it was designed with.
private val SpotlightPreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { SpotlightBarItem(it.label, it.icon, it.selectedIcon) }

private val CollarPreviewItems = HaloPreviewItems.map { CollarBarItem(it.label, it.icon, it.selectedIcon) }

// Buoy shows the selected label under its icon, so it uses the four tabs it was designed with.
private val BuoyPreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { BuoyBarItem(it.label, it.icon, it.selectedIcon) }

// Bloom shows the selected label beside its orb, so it uses the four tabs it was designed with.
private val BloomPreviewItems = HaloPreviewItems
    .filter { it.label != "Store" }
    .map { BloomBarItem(it.label, it.icon, it.selectedIcon) }
