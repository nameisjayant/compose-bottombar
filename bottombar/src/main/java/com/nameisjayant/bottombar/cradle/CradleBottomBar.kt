package com.nameisjayant.bottombar.cradle

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
 * A destination shown in a [CradleBottomBar].
 *
 * @param label text under the icon; for the center orb it is only used for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class CradleBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [CradleBottomBar]. See [CradleBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class CradleBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
    public val orbColor: Color,
    public val orbContentColor: Color,
)

public object CradleBottomBarDefaults {
    /** Height of the bar body, excluding the orb overhang and the navigation bar inset. */
    public val BarHeight: Dp = 72.dp

    /** Diameter of the center orb. */
    public val OrbSize: Dp = 56.dp

    /** How far the orb's center sits above the top edge of the bar body. */
    public val OrbLift: Dp = 6.dp

    /** Space left between the orb and the notch cut into the bar around it. */
    public val NotchGap: Dp = 5.dp

    /** Radius of the rounded shoulders where the top edge bends down into the notch. */
    public val NotchShoulderRadius: Dp = 28.dp

    /** How far the orb rises above the top edge of the bar body. */
    public val OrbOverhang: Dp = OrbSize / 2 + OrbLift

    public val IconSize: Dp = 24.dp
    public val OrbIconSize: Dp = 26.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): CradleBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
        orbColor: Color = Color(0xFF5B3CE0),
        orbContentColor: Color = Color(0xFFFFFFFF),
    ): CradleBottomBarColors = CradleBottomBarColors(
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
    ): CradleBottomBarColors = CradleBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        orbColor = orbColor,
        orbContentColor = orbContentColor,
    )
}

/**
 * A flat bar whose top edge dips into a smooth notch, cradling a raised orb in the middle.
 *
 * The middle entry of [items] becomes the orb and the rest are split evenly on either side, so [items]
 * must hold an odd number of entries (3, 5, 7...). Side tabs always show their icon and label; the
 * selected one switches to its filled icon and takes the selected color. The orb is a regular
 * destination too and swaps to its [CradleBarItem.selectedIcon] when active.
 *
 * @param items destinations in display order; the middle one is drawn as the orb.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The notch makes the outline concave,
 * which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun CradleBottomBar(
    items: List<CradleBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: CradleBottomBarColors = CradleBottomBarDefaults.colors(),
    labelStyle: TextStyle = CradleBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = CradleBottomBarDefaults.windowInsets,
) {
    require(items.size >= 3 && items.size % 2 == 1) {
        "CradleBottomBar needs an odd number of items (3, 5, 7...), got ${items.size}"
    }
    val orbIndex = items.size / 2
    val shape = remember { CradleShape() }

    Box(
        modifier
            .fillMaxWidth()
            .shadow(shadowElevation, shape, clip = false)
            .background(colors.containerColor, shape),
    ) {
        Row(
            modifier = Modifier
                .padding(top = CradleBottomBarDefaults.OrbOverhang)
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .height(CradleBottomBarDefaults.BarHeight)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEachIndexed { index, item ->
                if (index == orbIndex) {
                    // Keeps the slot under the orb so the side tabs stay evenly spaced.
                    Spacer(Modifier.weight(1f))
                } else {
                    CradleTab(
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
        CradleOrb(
            item = items[orbIndex],
            selected = orbIndex == selectedIndex,
            colors = colors,
            onClick = { onItemClick(orbIndex) },
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun CradleTab(
    item: CradleBarItem,
    selected: Boolean,
    colors: CradleBottomBarColors,
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
                .size(CradleBottomBarDefaults.IconSize)
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
                modifier = Modifier.size(CradleBottomBarDefaults.IconSize),
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
private fun CradleOrb(
    item: CradleBarItem,
    selected: Boolean,
    colors: CradleBottomBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "orbPress",
    )
    val bounce = rememberSelectionBounce(selected, from = 0.82f)

    // 0 -> 1 progress of the ring that ripples out through the notch when the orb becomes selected.
    val ripple = remember { Animatable(1f) }
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        if (selected && !wasSelected) {
            ripple.snapTo(0f)
            ripple.animateTo(1f, tween(RippleDurationMillis, easing = FastOutSlowInEasing))
        } else if (!selected) {
            // Deselected before the animation finished: it was cancelled mid-way, so clear it.
            ripple.snapTo(1f)
        }
        wasSelected = selected
    }

    Box(
        modifier = modifier
            .size(CradleBottomBarDefaults.OrbSize)
            .graphicsLayer {
                val scale = bounce.value * pressScale
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                val progress = ripple.value
                if (progress < 1f) {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        color = colors.orbColor.copy(alpha = 0.6f * (1f - progress)),
                        radius = radius + radius * 0.45f * progress,
                        style = Stroke(width = 4.dp.toPx() * (1f - progress * 0.5f)),
                    )
                }
            }
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
                modifier = Modifier.size(CradleBottomBarDefaults.OrbIconSize),
            )
        }
    }
}

/**
 * Bar outline: a rectangle starting [CradleBottomBarDefaults.OrbOverhang] below the top, with a round
 * notch cut around the orb. Two convex shoulders bend the top edge down into the notch circle, which
 * is concentric with the orb and [CradleBottomBarDefaults.NotchGap] wider than it.
 */
private class CradleShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        with(density) {
            val top = CradleBottomBarDefaults.OrbOverhang.toPx()
            val notchR = (CradleBottomBarDefaults.OrbSize / 2 + CradleBottomBarDefaults.NotchGap).toPx()
            val shoulderR = CradleBottomBarDefaults.NotchShoulderRadius.toPx()

            val cx = size.width / 2f
            val notchCy = top - CradleBottomBarDefaults.OrbLift.toPx()
            // Shoulder circles hang just under the top edge and touch the notch circle from outside.
            val dy = top + shoulderR - notchCy
            val dx = sqrt((notchR + shoulderR) * (notchR + shoulderR) - dy * dy)
            val tangent = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()

            val path = Path().apply {
                moveTo(0f, top)
                lineTo(cx - dx, top)
                arcTo(circle(cx - dx, top + shoulderR, shoulderR), 270f, 90f - tangent, false)
                arcTo(circle(cx, notchCy, notchR), 180f - tangent, 2 * tangent - 180f, false)
                arcTo(circle(cx + dx, top + shoulderR, shoulderR), 180f + tangent, 90f - tangent, false)
                lineTo(size.width, top)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
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
private const val RippleDurationMillis = 520
