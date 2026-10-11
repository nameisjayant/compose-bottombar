package com.nameisjayant.bottombar.crest

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * A destination shown in a [CrestBottomBar].
 *
 * @param label text under the icon; for the center orb it is only used for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class CrestBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [CrestBottomBar]. See [CrestBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class CrestBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
    public val orbColor: Color,
    public val orbContentColor: Color,
)

public object CrestBottomBarDefaults {
    /** Height of the bar body at its outer ends, excluding the wings' rise, the orb overhang and the navigation bar inset. */
    public val BarHeight: Dp = 68.dp

    /** How far each wing's top edge climbs from the bar's outer end towards the center. */
    public val WingRise: Dp = 20.dp

    /** Diameter of the center orb. */
    public val OrbSize: Dp = 54.dp

    /** How far the orb rises above the point where the two wings would meet. */
    public val OrbOverhang: Dp = 6.dp

    /** Width of the see-through ring between the orb and the socket cut around it. */
    public val SocketGap: Dp = 6.dp

    /** Radius of the rounded shoulders where the wings drop into the socket. */
    public val SocketShoulderRadius: Dp = 8.dp

    /** Radius of the corners at the outer ends of the wings. */
    public val TopCornerRadius: Dp = 12.dp

    /** Radius of the bar's bottom corners. */
    public val BottomCornerRadius: Dp = 16.dp

    /** Elevation of the colored glow under the orb. */
    public val OrbElevation: Dp = 10.dp

