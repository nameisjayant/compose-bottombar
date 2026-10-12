package com.nameisjayant.bottombar.collar

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
 * A destination shown in a [CollarBottomBar].
 *
 * @param label text under the icon; for the center orb it is only used for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class CollarBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [CollarBottomBar]. See [CollarBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class CollarBottomBarColors(
    public val containerColor: Color,
    public val collarColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
    public val orbColor: Color,
    public val orbRingColor: Color,
    public val orbContentColor: Color,
    public val cometColor: Color,
)

public object CollarBottomBarDefaults {
    /** Height of the bar body, excluding the collar overhang and the navigation bar inset. */
    public val BarHeight: Dp = 64.dp

    /** Corner radius of the two top corners of the bar body. */
    public val TopCornerRadius: Dp = 28.dp

    /** Outer diameter of the collar: the disk around the orb that rises out of the bar. */
    public val CollarSize: Dp = 78.dp

    /** How far the collar rises above the top edge of the bar body. */
    public val CollarRise: Dp = 18.dp

    /** Radius of the concave curves that blend the collar into the top edge. */
    public val CollarFilletRadius: Dp = 10.dp

    /** Outer diameter of the center orb, ring included. */
    public val OrbSize: Dp = 68.dp

    /** Thickness of the light ring around the orb. */
    public val OrbRingWidth: Dp = 6.dp

    public val IconSize: Dp = 24.dp
    public val OrbIconSize: Dp = 28.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): CollarBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        collarColor: Color = containerColor,
        selectedContentColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
        orbColor: Color = Color(0xFF5B3CE0),
        orbRingColor: Color = Color(0xFFB2C1F7),
        orbContentColor: Color = Color(0xFFFFFFFF),
        cometColor: Color = selectedContentColor,
    ): CollarBottomBarColors = CollarBottomBarColors(
        containerColor = containerColor,
        collarColor = collarColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        orbColor = orbColor,
        orbRingColor = orbRingColor,
        orbContentColor = orbContentColor,
        cometColor = cometColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF1F1F1F),
        collarColor: Color = Color(0xFF3D3D3D),
        selectedContentColor: Color = Color(0xFF4A6CF0),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        unselectedLabelColor: Color = Color(0xFFE4E6EB),
        orbColor: Color = Color(0xFF5B3CE0),
        orbRingColor: Color = Color(0xFFB2C1F7),
        orbContentColor: Color = Color(0xFFFFFFFF),
        cometColor: Color = selectedContentColor,
    ): CollarBottomBarColors = CollarBottomBarColors(
        containerColor = containerColor,
        collarColor = collarColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        orbColor = orbColor,
        orbRingColor = orbRingColor,
        orbContentColor = orbContentColor,
        cometColor = cometColor,
    )
}

/**
 * A flat bar with softly rounded top corners and a ringed orb in the middle, held in a collar that
 * rises out of the top edge.
 *
 * The middle entry of [items] becomes the orb and the rest are split evenly on either side, so [items]
 * must hold an odd number of entries (3, 5, 7...). Side tabs always show their icon and label; the
 * selected one fills in and turns the accent color. The orb is a regular destination too and swaps to
 * its [CollarBarItem.selectedIcon] when active, while a bright comet sweeps once around its ring.
 *
 * @param items destinations in display order; the middle one is drawn as the orb.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The collar makes the outline concave,
 * which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun CollarBottomBar(
    items: List<CollarBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: CollarBottomBarColors = CollarBottomBarDefaults.colors(),
    labelStyle: TextStyle = CollarBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = CollarBottomBarDefaults.windowInsets,
) {
    require(items.size >= 3 && items.size % 2 == 1) {
        "CollarBottomBar needs an odd number of items (3, 5, 7...), got ${items.size}"
    }
    val orbIndex = items.size / 2
    val shape = remember { CollarShape() }

    Box(
        modifier
            .fillMaxWidth()
            .shadow(shadowElevation, shape, clip = false)
            .background(colors.containerColor, shape)
            .windowInsetsPadding(windowInsets),
    ) {
        Row(
            modifier = Modifier
                .padding(top = CollarBottomBarDefaults.CollarRise)
                .fillMaxWidth()
                .height(CollarBottomBarDefaults.BarHeight)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEachIndexed { index, item ->
                if (index == orbIndex) {
                    // Keeps the slot under the orb so the side tabs stay evenly spaced.
                    Spacer(Modifier.weight(1f))
                } else {
                    CollarTab(
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
        CollarOrb(
            item = items[orbIndex],
            selected = orbIndex == selectedIndex,
            colors = colors,
            onClick = { onItemClick(orbIndex) },
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun CollarTab(
    item: CollarBarItem,
    selected: Boolean,
    colors: CollarBottomBarColors,
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
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.7f)) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.7f))
            },
            modifier = Modifier
                .size(CollarBottomBarDefaults.IconSize)
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
                modifier = Modifier.size(CollarBottomBarDefaults.IconSize),
            )
        }
        Spacer(Modifier.height(4.dp))
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
private fun CollarOrb(
    item: CollarBarItem,
    selected: Boolean,
    colors: CollarBottomBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "orbPress",
    )
    val bounce = rememberSelectionBounce(selected, from = 0.86f)

    // 0 -> 1 progress of the comet that sweeps once around the ring when the orb becomes selected.
    val sweep = remember { Animatable(1f) }
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        if (selected && !wasSelected) {
            sweep.snapTo(0f)
            sweep.animateTo(1f, tween(SweepDurationMillis, easing = FastOutSlowInEasing))
        } else if (!selected) {
            // Deselected before the animation finished: it was cancelled mid-way, so clear it.
            sweep.snapTo(1f)
        }
        wasSelected = selected
    }

    // The collar is a plain disk and stays still; only the ringed orb inside it reacts.
    Box(
        modifier = modifier
            .size(CollarBottomBarDefaults.CollarSize)
            .drawBehind { drawCircle(colors.collarColor) },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(CollarBottomBarDefaults.OrbSize)
                .graphicsLayer {
                    val scale = bounce.value * pressScale
                    scaleX = scale
                    scaleY = scale
                }
                .drawBehind {
                    val radius = size.minDimension / 2f
                    val ringWidth = CollarBottomBarDefaults.OrbRingWidth.toPx()
                    drawCircle(color = colors.orbRingColor, radius = radius)
                    drawCircle(color = colors.orbColor, radius = radius - ringWidth)
                    val progress = sweep.value
                    if (progress < 1f) {
                        drawComet(progress, radius - ringWidth / 2f, ringWidth, colors.cometColor)
                    }
                }
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
                    modifier = Modifier.size(CollarBottomBarDefaults.OrbIconSize),
                )
            }
        }
    }
}

/**
 * Bright arc running clockwise along the ring, from 12 o'clock back round to it. Its tail fades into
 * the ring, and the whole comet fades out over the last part of the trip.
 */
