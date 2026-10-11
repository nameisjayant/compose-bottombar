package com.nameisjayant.bottombar.wave

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * A destination shown in a [WaveBottomBar].
 *
 * @param label text under the orb.
 * @param icon icon shown inside the ring while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown inside the filled orb while the item is selected, usually the filled
 * twin of [icon].
 */
@Immutable
public class WaveBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [WaveBottomBar]. See [WaveBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class WaveBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val selectedOrbColor: Color,
    public val ringColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
)

public object WaveBottomBarDefaults {
    /** Diameter of every tab's orb. */
    public val OrbSize: Dp = 48.dp

    /** Gap between an orb and the dip in the bar that wraps around it. */
    public val OrbGap: Dp = 4.5.dp

    /** Width of the ring drawn around unselected orbs. */
    public val RingWidth: Dp = 1.5.dp

    /** Height of the bar body from the crests of the wave down, excluding the navigation bar inset. */
    public val BarHeight: Dp = 67.dp

    /** Space between the bar's sides and the outermost tab slots. */
    public val HorizontalPadding: Dp = 22.dp

    public val IconSize: Dp = 24.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): WaveBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF3F63EA),
        selectedOrbColor: Color = Color(0xFFFFFFFF),
        ringColor: Color = Color(0xFFA9B5D1),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFB2BCD4),
    ): WaveBottomBarColors = WaveBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        selectedOrbColor = selectedOrbColor,
        ringColor = ringColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF202020),
        selectedContentColor: Color = Color(0xFF4A6CF0),
        selectedOrbColor: Color = Color(0xFF2C2C2C),
        ringColor: Color = Color(0xFFB9C3DA),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        unselectedLabelColor: Color = Color(0xFFE4E8F0),
    ): WaveBottomBarColors = WaveBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        selectedOrbColor = selectedOrbColor,
        ringColor = ringColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
    )
}

/**
 * A flat-bottomed bar whose top edge rolls in a wave: every tab sits in its own dip, with an orb
 * floating in it, and the crests between them rise to the orbs' middle.
 *
 * Unselected tabs show their icon inside a thin ring; the selected tab's orb fills in with
 * [WaveBottomBarColors.selectedOrbColor], swaps to its filled icon and turns its label the selected
 * color. Every slot is the same width, so 3 or 4 items look best; with more the crests get sharper.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The wavy edge makes the outline
 * concave, which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun WaveBottomBar(
    items: List<WaveBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: WaveBottomBarColors = WaveBottomBarDefaults.colors(),
    labelStyle: TextStyle = WaveBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = WaveBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "WaveBottomBar needs at least one item" }
    val shape = remember(items.size) { WaveShape(items.size) }

    Box(
        modifier
            .fillMaxWidth()
            .shadow(shadowElevation, shape, clip = false)
            .drawBehind { drawPath(wavePath(size, this, items.size), colors.containerColor) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .height(TotalHeight)
                .padding(horizontal = WaveBottomBarDefaults.HorizontalPadding)
                .selectableGroup(),
        ) {
            items.forEachIndexed { index, item ->
                WaveTab(
                    item = item,
                    selected = index == selectedIndex,
                    colors = colors,
                    labelStyle = labelStyle,
                    onClick = { onItemClick(index) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WaveTab(
    item: WaveBarItem,
    selected: Boolean,
    colors: WaveBottomBarColors,
    labelStyle: TextStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabPress",
    )
    // 0 = ring, 1 = filled orb. Springs a little past 1 so the fill pops as it lands.
    val fill by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
        label = "orbFill",
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) colors.selectedContentColor else colors.unselectedContentColor,
        animationSpec = tween(TintDurationMillis),
        label = "tabIconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) colors.selectedContentColor else colors.unselectedLabelColor,
        animationSpec = tween(TintDurationMillis),
        label = "tabLabelColor",
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
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(WaveBottomBarDefaults.OrbSize)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .drawBehind {
                    val radius = size.minDimension / 2f
                    val ringAlpha = (1f - fill).coerceIn(0f, 1f)
                    if (ringAlpha > 0f) {
                        val ringWidth = WaveBottomBarDefaults.RingWidth.toPx()
                        drawCircle(
                            color = colors.ringColor,
                            radius = radius - ringWidth / 2f,
                            alpha = ringAlpha,
                            style = Stroke(ringWidth),
                        )
                    }
                    if (fill > 0f) {
                        drawCircle(
                            color = colors.selectedOrbColor,
                            radius = radius * fill,
                            alpha = fill.coerceAtMost(1f),
                        )
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.6f)) togetherWith
                        (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.6f))
                },
                label = "tabIcon",
            ) { isSelected ->
                Image(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(iconColor),
                    modifier = Modifier.size(WaveBottomBarDefaults.IconSize),
                )
            }
        }
        BasicText(
            text = item.label,
            style = labelStyle.copy(
                color = labelColor,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            ),
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = LabelTop),
        )
    }
}

/** Bar outline, used for the shadow; the fill itself is drawn from [wavePath] too. */
private class WaveShape(private val itemCount: Int) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(wavePath(size, density, itemCount))
}