    /** Space between the bar's sides and the outermost tabs. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 6.dp)

    public val IconSize: Dp = 24.dp
    public val OrbIconSize: Dp = 24.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): CrestBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
        orbColor: Color = Color(0xFF5B3CE0),
        orbContentColor: Color = Color(0xFFFFFFFF),
    ): CrestBottomBarColors = CrestBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        orbColor = orbColor,
        orbContentColor = orbContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        selectedContentColor: Color = Color(0xFF4A6CF0),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        unselectedLabelColor: Color = Color(0xFFE4E8F0),
        orbColor: Color = Color(0xFF5B3CE0),
        orbContentColor: Color = Color(0xFFFFFFFF),
    ): CrestBottomBarColors = CrestBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        orbColor = orbColor,
        orbContentColor = orbContentColor,
    )
}

/**
 * A floating bar split into two wings whose top edges climb from the outer ends towards the center,
 * where a glowing orb rests in a round socket cut between them, with a thin see-through ring around it.
 *
 * The middle entry of [items] becomes the orb and the rest are split evenly on either side, so [items]
 * must hold an odd number of entries (3, 5, 7...). Side tabs always show their icon and label; the
 * selected one switches to its filled icon and takes the selected color. The orb is a regular
 * destination too: when it becomes selected it pops up out of the socket while the wings dip under it,
 * then both spring back.
 *
 * @param items destinations in display order; the middle one is drawn as the orb.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The socket makes the outline concave,
 * which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun CrestBottomBar(
    items: List<CrestBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: CrestBottomBarColors = CrestBottomBarDefaults.colors(),
    labelStyle: TextStyle = CrestBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = CrestBottomBarDefaults.windowInsets,
) {
    require(items.size >= 3 && items.size % 2 == 1) {
        "CrestBottomBar needs an odd number of items (3, 5, 7...), got ${items.size}"
    }
    val orbIndex = items.size / 2
    val orbSelected = orbIndex == selectedIndex

    // 0 -> 1 -> 0 while the orb pops up and the wings dip on selection.
    val flex = remember { Animatable(0f) }
    var wasOrbSelected by remember { mutableStateOf(orbSelected) }
    LaunchedEffect(orbSelected) {
        if (orbSelected && !wasOrbSelected) {
            flex.animateTo(1f, tween(FlexOutMillis, easing = FastOutSlowInEasing))
            flex.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            )
        } else if (!orbSelected && flex.value != 0f) {
            // Deselected mid-pop: the animation above was cancelled, so settle back.
            flex.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
        }
        wasOrbSelected = orbSelected
    }

    Box(
        modifier
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(CrestBottomBarDefaults.OrbOverhang + CrestBottomBarDefaults.WingRise + CrestBottomBarDefaults.BarHeight)
            .graphicsLayer {
                // Set here rather than with Modifier.shadow so the outline follows the wings as they dip.
                this.shadowElevation = shadowElevation.toPx()
                shape = CrestShape(WingDip.toPx() * flex.value)
            }
            .drawBehind {
                // Drawn by hand rather than with background(shape) so the wings can follow the orb's pop
                // without recomposing the bar every frame.
                drawPath(crestPath(size, this, WingDip.toPx() * flex.value), colors.containerColor)
            },
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(CrestBottomBarDefaults.BarHeight)
                .padding(CrestBottomBarDefaults.ContentPadding)
                .selectableGroup(),
        ) {
            items.forEachIndexed { index, item ->
                if (index == orbIndex) {
                    // Keeps the slot under the orb so the side tabs stay evenly spaced.
                    Spacer(Modifier.weight(1f))
                } else {
                    CrestTab(
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
        CrestOrb(
            item = items[orbIndex],
            selected = orbSelected,
            colors = colors,
            onClick = { onItemClick(orbIndex) },
            popOffset = { OrbPop.toPx() * flex.value },
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun CrestTab(
    item: CrestBarItem,
    selected: Boolean,
    colors: CrestBottomBarColors,
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
    val bounce = rememberSelectionBounce(selected, from = 0.6f)
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
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.7f)) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.7f))
            },
            modifier = Modifier
                .size(CrestBottomBarDefaults.IconSize)
                .graphicsLayer {
                    val scale = bounce.value * pressScale
                    scaleX = scale
                    scaleY = scale
                },
            label = "tabIcon",
        ) { isSelected ->
            Image(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier.size(CrestBottomBarDefaults.IconSize),
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
        )
    }
}

@Composable
private fun CrestOrb(
    item: CrestBarItem,
    selected: Boolean,
    colors: CrestBottomBarColors,
    onClick: () -> Unit,
    popOffset: Density.() -> Float,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "orbPress",
    )

    Box(
        modifier = modifier
            .size(CrestBottomBarDefaults.OrbSize)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
                translationY = -popOffset()
            }
            .shadow(
                elevation = CrestBottomBarDefaults.OrbElevation,
                shape = CircleShape,
                ambientColor = colors.orbColor,
                spotColor = colors.orbColor,
            )
            .background(colors.orbColor, CircleShape)
            .semantics { contentDescription = item.label }
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                (fadeIn(tween(240)) + scaleIn(tween(240), initialScale = 0.5f)) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 1.3f))
            },
            label = "orbIcon",
        ) { isSelected ->
            Image(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.orbContentColor),
                modifier = Modifier.size(CrestBottomBarDefaults.OrbIconSize),
            )
        }
    }
}

/** Bar outline with the wings dipped by [dip] px, used for the shadow; the fill itself is drawn from [crestPath]. */
private class CrestShape(private val dip: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(crestPath(size, density, dip))
}

/**
 * Bar outline: a body whose top edge runs in two straight slopes from [CrestBottomBarDefaults.WingRise]
 * below the wings' meeting point at either end up to that point at the top center, [dip] px lower while
 * the wings flex. A round socket concentric with the orb and [CrestBottomBarDefaults.SocketGap] wider is
 * cut out of the middle and blends into the slopes through two rounded shoulders.
 */
private fun crestPath(size: Size, density: Density, dip: Float): Path = with(density) {
    val peakY = CrestBottomBarDefaults.OrbOverhang.toPx() + dip
    val endY = CrestBottomBarDefaults.OrbOverhang.toPx() + CrestBottomBarDefaults.WingRise.toPx()
    val topR = CrestBottomBarDefaults.TopCornerRadius.toPx()
    val bottomR = CrestBottomBarDefaults.BottomCornerRadius.toPx()
    val orbR = CrestBottomBarDefaults.OrbSize.toPx() / 2f
    val socketR = orbR + CrestBottomBarDefaults.SocketGap.toPx()
    val shoulderR = CrestBottomBarDefaults.SocketShoulderRadius.toPx()

    val w = size.width
    val h = size.height
    val leftEnd = Offset(0f, endY)
    val peak = Offset(w / 2f, peakY)
    val rightEnd = Offset(w, endY)
    val leftSlope = Slope(leftEnd, peak)
    val rightSlope = Slope(peak, rightEnd)

    val body = Path().apply {
        moveTo(0f, endY + topR)
        quadraticTo(leftEnd.x, leftEnd.y, leftEnd.x + leftSlope.dir.x * topR, leftEnd.y + leftSlope.dir.y * topR)
        lineTo(peak.x, peak.y)
        lineTo(rightEnd.x - rightSlope.dir.x * topR, rightEnd.y - rightSlope.dir.y * topR)
        quadraticTo(rightEnd.x, rightEnd.y, w, endY + topR)
        lineTo(w, h - bottomR)
        arcTo(Rect(w - 2 * bottomR, h - 2 * bottomR, w, h), 0f, 90f, false)
        lineTo(bottomR, h)
        arcTo(Rect(0f, h - 2 * bottomR, 2 * bottomR, h), 90f, 90f, false)
        close()
    }

    val socket = Offset(w / 2f, orbR)
    val left = shoulder(socket, socketR, shoulderR, leftSlope, onLeft = true)
    val right = shoulder(socket, socketR, shoulderR, rightSlope, onLeft = false)
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
 * socket circle from outside and [slope] from below. The socket is centered under the point where the
 * slopes meet and always reaches above it, so the shoulder always lands on its own slope.
 */
private fun shoulder(socket: Offset, socketR: Float, r: Float, slope: Slope, onLeft: Boolean): Shoulder {
    val reach = socketR + r
    // Centers r below the slope form a parallel line; take the point on it `reach` from the socket.
    val base = slope.start - slope.normal * r
    val along = (socket - base).let { it.x * slope.dir.x + it.y * slope.dir.y }
    val foot = base + slope.dir * along
    val off = sqrt((reach * reach - (socket - foot).getDistanceSquared()).coerceAtLeast(0f))
    val center = foot + slope.dir * (if (onLeft) -off else off)
    return Shoulder(center, center + slope.normal * r)
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
 * Scale that springs from [from] back to 1 each time [selected] flips to true. It stays at 1 on first
 * composition so the initially selected tab doesn't bounce when the screen opens.
 */
@Composable
private fun rememberSelectionBounce(selected: Boolean, from: Float): Animatable<Float, *> {
    val scale = remember { Animatable(1f) }
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        if (selected && !wasSelected) {
            scale.snapTo(from)
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

/** Space from the top of the bar's outer ends down to the tab icons. */
private val TabTopPadding = 14.dp
private val IconLabelGap = 4.dp

/** How far the orb pops up out of the socket when it is selected. */
private val OrbPop = 8.dp

/** How far the wings' meeting point dips while the orb pops. */
private val WingDip = 8.dp

private const val TintDurationMillis = 250
private const val FlexOutMillis = 150
