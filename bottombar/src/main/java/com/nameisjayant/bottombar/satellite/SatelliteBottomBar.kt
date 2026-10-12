package com.nameisjayant.bottombar.satellite

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * A destination shown in a [SatelliteBottomBar].
 *
 * @param label read out for accessibility; the bar itself only shows icons.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown inside the orb while the item is selected, usually the filled twin of
 * [icon].
 */
@Immutable
public class SatelliteBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [SatelliteBottomBar]. See [SatelliteBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class SatelliteBottomBarColors(
    public val containerColor: Color,
    public val indicatorColor: Color,
    public val satelliteColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
)

public object SatelliteBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 64.dp

    /** Diameter of the orb behind the selected icon. */
    public val OrbSize: Dp = 40.dp

    /** Diameter of the small satellite dot next to the orb. */
    public val SatelliteSize: Dp = 7.dp

    /** Gap between the orb's edge and the satellite's center. */
    public val SatelliteGap: Dp = 7.dp

    /** Space between the bar's ends and the outermost tab slots. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 12.dp)

    /** Size of the outlined icons of the unselected tabs. */
    public val IconSize: Dp = 24.dp

    /** Size of the filled icon inside the orb. */
    public val SelectedIconSize: Dp = 18.dp

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): SatelliteBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        indicatorColor: Color = Color(0xFF5B3CE0),
        satelliteColor: Color = indicatorColor,
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
    ): SatelliteBottomBarColors = SatelliteBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        satelliteColor = satelliteColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        indicatorColor: Color = Color(0xFF5B3CE0),
        satelliteColor: Color = indicatorColor,
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
    ): SatelliteBottomBarColors = SatelliteBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        satelliteColor = satelliteColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )
}

/**
 * A flat bar of icon-only tabs. The selected icon fills in inside an accent orb, with a small satellite
 * dot parked just off the orb's top-end side.
 *
 * When the selection changes the orb glides to the new tab and the satellite swings once around it on
 * the way, coming to rest at the top-end again as the orb lands. Icons fade between their outlined and
 * filled forms as the orb passes over them. Every tab can be selected and every slot gets the same
 * width, so 3 to 5 items look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar.
 */
@Composable
public fun SatelliteBottomBar(
    items: List<SatelliteBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: SatelliteBottomBarColors = SatelliteBottomBarDefaults.colors(),
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = SatelliteBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "SatelliteBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current
    val padding = SatelliteBottomBarDefaults.ContentPadding

    // Fractional tab index the orb sits over; springs from tab to tab.
    val position = remember { Animatable(selectedIndex.toFloat()) }
    // Where the current trip started, so the satellite's orbit can follow trip progress. Only read
    // while drawing.
    var tripStart by remember { mutableFloatStateOf(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        tripStart = position.value
        position.animateTo(selectedIndex.toFloat(), OrbSpring)
    }

    Row(
        modifier = modifier
            .shadow(shadowElevation)
            .background(colors.containerColor)
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(SatelliteBottomBarDefaults.BarHeight)
            .drawBehind {
                val start = padding.calculateStartPadding(layoutDirection).toPx()
                val slot = (size.width - start - padding.calculateEndPadding(layoutDirection).toPx()) / items.size
                val fromStart = start + slot * (position.value + 0.5f)
                val ltr = layoutDirection == LayoutDirection.Ltr
                val center = Offset(if (ltr) fromStart else size.width - fromStart, size.height / 2f)
                val orbRadius = SatelliteBottomBarDefaults.OrbSize.toPx() / 2f
                drawCircle(colors.indicatorColor, orbRadius, center)

                // One full turn per trip, in the direction of travel, starting and ending at the rest angle.
                val distance = position.targetValue - tripStart
                val progress = if (distance == 0f) 1f else ((position.value - tripStart) / distance).coerceIn(0f, 1f)
                val travel = if (distance >= 0f == ltr) 1f else -1f
                val angle = RestAngle + travel * 2f * PI.toFloat() * progress
                val orbit = orbRadius + SatelliteBottomBarDefaults.SatelliteGap.toPx()
                // Mirrored in RTL, so the rest spot stays on the top-end side.
                val dx = orbit * cos(angle) * (if (ltr) 1f else -1f)
                drawCircle(
                    color = colors.satelliteColor,
                    radius = SatelliteBottomBarDefaults.SatelliteSize.toPx() / 2f,
                    center = Offset(center.x + dx, center.y + orbit * sin(angle)),
                )
            }
            .padding(padding)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            SatelliteTab(
                item = item,
                selected = index == selectedIndex,
                coverage = { (1f - abs(position.value - index)).coerceIn(0f, 1f) },
                colors = colors,
                onClick = { onItemClick(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * @param coverage how much of this tab the orb covers, from 0 (elsewhere) to 1 (right on it). Read
 * only in layer blocks, so the orb's glide doesn't recompose the tab.
 */
@Composable
private fun SatelliteTab(
    item: SatelliteBarItem,
    selected: Boolean,
    coverage: () -> Float,
    colors: SatelliteBottomBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabPress",
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics { contentDescription = item.label }
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            imageVector = item.icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(colors.unselectedContentColor),
            modifier = Modifier
                .size(SatelliteBottomBarDefaults.IconSize)
                .graphicsLayer { alpha = 1f - coverage() },
        )
        Image(
            imageVector = item.selectedIcon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(colors.selectedContentColor),
            modifier = Modifier
                .size(SatelliteBottomBarDefaults.SelectedIconSize)
                .graphicsLayer {
                    val cover = coverage()
                    alpha = cover
                    // Swells up from a little smaller as the orb settles under it.
                    val scale = 0.7f + 0.3f * cover
                    scaleX = scale
                    scaleY = scale
                },
        )
    }
}

/** Where the satellite parks: up and towards the end side, 45° off the top of the orb. */
private const val RestAngle = -PI.toFloat() / 4f

private val OrbSpring = spring<Float>(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
