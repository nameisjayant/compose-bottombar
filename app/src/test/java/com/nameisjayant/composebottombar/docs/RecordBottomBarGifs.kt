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
import com.nameisjayant.bottombar.dome.DomeBarItem
import com.nameisjayant.bottombar.dome.DomeBottomBar
import com.nameisjayant.bottombar.dome.DomeBottomBarDefaults
import com.nameisjayant.bottombar.halo.HaloBarItem
import com.nameisjayant.bottombar.halo.HaloBottomBar
import com.nameisjayant.bottombar.halo.HaloBottomBarDefaults
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

    private fun record(
        name: String,
        darkTheme: Boolean,
        bar: @Composable (selectedIndex: Int, onItemClick: (Int) -> Unit) -> Unit,
    ) {
        assumeTrue("Pass -PbottomBarGifDir to record GIF frames", outputDir != null)
        val frameDir = File(outputDir, name).apply {
            deleteRecursively()
            mkdirs()
        }
        var selected by mutableIntStateOf(TOUR.first())
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
        TOUR.drop(1).forEach { index ->
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
