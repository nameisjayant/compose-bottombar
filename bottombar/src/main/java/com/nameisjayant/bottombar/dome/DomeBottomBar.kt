package com.nameisjayant.bottombar.dome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.offset
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
 * A destination shown in a [DomeBottomBar].
 *
 * @param label text shown under the icon while the item is selected; for the center orb it is only
 * used for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class DomeBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [DomeBottomBar]. See [DomeBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class DomeBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val orbColor: Color,
    public val orbContentColor: Color,
    public val orbGlowColor: Color,
)

public object DomeBottomBarDefaults {
    /** Height of the bar body, excluding the dome. */
    public val BarHeight: Dp = 66.dp

    /** How far the dome rises above the top edge of the bar body. */
    public val DomeRise: Dp = 28.dp

    /** Radius of the dome bump. The orb sits concentric inside it. */
    public val DomeRadius: Dp = 34.dp

    /** Radius of the concave curves that blend the dome into the top edge. */
    public val DomeFilletRadius: Dp = 14.dp

    /** Diameter of the center orb. */
    public val OrbSize: Dp = 50.dp

    /** Corner radius of the two top corners of the bar body. */
    public val TopCornerRadius: Dp = 8.dp

    /** Corner radius of the two bottom corners of the bar body. */
    public val BottomCornerRadius: Dp = 24.dp

    public val IconSize: Dp = 24.dp
    public val OrbIconSize: Dp = 26.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): DomeBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        orbColor: Color = Color(0xFF5B3CE0),
        orbContentColor: Color = Color(0xFFFFFFFF),
        orbGlowColor: Color = Color(0xFF5B3CE0),
    ): DomeBottomBarColors = DomeBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        orbColor = orbColor,
        orbContentColor = orbContentColor,
        orbGlowColor = orbGlowColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        selectedContentColor: Color = Color(0xFF4A6CF0),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        orbColor: Color = Color(0xFF5B3CE0),
        orbContentColor: Color = Color(0xFFFFFFFF),
        orbGlowColor: Color = Color(0xFF5B3CE0),
    ): DomeBottomBarColors = DomeBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        orbColor = orbColor,
        orbContentColor = orbContentColor,
        orbGlowColor = orbGlowColor,
    )
}

