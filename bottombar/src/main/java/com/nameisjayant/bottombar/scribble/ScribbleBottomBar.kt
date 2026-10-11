package com.nameisjayant.bottombar.scribble

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A destination shown in a [ScribbleBottomBar].
 *
 * @param label read out for accessibility; the bar itself shows icons only.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected. Defaults to [icon], as in the original design
 * the scribble and the darker tint already mark the selection.
 */
@Immutable
public class ScribbleBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [ScribbleBottomBar]. See [ScribbleBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class ScribbleBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val scribbleColor: Color,
)

public object ScribbleBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 68.dp

    /** Diameter of the hand-drawn loop around the selected icon. */
    public val ScribbleSize: Dp = 42.dp

    /** Stroke width of the loop. */
    public val ScribbleStrokeWidth: Dp = 2.dp

    /** Diameter of the dot under the loop. */
    public val DotSize: Dp = 4.dp

    /** Space between the bottom of the loop and the dot. */
    public val DotGap: Dp = 3.dp

    /** Space between the bar's sides and the outermost tab slots. */
    public val HorizontalPadding: Dp = 8.dp

    public val IconSize: Dp = 24.dp

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): ScribbleBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF14141C),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        scribbleColor: Color = Color(0xFF5B3CE0),
    ): ScribbleBottomBarColors = ScribbleBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        scribbleColor = scribbleColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        scribbleColor: Color = Color(0xFF6447F0),
    ): ScribbleBottomBarColors = ScribbleBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        scribbleColor = scribbleColor,
    )
}

/**
 * A flat bar of icon-only tabs. The selected tab is circled by a hand-drawn loop that overshoots its
 * start, like a quick pen stroke, with a small dot underneath. When a tab becomes selected the loop draws
 * itself around the icon while turning into place, then the dot pops in.
 *
 * Every tab can be selected and all slots share the width equally, so 3 to 5 items look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 */
@Composable
public fun ScribbleBottomBar(
    items: List<ScribbleBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: ScribbleBottomBarColors = ScribbleBottomBarDefaults.colors(),
    windowInsets: WindowInsets = ScribbleBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "ScribbleBottomBar needs at least one item" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.containerColor)
            .windowInsetsPadding(windowInsets)
            .height(ScribbleBottomBarDefaults.BarHeight)
            .padding(horizontal = ScribbleBottomBarDefaults.HorizontalPadding)
            .selectableGroup(),
    ) {
        items.forEachIndexed { index, item ->
            ScribbleTab(
                item = item,
                selected = index == selectedIndex,
                colors = colors,
                onClick = { onItemClick(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ScribbleTab(
    item: ScribbleBarItem,
    selected: Boolean,
    colors: ScribbleBottomBarColors,
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
    val contentColor by animateColorAsState(
        targetValue = if (selected) colors.selectedContentColor else colors.unselectedContentColor,
        animationSpec = tween(TintDurationMillis),
        label = "tabContentColor",
    )
    // How much of the loop is drawn; fully drawn from the start for the initially selected tab.
    val draw = remember { Animatable(if (selected) 1f else 0f) }
    // Fades the loop out on deselection instead of un-drawing it.
    val alpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(if (selected) 0 else FadeOutMillis),
        label = "scribbleAlpha",
    )
    val dot by animateFloatAsState(
        targetValue = if (selected && draw.value >= DotThreshold) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "scribbleDot",
    )
    val bounce = rememberSelectionBounce(selected, from = 0.7f)
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        if (selected && !wasSelected) {
            draw.snapTo(0f)
            draw.animateTo(1f, tween(DrawMillis, easing = FastOutSlowInEasing))
        }
        wasSelected = selected
    }

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
            .semantics { contentDescription = item.label },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(ScribbleBottomBarDefaults.ScribbleSize)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .drawBehind {
                    val progress = draw.value
                    if (alpha <= 0f) return@drawBehind
                    val stroke = ScribbleBottomBarDefaults.ScribbleStrokeWidth.toPx()
                    if (progress > 0f) {
                        val full = scribblePath(size.minDimension / 2f - stroke / 2f, center)
                        val measure = PathMeasure().apply { setPath(full, false) }
                        val partial = Path()
                        measure.getSegment(0f, measure.length * progress, partial, true)
                        // The loop turns the last stretch into place as it closes, like a flick of the pen.
                        rotate(degrees = (1f - progress) * -ScribbleTurnDegrees, pivot = center) {
                            drawPath(
                                path = partial,
                                color = colors.scribbleColor,
                                alpha = alpha,
                                style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round),
                            )
                        }
                    }
                    if (dot > 0f) {
                        val dotRadius = ScribbleBottomBarDefaults.DotSize.toPx() / 2f
                        drawCircle(
                            color = colors.scribbleColor,
                            radius = dotRadius * dot,
                            center = Offset(
                                center.x,
                                size.height + ScribbleBottomBarDefaults.DotGap.toPx() + dotRadius,
                            ),
                            alpha = alpha,
                        )
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.7f)) togetherWith
                        (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.7f))
                },
                modifier = Modifier.graphicsLayer {
                    scaleX = bounce.value
                    scaleY = bounce.value
                },
                label = "tabIcon",
            ) { isSelected ->
                Image(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(contentColor),
                    modifier = Modifier.size(ScribbleBottomBarDefaults.IconSize),
                )
            }
        }
    }
}

/**
 * A pen-like loop of roughly [radius] around [center]. It starts at the lower left, runs clockwise over
 * the top and all the way round, then carries on past its start up the left side. Its center drifts left
 * along the way, so the overshoot bulges out beside the start and crosses it near the top, leaving the
 * doubled stroke on the left that makes it read as hand drawn.
 */
private fun scribblePath(radius: Float, center: Offset): Path = Path().apply {
    val steps = 96
    for (i in 0..steps) {
        val t = i / steps.toFloat()
        val angle = (StartAngleDegrees + SweepDegrees * t) * PI.toFloat() / 180f
        val r = radius * (0.95f + 0.04f * t)
        // The center drifts left as the pen goes round, so the overshoot crosses the start near the top.
        val cx = center.x + radius * (0.06f - 0.16f * t)
        val cy = center.y + radius * (0.03f - 0.05f * t)
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
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

/** Where the loop starts, in degrees clockwise from 3 o'clock: the lower left. */
private const val StartAngleDegrees = 150f

/** How far the loop runs: a full turn plus the overshoot up the left side. */
private const val SweepDegrees = 430f

/** How far the loop turns into place while it is drawn. */
private const val ScribbleTurnDegrees = 40f

/** Share of the loop drawn before the dot pops in. */
private const val DotThreshold = 0.75f

private const val DrawMillis = 480
private const val FadeOutMillis = 160
private const val TintDurationMillis = 250
