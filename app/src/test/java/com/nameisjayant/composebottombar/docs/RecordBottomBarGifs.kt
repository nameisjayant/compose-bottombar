package com.nameisjayant.composebottombar.docs

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.nameisjayant.bottombar.bead.BeadBarItem
import com.nameisjayant.bottombar.bead.BeadBottomBar
import com.nameisjayant.bottombar.bead.BeadBottomBarDefaults
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
import com.nameisjayant.composebottombar.halo.ShopIcons
import java.io.File
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Records the frames behind the README GIFs: each bar is rendered off-device on its own, in light and
 * dark, and only the bar (plus a thin strip of page above it) is captured while it walks through tabs.
 *
 * Skipped unless `-PbottomBarGifDir=<dir>` is passed; run `scripts/record-bottombar-gifs.sh` instead.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w411dp-h891dp-xxhdpi")
class RecordBottomBarGifs {

    @get:Rule
    val composeRule = createComposeRule()

    private val outputDir: File? = System.getProperty("bottomBarGifDir")?.let(::File)

    private val haloItems = listOf(
        HaloBarItem("Home", ShopIcons.Home, ShopIcons.HomeFilled),
        HaloBarItem("Search", ShopIcons.Search, ShopIcons.SearchFilled),
        HaloBarItem("Store", ShopIcons.Store, ShopIcons.StoreFilled),
        HaloBarItem("Cart", ShopIcons.Cart, ShopIcons.CartFilled),
        HaloBarItem("Profile", ShopIcons.Profile, ShopIcons.ProfileFilled),
    )

    private val domeItems = haloItems.map { DomeBarItem(it.label, it.icon, it.selectedIcon) }

    private val cradleItems = haloItems.map { CradleBarItem(it.label, it.icon, it.selectedIcon) }

    private val valleyItems = haloItems.map { ValleyBarItem(it.label, it.icon, it.selectedIcon) }

    // Socket has no center orb, so it uses the four tabs it was designed with.
    private val socketItems = haloItems
        .filter { it.label != "Store" }
        .map { SocketBarItem(it.label, it.icon, it.selectedIcon) }

    private val gableItems = haloItems.map { GableBarItem(it.label, it.icon, it.selectedIcon) }

    private val diamondItems = haloItems.map { DiamondBarItem(it.label, it.icon, it.selectedIcon) }

    // Wave gives every tab its own dip, so it uses the three tabs it was designed with.
    private val waveItems = haloItems
        .filter { it.label in setOf("Home", "Cart", "Profile") }
        .map { WaveBarItem(it.label, it.icon, it.selectedIcon) }

    // Rail widens the selected tab to fit its label, so it uses the four tabs it was designed with.
    private val railItems = haloItems
        .filter { it.label != "Store" }
        .map { RailBarItem(it.label, it.icon, it.selectedIcon) }

    private val crestItems = haloItems.map { CrestBarItem(it.label, it.icon, it.selectedIcon) }

    // Scribble was designed with four outline-only tabs, so it skips Store and the filled icons.
    private val scribbleItems = haloItems
        .filter { it.label != "Store" }
        .map { ScribbleBarItem(it.label, it.icon) }

    private val beadItems = haloItems.map { BeadBarItem(it.label, it.icon, it.selectedIcon) }

    // Peak shows the selected label under its icon, so it uses the four tabs it was designed with.
    private val peakItems = haloItems
        .filter { it.label != "Store" }
        .map { PeakBarItem(it.label, it.icon, it.selectedIcon) }

    // Teardrop was designed with four tabs, so it skips Store.
    private val teardropItems = haloItems
        .filter { it.label != "Store" }
        .map { TeardropBarItem(it.label, it.icon, it.selectedIcon) }

    @Test
    fun haloLight() = recordHalo(darkTheme = false)

    @Test
    fun haloDark() = recordHalo(darkTheme = true)