/**
 * A rounded bar whose top edge swells into a dome in the middle, cradling a glowing orb.
 *
 * The middle entry of [items] becomes the orb and the rest are split evenly on either side, so [items]
 * must hold an odd number of entries (3, 5, 7...). Side tabs show only their icon until selected; the
 * selected tab switches to its filled icon and its label slides in underneath. The orb is a regular
 * destination too and swaps to its [DomeBarItem.selectedIcon] when active.
 *
 * @param items destinations in display order; the middle one is drawn as the orb.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The dome makes the outline concave,
 * which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun DomeBottomBar(
    items: List<DomeBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: DomeBottomBarColors = DomeBottomBarDefaults.colors(),
    labelStyle: TextStyle = DomeBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = DomeBottomBarDefaults.windowInsets,
) {
    require(items.size >= 3 && items.size % 2 == 1) {
        "DomeBottomBar needs an odd number of items (3, 5, 7...), got ${items.size}"
    }
    val orbIndex = items.size / 2
    val shape = remember { DomeShape() }

    Box(
        modifier
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(DomeBottomBarDefaults.DomeRise + DomeBottomBarDefaults.BarHeight)
            .shadow(shadowElevation, shape, clip = false)
            .background(colors.containerColor, shape),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(DomeBottomBarDefaults.BarHeight)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEachIndexed { index, item ->
                if (index == orbIndex) {
                    // Keeps the slot under the orb so the side tabs stay evenly spaced.
                    Spacer(Modifier.weight(1f))
                } else {
                    DomeTab(
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
        DomeOrb(
            item = items[orbIndex],
            selected = orbIndex == selectedIndex,
            colors = colors,
            onClick = { onItemClick(orbIndex) },
            // Concentric with the dome circle, whose top touches the top of this box.
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = DomeBottomBarDefaults.DomeRadius - DomeBottomBarDefaults.OrbSize / 2),
        )
    }
}

@Composable
private fun DomeTab(
    item: DomeBarItem,
    selected: Boolean,
    colors: DomeBottomBarColors,
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

    // The icon keeps the same spot whether or not the label is showing, as in the design.
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
            .semantics { contentDescription = item.label }
            .padding(top = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.7f)) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.7f))
            },
            modifier = Modifier
                .size(DomeBottomBarDefaults.IconSize)
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
                modifier = Modifier.size(DomeBottomBarDefaults.IconSize),
            )
        }
        Spacer(Modifier.height(5.dp))
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(tween(220, delayMillis = 60)) + slideInVertically(tween(260, delayMillis = 60)) { it / 2 },
            exit = fadeOut(tween(120)) + slideOutVertically(tween(160)) { it / 3 },
            label = "tabLabel",
        ) {
            BasicText(
                text = item.label,
                style = labelStyle.copy(color = colors.selectedContentColor, fontWeight = FontWeight.Medium),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DomeOrb(
    item: DomeBarItem,
    selected: Boolean,
    colors: DomeBottomBarColors,
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

    // 0 -> 1 progress of the glow pulse that swells out of the orb when it becomes selected.
    val pulse = remember { Animatable(1f) }
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        if (selected && !wasSelected) {
            pulse.snapTo(0f)
            pulse.animateTo(1f, tween(PulseDurationMillis, easing = FastOutSlowInEasing))
        }
        wasSelected = selected
    }

    Box(
        modifier = modifier
            .size(DomeBottomBarDefaults.OrbSize)
            .graphicsLayer {
                val scale = bounce.value * pressScale
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                val progress = pulse.value
                if (progress < 1f) {
                    drawCircle(
                        color = colors.orbGlowColor.copy(alpha = 0.35f * (1f - progress)),
                        radius = size.minDimension / 2f * (1f + 0.4f * progress),
                    )
                }
            }
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = colors.orbGlowColor,
                spotColor = colors.orbGlowColor,
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
                modifier = Modifier.size(DomeBottomBarDefaults.OrbIconSize),
            )
        }
    }
}

/**
 * Bar outline: a rectangle starting [DomeBottomBarDefaults.DomeRise] below the top, with a circular dome
 * centered on top that blends into the edge through two concave fillets.
 */
private class DomeShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        with(density) {
            val rise = DomeBottomBarDefaults.DomeRise.toPx()
            val domeR = DomeBottomBarDefaults.DomeRadius.toPx()
            val filletR = DomeBottomBarDefaults.DomeFilletRadius.toPx()
            val topR = DomeBottomBarDefaults.TopCornerRadius.toPx()
            val bottomR = DomeBottomBarDefaults.BottomCornerRadius.toPx()

            val top = rise
            val cx = size.width / 2f
            val domeCy = top - rise + domeR
            // Fillet circles sit on the top edge and touch the dome circle from outside.
            val dy = domeCy - (top - filletR)
            val dx = sqrt((domeR + filletR) * (domeR + filletR) - dy * dy)
            val tangent = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()

            val path = Path().apply {
                moveTo(0f, top + topR)
                arcTo(Rect(0f, top, 2 * topR, top + 2 * topR), 180f, 90f, false)
                lineTo(cx - dx, top)
                arcTo(circle(cx - dx, top - filletR, filletR), 90f, tangent - 90f, false)
                arcTo(circle(cx, domeCy, domeR), tangent - 180f, 180f - 2 * tangent, false)
                arcTo(circle(cx + dx, top - filletR, filletR), 180f - tangent, tangent - 90f, false)
                lineTo(size.width - topR, top)
                arcTo(Rect(size.width - 2 * topR, top, size.width, top + 2 * topR), 270f, 90f, false)
                lineTo(size.width, size.height - bottomR)
                arcTo(Rect(size.width - 2 * bottomR, size.height - 2 * bottomR, size.width, size.height), 0f, 90f, false)
                lineTo(bottomR, size.height)
                arcTo(Rect(0f, size.height - 2 * bottomR, 2 * bottomR, size.height), 90f, 90f, false)
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
        }
        wasSelected = selected
    }
    return scale
}

private const val TintDurationMillis = 250
private const val PulseDurationMillis = 520
