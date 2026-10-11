package com.nameisjayant.bottombar.bead

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A destination shown in a [BeadBottomBar].
 *
 * @param label text under the icon, shown for every tab including the selected one.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class BeadBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [BeadBottomBar]. See [BeadBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class BeadBottomBarColors(
    public val containerColor: Color,
    public val beadColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
)

public object BeadBottomBarDefaults {
    /** Height of the bar body, excluding the bead overhang and the navigation bar inset. */
    public val BarHeight: Dp = 66.dp

    /** Diameter of the bead resting in the notch. Its center sits on the bar's top edge. */
    public val BeadSize: Dp = 12.dp

    /** Radius of the round notch cut into the top edge around the bead. */
    public val NotchRadius: Dp = 15.dp

    /** Radius of the rounded shoulders that blend the notch into the top edge. */
    public val NotchShoulderRadius: Dp = 7.dp

    /** How high the bead hops while it travels from one tab to another. */
    public val HopHeight: Dp = 10.dp

    /** Space between the bar's ends and the outermost tabs. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 4.dp)

    public val IconSize: Dp = 26.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): BeadBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        beadColor: Color = Color(0xFF4169E9),
        selectedContentColor: Color = beadColor,
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
    ): BeadBottomBarColors = BeadBottomBarColors(
        containerColor = containerColor,
        beadColor = beadColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        beadColor: Color = Color(0xFF4A6CF0),
        selectedContentColor: Color = beadColor,
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        unselectedLabelColor: Color = Color(0xFFE4E8F0),
    ): BeadBottomBarColors = BeadBottomBarColors(
        containerColor = containerColor,
        beadColor = beadColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
    )
}

/**
 * A flat bar with a small round notch cut into its top edge over the selected tab, holding an accent
 * bead that sits on the edge line.
 *
 * Every tab shows its icon and label. When the selection changes, the notch glides over to the new
 * tab while the bead hops along with it, and the new tab fills in and takes the accent color. Any
 * number of items works, though 3 to 5 fit best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The notch makes the outline concave,
 * which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun BeadBottomBar(
    items: List<BeadBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: BeadBottomBarColors = BeadBottomBarDefaults.colors(),
    labelStyle: TextStyle = BeadBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = BeadBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "BeadBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current
    val padding = BeadBottomBarDefaults.ContentPadding
    val startPadding = padding.calculateStartPadding(layoutDirection)
    val endPadding = padding.calculateEndPadding(layoutDirection)

    // Fractional tab index the notch and bead sit over; springs from tab to tab.
    val position = remember { Animatable(selectedIndex.toFloat()) }
    // Where the current trip started, so the hop can peak halfway there. Only read while drawing.
    var tripStart by remember { mutableFloatStateOf(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        tripStart = position.value
        position.animateTo(
            targetValue = selectedIndex.toFloat(),
            animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        )
    }
    // Center of the notch from the bar's left edge, for a bar [width] px wide.
    val notchCenterX: Density.(width: Float) -> Float = { width ->
        val start = startPadding.toPx()
        val slot = (width - start - endPadding.toPx()) / items.size
        val fromStart = start + slot * (position.value + 0.5f)
        if (layoutDirection == LayoutDirection.Ltr) fromStart else width - fromStart
    }

    Box(
        modifier
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(BeadOverhang + BeadBottomBarDefaults.BarHeight)
            .graphicsLayer {
                // Set here rather than with Modifier.shadow so the outline follows the notch as it moves.
                this.shadowElevation = shadowElevation.toPx()
                shape = NotchShape(notchCenterX(size.width))
            }
            .drawBehind {
                // Drawn by hand rather than with background(shape) so the notch and bead can move
                // without recomposing the bar every frame.
                val cx = notchCenterX(size.width)
                drawPath(notchPath(size, this, cx), colors.containerColor)

                val distance = position.targetValue - tripStart
                val progress = if (distance == 0f) 1f else ((position.value - tripStart) / distance).coerceIn(0f, 1f)
                val hop = sin(PI.toFloat() * progress)
                val beadRadius = BeadBottomBarDefaults.BeadSize.toPx() / 2f
                drawCircle(
                    color = colors.beadColor,
                    // Squeezes a little at the top of the hop, like a bead in flight.
                    radius = beadRadius * (1f - 0.12f * hop),
                    center = Offset(cx, BeadOverhang.toPx() - BeadBottomBarDefaults.HopHeight.toPx() * hop),
                )
            },
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(BeadBottomBarDefaults.BarHeight)
                .padding(padding)
                .selectableGroup(),
        ) {
            items.forEachIndexed { index, item ->
                BeadTab(
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
private fun BeadTab(
    item: BeadBarItem,
    selected: Boolean,
    colors: BeadBottomBarColors,
    labelStyle: TextStyle,
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
    // 0 shows the outlined icon, 1 the filled one.
    val fill by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(TintDurationMillis),
        label = "tabFill",
    )
    val bounce = rememberSelectionBounce(selected)
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

    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(top = TabTopPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(BeadBottomBarDefaults.IconSize)
                .graphicsLayer {
                    val scale = pressScale * bounce.value
                    scaleX = scale
                    scaleY = scale
                },
        ) {
            Image(
                imageVector = item.icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier
                    .size(BeadBottomBarDefaults.IconSize)
                    .graphicsLayer { alpha = 1f - fill },
            )
            Image(
                imageVector = item.selectedIcon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier
                    .size(BeadBottomBarDefaults.IconSize)
                    .graphicsLayer { alpha = fill },
            )
        }
        Spacer(Modifier.height(IconLabelGap))
        BasicText(
            text = item.label,
            style = labelStyle.copy(
                color = labelColor,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            ),
            maxLines = 1,
            modifier = Modifier.graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        )
    }
}

/** Bar outline with the notch at [cx], used for the shadow; the fill itself is drawn from [notchPath]. */
private class NotchShape(private val cx: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(notchPath(size, density, cx))
}

/**
 * Bar outline: a rectangle starting [BeadOverhang] below the top, with a round notch cut into its top
 * edge around the bead centered at [cx]. The notch is centered on the top edge and blends into it
 * through two rounded shoulders.
 */
private fun notchPath(size: Size, density: Density, cx: Float): Path = with(density) {
    val top = BeadOverhang.toPx()
    val notchR = BeadBottomBarDefaults.NotchRadius.toPx()
    val shoulderR = BeadBottomBarDefaults.NotchShoulderRadius.toPx()

    val body = Path().apply { addRect(Rect(0f, top, size.width, size.height)) }
    // Shoulder circles hang just under the top edge and touch the notch circle from outside.
    val dy = shoulderR
    val dx = sqrt((notchR + shoulderR) * (notchR + shoulderR) - dy * dy)
    val tangent = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
    // Everything above the notch's edge; cutting it out of the body leaves the notch behind.
    val cutter = Path().apply {
        moveTo(cx - dx, 0f)
        lineTo(cx - dx, top)
        arcTo(circle(cx - dx, top + shoulderR, shoulderR), 270f, 90f - tangent, false)
        arcTo(circle(cx, top, notchR), 180f - tangent, 2 * tangent - 180f, false)
        arcTo(circle(cx + dx, top + shoulderR, shoulderR), 180f + tangent, 90f - tangent, false)
        lineTo(cx + dx, 0f)
        close()
    }
    Path().apply { op(body, cutter, PathOperation.Difference) }
}

private fun circle(cx: Float, cy: Float, r: Float) = Rect(cx - r, cy - r, cx + r, cy + r)

/**
 * Scale that dips and springs back to 1 each time [selected] flips to true. It stays at 1 on first
 * composition so the initially selected tab doesn't bounce when the screen opens.
 */
@Composable
private fun rememberSelectionBounce(selected: Boolean): Animatable<Float, *> {
    val scale = remember { Animatable(1f) }
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        if (selected && !wasSelected) {
            scale.snapTo(0.8f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        } else if (!selected && scale.value != 1f) {
            // Deselected mid-bounce: the bounce above was cancelled, so settle back instead of staying shrunk.
            scale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
        }
        wasSelected = selected
    }
    return scale
}

/** Room above the bar body for the top half of the bead. */
private val BeadOverhang: Dp
    get() = BeadBottomBarDefaults.BeadSize / 2

/** Space from the bar's top edge down to the tab icons. */
private val TabTopPadding = 18.dp
private val IconLabelGap = 3.dp

private const val TintDurationMillis = 250