    private fun recordHalo(darkTheme: Boolean) {
        record(name = if (darkTheme) "halo-dark" else "halo-light", darkTheme = darkTheme) { selectedIndex, onItemClick ->
            HaloBottomBar(
                items = haloItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = HaloBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun domeLight() = recordDome(darkTheme = false)

    @Test
    fun domeDark() = recordDome(darkTheme = true)

    private fun recordDome(darkTheme: Boolean) {
        record(name = if (darkTheme) "dome-dark" else "dome-light", darkTheme = darkTheme) { selectedIndex, onItemClick ->
            DomeBottomBar(
                items = domeItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                colors = DomeBottomBarDefaults.colors(darkTheme),
                shadowElevation = if (darkTheme) 0.dp else 8.dp,
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun cradleLight() = recordCradle(darkTheme = false)

    @Test
    fun cradleDark() = recordCradle(darkTheme = true)

    private fun recordCradle(darkTheme: Boolean) {
        record(name = if (darkTheme) "cradle-dark" else "cradle-light", darkTheme = darkTheme) { selectedIndex, onItemClick ->
            CradleBottomBar(
                items = cradleItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = CradleBottomBarDefaults.colors(darkTheme),
                shadowElevation = if (darkTheme) 0.dp else 8.dp,
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun valleyLight() = recordValley(darkTheme = false)

    @Test
    fun valleyDark() = recordValley(darkTheme = true)

    private fun recordValley(darkTheme: Boolean) {
        record(name = if (darkTheme) "valley-dark" else "valley-light", darkTheme = darkTheme) { selectedIndex, onItemClick ->
            ValleyBottomBar(
                items = valleyItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = ValleyBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun socketLight() = recordSocket(darkTheme = false)

    @Test
    fun socketDark() = recordSocket(darkTheme = true)

    private fun recordSocket(darkTheme: Boolean) {
        record(
            name = if (darkTheme) "socket-dark" else "socket-light",
            darkTheme = darkTheme,
            tour = listOf(0, 1, 2, 3, 1, 0),
        ) { selectedIndex, onItemClick ->
            SocketBottomBar(
                items = socketItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                colors = SocketBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun gableLight() = recordGable(darkTheme = false)

    @Test
    fun gableDark() = recordGable(darkTheme = true)

    private fun recordGable(darkTheme: Boolean) {
        record(
            name = if (darkTheme) "gable-dark" else "gable-light",
            darkTheme = darkTheme,
            tour = listOf(0, 1, 2, 3, 4, 2, 0),
        ) { selectedIndex, onItemClick ->
            GableBottomBar(
                items = gableItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                colors = GableBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun diamondLight() = recordDiamond(darkTheme = false)

    @Test
    fun diamondDark() = recordDiamond(darkTheme = true)

    private fun recordDiamond(darkTheme: Boolean) {
        record(name = if (darkTheme) "diamond-dark" else "diamond-light", darkTheme = darkTheme) { selectedIndex, onItemClick ->
            DiamondBottomBar(
                items = diamondItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = DiamondBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun waveLight() = recordWave(darkTheme = false)

    @Test
    fun waveDark() = recordWave(darkTheme = true)

    private fun recordWave(darkTheme: Boolean) {
        record(
            name = if (darkTheme) "wave-dark" else "wave-light",
            darkTheme = darkTheme,
            tour = listOf(0, 1, 2, 1, 0),
        ) { selectedIndex, onItemClick ->
            WaveBottomBar(
                items = waveItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = WaveBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun railLight() = recordRail(darkTheme = false)

    @Test
    fun railDark() = recordRail(darkTheme = true)

    private fun recordRail(darkTheme: Boolean) {
        record(
            name = if (darkTheme) "rail-dark" else "rail-light",
            darkTheme = darkTheme,
            tour = listOf(0, 1, 2, 3, 0),
        ) { selectedIndex, onItemClick ->
            RailBottomBar(
                items = railItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = RailBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun crestLight() = recordCrest(darkTheme = false)

    @Test
    fun crestDark() = recordCrest(darkTheme = true)

    private fun recordCrest(darkTheme: Boolean) {
        record(
            name = if (darkTheme) "crest-dark" else "crest-light",
            darkTheme = darkTheme,
            tour = listOf(0, 1, 2, 3, 4, 2, 0),
        ) { selectedIndex, onItemClick ->
            CrestBottomBar(
                items = crestItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                // Room above the bar for the orb to pop up into.
                modifier = Modifier.padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 14.dp),
                colors = CrestBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun scribbleLight() = recordScribble(darkTheme = false)

    @Test
    fun scribbleDark() = recordScribble(darkTheme = true)

    private fun recordScribble(darkTheme: Boolean) {
        record(
            name = if (darkTheme) "scribble-dark" else "scribble-light",
            darkTheme = darkTheme,
            tour = listOf(0, 1, 2, 3, 0),
        ) { selectedIndex, onItemClick ->
            ScribbleBottomBar(
                items = scribbleItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = ScribbleBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun beadLight() = recordBead(darkTheme = false)

    @Test
    fun beadDark() = recordBead(darkTheme = true)

    private fun recordBead(darkTheme: Boolean) {
        record(name = if (darkTheme) "bead-dark" else "bead-light", darkTheme = darkTheme) { selectedIndex, onItemClick ->
            BeadBottomBar(
                items = beadItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = BeadBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun peakLight() = recordPeak(darkTheme = false)

    @Test
    fun peakDark() = recordPeak(darkTheme = true)

    private fun recordPeak(darkTheme: Boolean) {
        record(
            name = if (darkTheme) "peak-dark" else "peak-light",
            darkTheme = darkTheme,
            tour = listOf(0, 1, 2, 3, 1, 0),
        ) { selectedIndex, onItemClick ->
            PeakBottomBar(
                items = peakItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                colors = PeakBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    @Test
    fun teardropLight() = recordTeardrop(darkTheme = false)

    @Test
    fun teardropDark() = recordTeardrop(darkTheme = true)

    private fun recordTeardrop(darkTheme: Boolean) {
        record(
            name = if (darkTheme) "teardrop-dark" else "teardrop-light",
            darkTheme = darkTheme,
            tour = listOf(0, 1, 2, 3, 1, 0),
        ) { selectedIndex, onItemClick ->
            TeardropBottomBar(
                items = teardropItems,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                colors = TeardropBottomBarDefaults.colors(darkTheme),
                windowInsets = WindowInsets(0.dp),
            )
        }
    }

    private fun record(
        name: String,
        darkTheme: Boolean,
        tour: List<Int> = TOUR,
        bar: @Composable (selectedIndex: Int, onItemClick: (Int) -> Unit) -> Unit,
    ) {
        assumeTrue("Pass -PbottomBarGifDir to record GIF frames", outputDir != null)
        val frameDir = File(outputDir, name).apply {
            deleteRecursively()
            mkdirs()
        }
        var selected by mutableIntStateOf(tour.first())
        var frame = 0
        fun capture(durationMillis: Long) {
            repeat((durationMillis / FRAME_MILLIS).toInt()) {
                val bitmap = composeRule.onNodeWithTag(BAR_TAG).captureToImage().asAndroidBitmap()
                File(frameDir, "frame_%04d.png".format(frame++)).outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                composeRule.mainClock.advanceTimeBy(FRAME_MILLIS)
            }
        }

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Box(
                Modifier
                    .testTag(BAR_TAG)
                    .width(BAR_WIDTH.dp)
                    .background(if (darkTheme) DarkPage else LightPage)
                    .padding(top = 18.dp),
            ) {
                bar(selected) { selected = it }
            }
        }
        capture(HOLD_MILLIS)
        tour.drop(1).forEach { index ->
            selected = index
            capture(STEP_MILLIS)
        }
    }

    private companion object {
        const val BAR_TAG = "bottomBar"
        const val BAR_WIDTH = 380
        const val FRAME_MILLIS = 40L
        const val HOLD_MILLIS = 800L
        const val STEP_MILLIS = 960L

        /** Tabs visited in order; ends where it started so the GIF loops seamlessly. */
        val TOUR = listOf(0, 1, 2, 3, 4, 2, 0)

        val LightPage = Color(0xFFEEF1F8)
        val DarkPage = Color(0xFF0F0F12)
    }
}
