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
import com.nameisjayant.bottombar.wave.WaveBarItem
import com.nameisjayant.bottombar.wave.WaveBottomBar
import com.nameisjayant.bottombar.wave.WaveBottomBarDefaults
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