/**
 * Bar outline. Each orb (center at `OrbSize / 2` from the top) gets a dip that follows a circle
 * [WaveBottomBarDefaults.OrbGap] wider than the orb. Between neighbouring dips, and at both ends, a
 * crest follows a convex circle whose top sits [CrestLift] above the orb centers. Each crest circle is
 * sized to touch the dip circles beside it, so the edge flows from dip to crest without a corner.
 */
private fun wavePath(size: Size, density: Density, itemCount: Int): Path = with(density) {
    val padding = WaveBottomBarDefaults.HorizontalPadding.toPx()
    val slot = (size.width - 2 * padding) / itemCount
    val half = slot / 2f
    val orbY = WaveBottomBarDefaults.OrbSize.toPx() / 2f
    val dip = orbY + WaveBottomBarDefaults.OrbGap.toPx()
    val lift = CrestLift.toPx()
    val crestTop = orbY - lift
    // Tangency of a crest circle (radius r, center half a slot away and r below crestTop) with a dip
    // circle: half² + (r - lift)² = (dip + r)².
    val crest = ((half * half + lift * lift - dip * dip) / (2 * (dip + lift))).coerceAtLeast(1f)
    val crestY = crestTop + crest

    // Direction from a crest center down to the dip on its right, and from a dip up to the crest on its right.
    val toRightDip = atan2(orbY - crestY, half)
    val toRightCrest = atan2(crestY - orbY, half)

    Path().apply {
        // Left end: the first crest rounds over into the bar's left side.
        if (padding < crest) {
            moveTo(0f, crestY - sqrt(crest * crest - padding * padding))
            arc(padding, crestY, crest, atan2(-sqrt(crest * crest - padding * padding), -padding), toRightDip)
        } else {
            moveTo(0f, crestTop)
            arc(padding, crestY, crest, -HalfPi, toRightDip)
        }
        for (i in 0 until itemCount) {
            val orbX = padding + slot * (i + 0.5f)
            // Under the orb, sweeping from its lower left through the bottom to its lower right.
            arc(orbX, orbY, dip, Pi - toRightCrest, toRightCrest)
            val crestX = orbX + half
            val toLeftDip = -Pi - toRightDip
            if (i < itemCount - 1) {
                arc(crestX, crestY, crest, toLeftDip, toRightDip)
            } else if (padding < crest) {
                // Right end, mirroring the left.
                arc(crestX, crestY, crest, toLeftDip, atan2(-sqrt(crest * crest - padding * padding), padding))
            } else {
                arc(crestX, crestY, crest, toLeftDip, -HalfPi)
                lineTo(size.width, crestTop)
            }
        }
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
}

/** Appends a circular arc from [from] to [to] (radians, y pointing down), joined to the current point. */
private fun Path.arc(cx: Float, cy: Float, radius: Float, from: Float, to: Float) {
    arcTo(
        rect = Rect(cx - radius, cy - radius, cx + radius, cy + radius),
        startAngleDegrees = Math.toDegrees(from.toDouble()).toFloat(),
        sweepAngleDegrees = Math.toDegrees((to - from).toDouble()).toFloat(),
        forceMoveTo = false,
    )
}

/** How far the crests of the wave rise above the orb centers. */
private val CrestLift = 2.dp

/** Height of the bar's content: from the top of the orbs to the bottom of the bar body. */
private val TotalHeight = WaveBottomBarDefaults.OrbSize / 2 - CrestLift + WaveBottomBarDefaults.BarHeight

/** Space from the top of the orbs down to the labels. */
private val LabelTop = 63.dp

private const val Pi = Math.PI.toFloat()
private const val HalfPi = Pi / 2f
private const val TintDurationMillis = 250
