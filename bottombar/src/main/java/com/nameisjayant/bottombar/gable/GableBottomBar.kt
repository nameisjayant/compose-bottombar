package com.nameisjayant.bottombar.gable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * A destination shown in a [GableBottomBar].
 *
 * @param label text under the icon, shown for every tab including the selected one.
 * @param icon icon shown in the bar while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown inside the bubble while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class GableBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [GableBottomBar]. See [GableBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class GableBottomBarColors(
    public val containerColor: Color,
    public val bubbleColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
)

public object GableBottomBarDefaults {
    /** Height of the bar body at its two ends, excluding the peak and the navigation bar inset. */
    public val BarHeight: Dp = 70.dp

    /** How far the peak in the middle of the top edge rises above its two ends. */
    public val PeakRise: Dp = 20.dp

    /** Diameter of the bubble that carries the selected icon. Its top lines up with the peak. */
    public val BubbleSize: Dp = 50.dp

    /** Width of the see-through ring between the bubble and the socket cut into the bar. */
    public val SocketGap: Dp = 4.dp

    /** Radius of the rounded shoulders that blend the socket into the sloped top edge. */
    public val SocketShoulderRadius: Dp = 10.dp

    /** Corner radius where the sloped top edge meets the sides. */
    public val TopCornerRadius: Dp = 10.dp

    /** Corner radius of the two bottom corners. */
    public val BottomCornerRadius: Dp = 22.dp

    /** Space between the bar's ends and the outermost tabs. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 32.dp)

    public val IconSize: Dp = 24.dp
    public val BubbleIconSize: Dp = 24.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): GableBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        bubbleColor: Color = containerColor,
        selectedContentColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
    ): GableBottomBarColors = GableBottomBarColors(
        containerColor = containerColor,
        bubbleColor = bubbleColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        bubbleColor: Color = containerColor,
        selectedContentColor: Color = Color(0xFF4A6CF0),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        unselectedLabelColor: Color = Color(0xFFE4E8F0),
    ): GableBottomBarColors = GableBottomBarColors(
        containerColor = containerColor,
        bubbleColor = bubbleColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
    )
}

/**
 * A floating bar whose top edge rises like a gable roof to a peak in the middle. The selected icon
 * lifts out of the bar into a bubble that sits in a socket cut into the sloped edge, with a thin
 * see-through ring between the two.
 *
 * Every tab shows its label; unselected tabs also show their icon. When the selection changes, the
 * bubble and its socket glide along the roof line to the new tab, the tab's icon rises into the
 * bubble as its filled twin, and the label takes the selected color. Any number of items works, though 3 to 5 fit best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The socket makes the outline concave,
 * which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun GableBottomBar(
    items: List<GableBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: GableBottomBarColors = GableBottomBarDefaults.colors(),
    labelStyle: TextStyle = GableBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = GableBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "GableBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current
    val padding = GableBottomBarDefaults.ContentPadding
    val startPadding = padding.calculateStartPadding(layoutDirection)
    val endPadding = padding.calculateEndPadding(layoutDirection)

    // Fractional tab index the bubble and its socket sit over; springs from tab to tab.
    val position by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "gablePosition",
    )
    // Center of the bubble from the bar's left edge, for a bar [width] px wide.
    val bubbleCenterX: Density.(width: Float) -> Float = { width ->
        val start = startPadding.toPx()
        val slot = (width - start - endPadding.toPx()) / items.size
        val fromStart = start + slot * (position + 0.5f)
        if (layoutDirection == LayoutDirection.Ltr) fromStart else width - fromStart
    }
    var barWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(GableBottomBarDefaults.PeakRise + GableBottomBarDefaults.BarHeight)
            .onSizeChanged { barWidth = it.width }
            .graphicsLayer {
                // Set here rather than with Modifier.shadow so the outline follows the socket as it moves.
                this.shadowElevation = shadowElevation.toPx()
                shape = GableShape(bubbleCenterX(size.width))
            }
            .drawBehind {
                // Drawn by hand rather than with background(shape) so the socket can follow the bubble
                // without recomposing the bar every frame.
                drawPath(gablePath(size, this, bubbleCenterX(size.width)), colors.containerColor)
            },
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(GableBottomBarDefaults.BarHeight)
                .padding(padding)
                .selectableGroup(),
        ) {
            items.forEachIndexed { index, item ->
                GableTab(
                    item = item,
                    selected = index == selectedIndex,
                    colors = colors,
                    labelStyle = labelStyle,
                    onClick = { onItemClick(index) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        GableBubble(
            items = items,
            selectedIndex = selectedIndex,
            colors = colors,
            onClick = { onItemClick(selectedIndex) },
            // bubbleCenterX already mirrors for RTL, so place the bubble in absolute coordinates.
            modifier = Modifier
                .align(AbsoluteAlignment.TopLeft)
                .absoluteOffset {
                    val half = GableBottomBarDefaults.BubbleSize.toPx() / 2f
                    IntOffset((bubbleCenterX(barWidth.toFloat()) - half).roundToInt(), 0)
                },
        )
    }
}

@Composable
private fun GableTab(
    item: GableBarItem,
    selected: Boolean,
    colors: GableBottomBarColors,
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
    // 1 while the icon sits in the bar, 0 once it has lifted into the bubble.
    val iconPresence by animateFloatAsState(
        targetValue = if (selected) 0f else 1f,
        animationSpec = if (selected) tween(IconLiftMillis) else spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tabIconPresence",
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
        Image(
            imageVector = item.icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(colors.unselectedContentColor),
            modifier = Modifier
                .size(GableBottomBarDefaults.IconSize)
                .graphicsLayer {
                    val scale = (0.5f + 0.5f * iconPresence) * pressScale
                    scaleX = scale
                    scaleY = scale
                    alpha = iconPresence.coerceIn(0f, 1f)
                    translationY = -IconLiftDistance.toPx() * (1f - iconPresence)
                },
        )
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

@Composable
private fun GableBubble(
    items: List<GableBarItem>,
    selectedIndex: Int,
    colors: GableBottomBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "bubblePress",
    )
    val bounce = rememberSelectionBounce(selectedIndex)

    Box(
        modifier = modifier
            .size(GableBottomBarDefaults.BubbleSize)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .background(colors.bubbleColor, CircleShape)
            // The selected tab underneath already carries the semantics; this is only a bigger target.
            .clearAndSetSemantics {}
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = {
                (fadeIn(tween(220, delayMillis = 60)) +
                    scaleIn(tween(260, delayMillis = 60), initialScale = 0.4f) +
                    slideInVertically(tween(260, delayMillis = 60)) { it / 2 }) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.4f))
            },
            modifier = Modifier.graphicsLayer {
                scaleX = bounce.value
                scaleY = bounce.value
            },
            label = "bubbleIcon",
        ) { index ->
            val item = items.getOrNull(index)
            if (item != null) {
                Image(
                    imageVector = item.selectedIcon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colors.selectedContentColor),
                    modifier = Modifier.size(GableBottomBarDefaults.BubbleIconSize),
                )
            } else {
                Spacer(Modifier.size(GableBottomBarDefaults.BubbleIconSize))
            }
        }
    }
}

/** Bar outline with the socket at [cx], used for the shadow; the fill itself is drawn from [gablePath]. */
private class GableShape(private val cx: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(gablePath(size, density, cx))
}

/**
 * Bar outline: a body whose top edge runs in two straight slopes from [GableBottomBarDefaults.PeakRise]
 * below the top at either end up to a peak at the top center, with a round socket cut into it around
 * the bubble centered at [cx]. The socket is concentric with the bubble and
 * [GableBottomBarDefaults.SocketGap] wider, and blends into the slopes through two rounded shoulders.
 */
private fun gablePath(size: Size, density: Density, cx: Float): Path = with(density) {
    val rise = GableBottomBarDefaults.PeakRise.toPx()
    val topR = GableBottomBarDefaults.TopCornerRadius.toPx()
    val bottomR = GableBottomBarDefaults.BottomCornerRadius.toPx()
    val bubbleR = GableBottomBarDefaults.BubbleSize.toPx() / 2f
    val socketR = bubbleR + GableBottomBarDefaults.SocketGap.toPx()
    val shoulderR = GableBottomBarDefaults.SocketShoulderRadius.toPx()

    val w = size.width
    val h = size.height
    val leftEnd = Offset(0f, rise)
    val peak = Offset(w / 2f, 0f)
    val rightEnd = Offset(w, rise)
    val leftSlope = Slope(leftEnd, peak)
    val rightSlope = Slope(peak, rightEnd)

    val body = Path().apply {
        moveTo(0f, rise + topR)
        quadraticTo(leftEnd.x, leftEnd.y, leftEnd.x + leftSlope.dir.x * topR, leftEnd.y + leftSlope.dir.y * topR)
        lineTo(peak.x, peak.y)
        lineTo(rightEnd.x - rightSlope.dir.x * topR, rightEnd.y - rightSlope.dir.y * topR)
        quadraticTo(rightEnd.x, rightEnd.y, w, rise + topR)
        lineTo(w, h - bottomR)
        arcTo(Rect(w - 2 * bottomR, h - 2 * bottomR, w, h), 0f, 90f, false)
        lineTo(bottomR, h)
        arcTo(Rect(0f, h - 2 * bottomR, 2 * bottomR, h), 90f, 90f, false)
        close()
    }

    val socket = Offset(cx, bubbleR)
    val left = shoulder(socket, socketR, shoulderR, preferred = leftSlope, other = rightSlope, peak, onLeft = true)
    val right = shoulder(socket, socketR, shoulderR, preferred = rightSlope, other = leftSlope, peak, onLeft = false)
    // Where each shoulder meets the socket circle.
    val leftTouch = socket + (left.center - socket) * (socketR / (socketR + shoulderR))
    val rightTouch = socket + (right.center - socket) * (socketR / (socketR + shoulderR))

    // Everything above the socket's edge; cutting it out of the body leaves the socket behind.
    val cutter = Path().apply {
        moveTo(left.tangent.x, -1f)
        lineTo(left.tangent.x, left.tangent.y)
        arcBetween(left.center, shoulderR, left.tangent, leftTouch, clockwise = true)
        arcBetween(socket, socketR, leftTouch, rightTouch, clockwise = false)
        arcBetween(right.center, shoulderR, rightTouch, right.tangent, clockwise = true)
        lineTo(right.tangent.x, -1f)
        close()
    }
    Path().apply { op(body, cutter, PathOperation.Difference) }
}

/** One straight run of the top edge, from [start] to [end], with [dir] its unit direction. */
private class Slope(val start: Offset, val end: Offset) {
    val dir: Offset = (end - start).let { it / it.getDistance() }

    /** Unit normal pointing out of the bar, i.e. up. */
    val normal: Offset = Offset(dir.y, -dir.x)
}

/** A shoulder circle centered at [center], meeting the top edge at [tangent]. */
private class Shoulder(val center: Offset, val tangent: Offset)

/**
 * The shoulder on one side of the socket: a circle of radius [r] inside the bar that touches the
 * socket circle from outside and the top edge from below. It rests on [preferred] if it can, else on
 * [other], and when it would land on neither slope it sits right on the [peak] instead.
 */
private fun shoulder(
    socket: Offset,
    socketR: Float,
    r: Float,
    preferred: Slope,
    other: Slope,
    peak: Offset,
    onLeft: Boolean,
): Shoulder {
    val reach = socketR + r
    for (slope in listOf(preferred, other)) {
        // Centers r below the slope form a parallel line; take the point on it `reach` from the socket.
        val base = slope.start - slope.normal * r
        val along = (socket - base).let { it.x * slope.dir.x + it.y * slope.dir.y }
        val foot = base + slope.dir * along
        val off = reach * reach - (socket - foot).getDistanceSquared()
        if (off < 0f) continue
        val center = foot + slope.dir * (if (onLeft) -sqrt(off) else sqrt(off))
        val tangent = center + slope.normal * r
        if (tangent.x >= slope.start.x && tangent.x <= slope.end.x) return Shoulder(center, tangent)
    }
    // Circle of radius r through the peak that touches the socket: intersect circle(peak, r) with
    // circle(socket, reach) and keep the point on this shoulder's side.
    val d = (socket - peak).getDistance()
    val a = (r * r - reach * reach + d * d) / (2 * d)
    val hh = sqrt((r * r - a * a).coerceAtLeast(0f))
    val unit = (socket - peak) / d
    val mid = peak + unit * a
    val perp = Offset(-unit.y, unit.x) * hh
    val c1 = mid + perp
    val c2 = mid - perp
    val center = if ((c1.x < c2.x) == onLeft) c1 else c2
    return Shoulder(center, peak)
}

/** Arcs along the circle at [center] from [from] to [to], the short way round in the given direction. */
private fun Path.arcBetween(center: Offset, radius: Float, from: Offset, to: Offset, clockwise: Boolean) {
    val start = angleOf(from - center)
    val end = angleOf(to - center)
    val sweep = if (clockwise) (end - start).mod(360f) else -(start - end).mod(360f)
    arcTo(circle(center.x, center.y, radius), start, sweep, false)
}

private fun angleOf(v: Offset): Float = Math.toDegrees(atan2(v.y, v.x).toDouble()).toFloat()

private fun circle(cx: Float, cy: Float, r: Float) = Rect(cx - r, cy - r, cx + r, cy + r)

/**
 * Scale that dips and springs back to 1 each time [key] changes. It stays at 1 on first composition so
 * the bubble doesn't bounce when the screen opens.
 */
@Composable
private fun rememberSelectionBounce(key: Int): Animatable<Float, *> {
    val scale = remember { Animatable(1f) }
    var previous by remember { mutableStateOf(key) }
    LaunchedEffect(key) {
        if (key != previous) {
            scale.snapTo(0.82f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        }
        previous = key
    }
    return scale
}

/** Space from the top of the bar's ends down to the tab icons. */
private val TabTopPadding = 18.dp
private val IconLabelGap = 2.dp

/** How far an icon rises while it lifts out of the bar into the bubble. */
private val IconLiftDistance = 14.dp

private const val TintDurationMillis = 250
private const val IconLiftMillis = 160
