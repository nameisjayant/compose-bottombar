package com.nameisjayant.bottombar.peak

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * A destination shown in a [PeakBottomBar].
 *
 * @param label text shown under the icon while the item is selected, and read out for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class PeakBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [PeakBottomBar]. See [PeakBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class PeakBottomBarColors(
    public val containerColor: Color,
    public val indicatorColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
)

public object PeakBottomBarDefaults {
    /** Height of the pill-shaped bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 70.dp

    /** Width of the strip along the bottom edge under the selected tab, at rest. */
    public val IndicatorWidth: Dp = 60.dp

    /** Thickness of the strip under the selected tab. */
    public val IndicatorThickness: Dp = 4.dp

    /** Width of the small peak rising from the middle of the strip. */
    public val PeakWidth: Dp = 16.dp

    /** How far the peak rises above the top of the strip. */
    public val PeakHeight: Dp = 5.dp

    /** Space between the bar's rounded ends and the outermost tab slots. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 28.dp)

    public val IconSize: Dp = 24.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): PeakBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        indicatorColor: Color = Color(0xFF3F63EA),
        selectedContentColor: Color = indicatorColor,
        unselectedContentColor: Color = Color(0xFFA9B5D1),
    ): PeakBottomBarColors = PeakBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        indicatorColor: Color = Color(0xFF4A6CF0),
        selectedContentColor: Color = indicatorColor,
        unselectedContentColor: Color = Color(0xFFC3CCE0),
    ): PeakBottomBarColors = PeakBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )
}

/**
 * A floating, pill-shaped bar of icon-only tabs. The selected tab lifts its icon, fills it in and shows
 * its label underneath, and an accent strip with a small peak in its middle sits on the bar's bottom
 * edge under it.
 *
 * When the selection changes the strip slides over to the new tab, stretching as its leading end runs
 * ahead and its trailing end catches up. Every tab can be selected and every slot gets the same width,
 * so 3 to 5 items with short labels look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar.
 */
@Composable
public fun PeakBottomBar(
    items: List<PeakBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: PeakBottomBarColors = PeakBottomBarDefaults.colors(),
    labelStyle: TextStyle = PeakBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = PeakBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "PeakBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current
    val padding = PeakBottomBarDefaults.ContentPadding
    val shape = RoundedCornerShape(percent = 50)

    // Fractional tab indexes the strip's two ends sit under. The end heading towards the new tab moves
    // on a stiffer spring than the other, so the strip stretches mid-flight and settles back to size.
    val startEdge = remember { Animatable(selectedIndex.toFloat()) }
    val endEdge = remember { Animatable(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        val target = selectedIndex.toFloat()
        val forward = target > (startEdge.value + endEdge.value) / 2f
        launch { startEdge.animateTo(target, if (forward) TrailingSpring else LeadingSpring) }
        launch { endEdge.animateTo(target, if (forward) LeadingSpring else TrailingSpring) }
    }

    Row(
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(PeakBottomBarDefaults.BarHeight)
            .shadow(shadowElevation, shape)
            .clip(shape)
            .background(colors.containerColor)
            .drawBehind {
                val start = padding.calculateStartPadding(layoutDirection).toPx()
                val slot = (size.width - start - padding.calculateEndPadding(layoutDirection).toPx()) / items.size
                fun centerOf(position: Float): Float {
                    val fromStart = start + slot * (position + 0.5f)
                    return if (layoutDirection == LayoutDirection.Ltr) fromStart else size.width - fromStart
                }
                val halfWidth = PeakBottomBarDefaults.IndicatorWidth.toPx() / 2f
                val a = centerOf(startEdge.value)
                val b = centerOf(endEdge.value)
                drawPath(
                    path = indicatorPath(
                        left = minOf(a, b) - halfWidth,
                        right = maxOf(a, b) + halfWidth,
                        bottom = size.height,
                        thickness = PeakBottomBarDefaults.IndicatorThickness.toPx(),
                        peakHalfWidth = PeakBottomBarDefaults.PeakWidth.toPx() / 2f,
                        peakHeight = PeakBottomBarDefaults.PeakHeight.toPx(),
                    ),
                    color = colors.indicatorColor,
                )
            }
            .padding(padding)
            .selectableGroup(),
    ) {
        items.forEachIndexed { index, item ->
            PeakTab(
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

@Composable
private fun PeakTab(
    item: PeakBarItem,
    selected: Boolean,
    colors: PeakBottomBarColors,
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
    // 0 = icon centered on its own, 1 = icon lifted with the label showing underneath.
    val lift by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "tabLift",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) colors.selectedContentColor else colors.unselectedContentColor,
        animationSpec = tween(TintDurationMillis),
        label = "tabContentColor",
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
            // The label is only on screen while selected, so give TalkBack the name the rest of the time.
            .semantics { if (!selected) contentDescription = item.label }
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        contentAlignment = Alignment.Center,
    ) {
        Crossfade(
            targetState = selected,
            animationSpec = tween(TintDurationMillis),
            modifier = Modifier.graphicsLayer { translationY = -IconLift.toPx() * lift },
            label = "tabIcon",
        ) { isSelected ->
            Image(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(contentColor),
                modifier = Modifier.size(PeakBottomBarDefaults.IconSize),
            )
        }
        BasicText(
            text = item.label,
            style = labelStyle.copy(color = contentColor),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.graphicsLayer {
                alpha = lift.coerceIn(0f, 1f)
                // Rises into place from just below its resting spot as it fades in.
                translationY = (LabelDrop + LabelRise * (1f - lift)).toPx()
                val scale = 0.8f + 0.2f * lift
                scaleX = scale
                scaleY = scale
            },
        )
    }
}

/**
 * The strip from [left] to [right] along [bottom], with rounded top corners and a soft peak rising
 * from its middle.
 */
private fun indicatorPath(
    left: Float,
    right: Float,
    bottom: Float,
    thickness: Float,
    peakHalfWidth: Float,
    peakHeight: Float,
): Path = Path().apply {
    val top = bottom - thickness
    val corner = thickness / 2f
    val cx = (left + right) / 2f
    moveTo(left, bottom)
    lineTo(left, top + corner)
    quadraticTo(left, top, left + corner, top)
    lineTo(cx - peakHalfWidth, top)
    cubicTo(cx - peakHalfWidth * 0.45f, top, cx - peakHalfWidth * 0.3f, top - peakHeight, cx, top - peakHeight)
    cubicTo(cx + peakHalfWidth * 0.3f, top - peakHeight, cx + peakHalfWidth * 0.45f, top, cx + peakHalfWidth, top)
    lineTo(right - corner, top)
    quadraticTo(right, top, right, top + corner)
    lineTo(right, bottom)
    close()
}

/** Spring for the strip's end heading towards the new tab. */
private val LeadingSpring = spring<Float>(dampingRatio = 0.8f, stiffness = 520f)

/** Spring for the strip's end leaving the old tab, slower so the strip stretches on the way. */
private val TrailingSpring = spring<Float>(dampingRatio = 0.85f, stiffness = 210f)

/** How far the selected icon rises above the tab's center. */
private val IconLift = 10.dp

/** Distance from the tab's center to the label's center while it shows. */
private val LabelDrop = 15.dp

/** Extra distance the label rises from as it fades in. */
private val LabelRise = 6.dp

private const val TintDurationMillis = 250