private fun DrawScope.drawComet(
    progress: Float,
    ringRadius: Float,
    ringWidth: Float,
    color: Color,
) {
    val head = 360f * progress
    val tail = minOf(head, CometLengthDegrees)
    if (tail <= 0f) return
    val alpha = if (progress < 0.75f) 1f else (1f - progress) / 0.25f
    // The gradient runs clockwise from the tail (transparent) to the head (bright).
    val brush = Brush.sweepGradient(
        0f to Color.Transparent,
        tail / 360f to color.copy(alpha = alpha),
        tail / 360f to Color.Transparent,
        center = center,
    )
    // Sweep gradients start at 3 o'clock; turn the canvas so the comet's tail sits at its 0 stop.
    rotate(degrees = head - tail - 90f) {
        drawArc(
            brush = brush,
            startAngle = 0f,
            sweepAngle = tail,
            useCenter = false,
            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
            size = Size(ringRadius * 2, ringRadius * 2),
            style = Stroke(width = ringWidth, cap = StrokeCap.Butt),
        )
    }
}

/**
 * Bar outline: a rectangle starting [CollarBottomBarDefaults.CollarRise] below the top, with rounded top
 * corners and the collar disk centered on top, blended into the edge through two concave fillets.
 */
private class CollarShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        with(density) {
            val top = CollarBottomBarDefaults.CollarRise.toPx()
            val collarR = CollarBottomBarDefaults.CollarSize.toPx() / 2f
            val filletR = CollarBottomBarDefaults.CollarFilletRadius.toPx()
            val cornerR = CollarBottomBarDefaults.TopCornerRadius.toPx()

            val cx = size.width / 2f
            val collarCy = collarR
            // Fillet circles sit on the top edge and touch the collar circle from outside.
            val dy = collarCy - (top - filletR)
            val dx = sqrt((collarR + filletR) * (collarR + filletR) - dy * dy)
            val tangent = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()

            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, top + cornerR)
                arcTo(Rect(0f, top, 2 * cornerR, top + 2 * cornerR), 180f, 90f, false)
                lineTo(cx - dx, top)
                arcTo(circle(cx - dx, top - filletR, filletR), 90f, tangent - 90f, false)
                arcTo(circle(cx, collarCy, collarR), tangent - 180f, 180f - 2 * tangent, false)
                arcTo(circle(cx + dx, top - filletR, filletR), 180f - tangent, tangent - 90f, false)
                lineTo(size.width - cornerR, top)
                arcTo(Rect(size.width - 2 * cornerR, top, size.width, top + 2 * cornerR), 270f, 90f, false)
                lineTo(size.width, size.height)
                close()
            }
            Outline.Generic(path)
        }

    private fun circle(cx: Float, cy: Float, r: Float) = Rect(cx - r, cy - r, cx + r, cy + r)
}

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

private const val TintDurationMillis = 250
private const val SweepDurationMillis = 900
private const val CometLengthDegrees = 140f
