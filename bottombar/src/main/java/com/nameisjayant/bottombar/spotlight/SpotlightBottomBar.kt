package com.nameisjayant.bottombar.spotlight

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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * A destination shown in a [SpotlightBottomBar].
 *
 * @param label text shown under the icon while the item is selected, and read out for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class SpotlightBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [SpotlightBottomBar]. See [SpotlightBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class SpotlightBottomBarColors(
    public val containerColor: Color,
    public val indicatorColor: Color,
    public val accentColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
)

public object SpotlightBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 68.dp

    /** Corner radius of the floating bar. */
    public val CornerRadius: Dp = 16.dp

    /** Gap between the panel behind the selected tab and the bar's top and bottom edges. */
    public val IndicatorInset: Dp = 2.dp

    /** Thickness of the accent line along the top of the panel. */
    public val AccentThickness: Dp = 2.dp

    /** Space on each side of the selected label inside the panel. */
    public val IndicatorPadding: Dp = 18.dp

    /** The panel is never narrower than this, so short labels still get a roomy spotlight. */
    public val MinIndicatorWidth: Dp = 64.dp

    /** Space between the bar's rounded ends and the outermost tab slots. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 20.dp)

    public val IconSize: Dp = 24.dp

    public val LabelStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): SpotlightBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        indicatorColor: Color = Color(0xFFEBEFFD),
        accentColor: Color = Color(0xFF3F63EA),
        selectedContentColor: Color = accentColor,
        unselectedContentColor: Color = Color(0xFFA9B5D1),
    ): SpotlightBottomBarColors = SpotlightBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        accentColor = accentColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF212121),
        indicatorColor: Color = Color(0xFF242A3C),
        accentColor: Color = Color(0xFF4A6CF0),
        selectedContentColor: Color = accentColor,
        unselectedContentColor: Color = Color(0xFFC3CCE0),
    ): SpotlightBottomBarColors = SpotlightBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        accentColor = accentColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )
}

/**
 * A floating, rounded bar of icon-only tabs. The selected tab sits in a tinted panel with an accent line
 * along its top, like a spotlight shining down on it; its icon fills in and lifts over its label.
 *
 * The panel hugs the selected label, so it is wider for longer labels. When the selection changes the
 * panel slides over to the new tab, its leading edge running ahead of its trailing one so it stretches
 * in flight, and resizes to the new label as it lands. Every tab can be selected and every slot gets the
 * same width, so 3 to 5 items with short labels look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar.
 */
@Composable
public fun SpotlightBottomBar(
    items: List<SpotlightBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: SpotlightBottomBarColors = SpotlightBottomBarDefaults.colors(),
    labelStyle: TextStyle = SpotlightBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = SpotlightBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "SpotlightBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current
    val padding = SpotlightBottomBarDefaults.ContentPadding
    val shape = RoundedCornerShape(SpotlightBottomBarDefaults.CornerRadius)

    // Panel width for each tab, in px: its label plus padding on both sides, never below the minimum.
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val panelWidths = remember(items, labelStyle, textMeasurer, density) {
        with(density) {
            val sidePadding = SpotlightBottomBarDefaults.IndicatorPadding.toPx() * 2
            val minWidth = SpotlightBottomBarDefaults.MinIndicatorWidth.toPx()
            items.map { item ->
                val labelWidth = textMeasurer.measure(item.label, labelStyle, maxLines = 1, softWrap = false).size.width
                maxOf(labelWidth + sidePadding, minWidth)
            }
        }
    }
    val panelWidth by animateFloatAsState(
        targetValue = panelWidths[selectedIndex.coerceIn(items.indices)],
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "panelWidth",
    )

    // Fractional tab indexes the panel's two edges sit over. The edge heading towards the new tab moves
    // on a stiffer spring than the other, so the panel stretches mid-flight and settles back to size.
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
            .height(SpotlightBottomBarDefaults.BarHeight)
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
                val a = centerOf(startEdge.value)
                val b = centerOf(endEdge.value)
                val left = minOf(a, b) - panelWidth / 2f
                val right = maxOf(a, b) + panelWidth / 2f
                val inset = SpotlightBottomBarDefaults.IndicatorInset.toPx()
                drawRect(
                    color = colors.indicatorColor,
                    topLeft = Offset(left, inset),
                    size = Size(right - left, size.height - inset * 2),
                )
                drawRect(
                    color = colors.accentColor,
                    topLeft = Offset(left, inset),
                    size = Size(right - left, SpotlightBottomBarDefaults.AccentThickness.toPx()),
                )
            }
            .padding(padding)
            .selectableGroup(),
    ) {
        items.forEachIndexed { index, item ->
            SpotlightTab(
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
private fun SpotlightTab(
    item: SpotlightBarItem,
    selected: Boolean,
    colors: SpotlightBottomBarColors,
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
                modifier = Modifier.size(SpotlightBottomBarDefaults.IconSize),
            )
        }
        BasicText(
            text = item.label,
            style = labelStyle.copy(color = contentColor),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                // A label wider than its slot spills evenly into its neighbors instead of being cut off.
                .wrapContentWidth(unbounded = true)
                .graphicsLayer {
                    alpha = lift.coerceIn(0f, 1f)
                    // Rises into place from just below its resting spot as it fades in.
                    translationY = (LabelDrop + LabelRise * (1f - lift)).toPx()
                },
        )
    }
}

/** Spring for the panel's edge heading towards the new tab. */
private val LeadingSpring = spring<Float>(dampingRatio = 0.8f, stiffness = 560f)

/** Spring for the panel's edge leaving the old tab, slower so the panel stretches on the way. */
private val TrailingSpring = spring<Float>(dampingRatio = 0.85f, stiffness = 220f)

/** How far the selected icon rises above the tab's center. */
private val IconLift = 10.dp

/** Distance from the tab's center to the label's center while it shows. */
private val LabelDrop = 14.dp

/** Extra distance the label rises from as it fades in. */
private val LabelRise = 6.dp

private const val TintDurationMillis = 250
