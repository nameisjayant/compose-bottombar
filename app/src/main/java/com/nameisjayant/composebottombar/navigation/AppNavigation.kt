package com.nameisjayant.composebottombar.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nameisjayant.composebottombar.arch.ArchBottomBarDemo
import com.nameisjayant.composebottombar.bead.BeadBottomBarDemo
import com.nameisjayant.composebottombar.bloom.BloomBottomBarDemo
import com.nameisjayant.composebottombar.buoy.BuoyBottomBarDemo
import com.nameisjayant.composebottombar.collar.CollarBottomBarDemo
import com.nameisjayant.composebottombar.cradle.CradleBottomBarDemo
import com.nameisjayant.composebottombar.crest.CrestBottomBarDemo
import com.nameisjayant.composebottombar.diamond.DiamondBottomBarDemo
import com.nameisjayant.composebottombar.dome.DomeBottomBarDemo
import com.nameisjayant.composebottombar.gable.GableBottomBarDemo
import com.nameisjayant.composebottombar.halo.HaloBottomBarDemo
import com.nameisjayant.composebottombar.home.BottomBarType
import com.nameisjayant.composebottombar.home.HomeScreen
import com.nameisjayant.composebottombar.orbit.OrbitBottomBarDemo
import com.nameisjayant.composebottombar.peak.PeakBottomBarDemo
import com.nameisjayant.composebottombar.rail.RailBottomBarDemo
import com.nameisjayant.composebottombar.satellite.SatelliteBottomBarDemo
import com.nameisjayant.composebottombar.scribble.ScribbleBottomBarDemo
import com.nameisjayant.composebottombar.socket.SocketBottomBarDemo
import com.nameisjayant.composebottombar.spotlight.SpotlightBottomBarDemo
import com.nameisjayant.composebottombar.teardrop.TeardropBottomBarDemo
import com.nameisjayant.composebottombar.tile.TileBottomBarDemo
import com.nameisjayant.composebottombar.valley.ValleyBottomBarDemo
import com.nameisjayant.composebottombar.wave.WaveBottomBarDemo
import kotlinx.serialization.Serializable

/** Top-level routes: the catalog, and one full-screen demo per bottom bar. */
@Serializable data object CatalogRoute : NavKey
@Serializable data object HaloDemoRoute : NavKey
@Serializable data object DomeDemoRoute : NavKey
@Serializable data object CradleDemoRoute : NavKey
@Serializable data object ValleyDemoRoute : NavKey
@Serializable data object SocketDemoRoute : NavKey
@Serializable data object GableDemoRoute : NavKey
@Serializable data object DiamondDemoRoute : NavKey
@Serializable data object WaveDemoRoute : NavKey
@Serializable data object RailDemoRoute : NavKey
@Serializable data object CrestDemoRoute : NavKey
@Serializable data object ScribbleDemoRoute : NavKey
@Serializable data object BeadDemoRoute : NavKey
@Serializable data object PeakDemoRoute : NavKey
@Serializable data object TeardropDemoRoute : NavKey
@Serializable data object OrbitDemoRoute : NavKey
@Serializable data object TileDemoRoute : NavKey
@Serializable data object ArchDemoRoute : NavKey
@Serializable data object SatelliteDemoRoute : NavKey
@Serializable data object SpotlightDemoRoute : NavKey
@Serializable data object CollarDemoRoute : NavKey
@Serializable data object BuoyDemoRoute : NavKey
@Serializable data object BloomDemoRoute : NavKey

private fun BottomBarType.route(): NavKey = when (this) {
    BottomBarType.Halo -> HaloDemoRoute
    BottomBarType.Dome -> DomeDemoRoute
    BottomBarType.Cradle -> CradleDemoRoute
    BottomBarType.Valley -> ValleyDemoRoute
    BottomBarType.Socket -> SocketDemoRoute
    BottomBarType.Gable -> GableDemoRoute
    BottomBarType.Diamond -> DiamondDemoRoute
    BottomBarType.Wave -> WaveDemoRoute
    BottomBarType.Rail -> RailDemoRoute
    BottomBarType.Crest -> CrestDemoRoute
    BottomBarType.Scribble -> ScribbleDemoRoute
    BottomBarType.Bead -> BeadDemoRoute
    BottomBarType.Peak -> PeakDemoRoute
    BottomBarType.Teardrop -> TeardropDemoRoute
    BottomBarType.Orbit -> OrbitDemoRoute
    BottomBarType.Tile -> TileDemoRoute
    BottomBarType.Arch -> ArchDemoRoute
    BottomBarType.Satellite -> SatelliteDemoRoute
    BottomBarType.Spotlight -> SpotlightDemoRoute
    BottomBarType.Collar -> CollarDemoRoute
    BottomBarType.Buoy -> BuoyDemoRoute
    BottomBarType.Bloom -> BloomDemoRoute
}

@Composable
fun AppNavigation(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    val backStack = rememberNavBackStack(CatalogRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        transitionSpec = {
            slideInHorizontally(tween(300)) { it / 3 } + fadeIn(tween(300)) togetherWith fadeOut(tween(150))
        },
        popTransitionSpec = {
            fadeIn(tween(300)) togetherWith slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(300))
        },
        predictivePopTransitionSpec = {
            fadeIn(tween(300)) togetherWith slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(300))
        },
        entryProvider = entryProvider {
            entry<CatalogRoute> {
                HomeScreen(
                    darkTheme = darkTheme,
                    onDarkThemeChange = onDarkThemeChange,
                    onBottomBarClick = { type -> backStack.add(type.route()) },
                )
            }
            entry<HaloDemoRoute> {
                HaloBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<DomeDemoRoute> {
                DomeBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<CradleDemoRoute> {
                CradleBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<ValleyDemoRoute> {
                ValleyBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<SocketDemoRoute> {
                SocketBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<GableDemoRoute> {
                GableBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<DiamondDemoRoute> {
                DiamondBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<WaveDemoRoute> {
                WaveBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<RailDemoRoute> {
                RailBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<CrestDemoRoute> {
                CrestBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<ScribbleDemoRoute> {
                ScribbleBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<BeadDemoRoute> {
                BeadBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<PeakDemoRoute> {
                PeakBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<TeardropDemoRoute> {
                TeardropBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<OrbitDemoRoute> {
                OrbitBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<TileDemoRoute> {
                TileBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<ArchDemoRoute> {
                ArchBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<SatelliteDemoRoute> {
                SatelliteBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<SpotlightDemoRoute> {
                SpotlightBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<CollarDemoRoute> {
                CollarBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<BuoyDemoRoute> {
                BuoyBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
            entry<BloomDemoRoute> {
                BloomBottomBarDemo(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
            }
        },
    )
}
