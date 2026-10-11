package com.nameisjayant.bottombar.tile

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A destination shown in a [TileBottomBar].
 *
 * @param label read out for accessibility; the bar itself only shows icons.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown on the tile while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class TileBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [TileBottomBar]. See [TileBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class TileBottomBarColors(
    public val containerColor: Color,
    public val indicatorColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
)

public object TileBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 64.dp

    /** Size of the tab icons. */
    public val IconSize: Dp = 24.dp

    /** Diameter of the dot under the selected icon. */
    public val DotSize: Dp = 4.dp

    /** Gap between the selected icon and its dot. */
    public val DotSpacing: Dp = 5.dp

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): TileBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFF2F2F2),
        indicatorColor: Color = Color(0xFF5634A8),
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFF1C1C1E),
    ): TileBottomBarColors = TileBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF202020),
        indicatorColor: Color = Color(0xFF5634A8),
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFF2F2F2),
    ): TileBottomBarColors = TileBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )
}

/**
 * A flat, square-cornered bar of icon-only tabs. The selected tab is filled edge to edge with an accent
 * tile, and its icon fills in with a small dot underneath.
 *
 * When the selection changes the tile slides to the new tab, its leading edge running ahead of its
 * trailing one so it stretches in flight and snaps back to a square when it lands. Every tab can be
 * selected and every slot gets the same width, so 3 to 5 items look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar.
 */
@Composable
public fun TileBottomBar(
    items: List<TileBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: TileBottomBarColors = TileBottomBarDefaults.colors(),
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = TileBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "TileBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current

    // Fractional tab indexes the tile's two edges sit on. The edge heading towards the new tab moves on
    // a stiffer spring than the other, so the tile stretches mid-flight and settles back to one slot.
    val startEdge = remember { Animatable(selectedIndex.toFloat()) }
    val endEdge = remember { Animatable(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        val target = selectedIndex.toFloat()
        val forward = target > (startEdge.value + endEdge.value) / 2f
        launch { startEdge.animateTo(target, if (forward) TrailingSpring else LeadingSpring) }
        launch { endEdge.animateTo(target, if (forward) LeadingSpring else TrailingSpring) }
    }

    Box(
        modifier = modifier
            .shadow(shadowElevation)
            .background(colors.containerColor)
            .windowInsetsPadding(windowInsets),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TileBottomBarDefaults.BarHeight)
                .drawBehind {
                    val slot = size.width / items.size
                    val left = slot * minOf(startEdge.value, endEdge.value)
                    val right = slot * (maxOf(startEdge.value, endEdge.value) + 1f)
                    val x = if (layoutDirection == LayoutDirection.Ltr) left else size.width - right
                    drawRect(
                        color = colors.indicatorColor,
                        topLeft = Offset(x, 0f),
                        size = Size(right - left, size.height),
                    )
                }
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                TileTab(
                    item = item,
                    selected = index == selectedIndex,
                    colors = colors,
                    onClick = { onItemClick(index) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TileTab(
    item: TileBarItem,
    selected: Boolean,
    colors: TileBottomBarColors,
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
    // 0 = icon centered on its own, 1 = icon nudged up with the dot showing underneath.
    val lift by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
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
            .semantics { contentDescription = item.label }
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        contentAlignment = Alignment.Center,
    ) {
        val shift = (TileBottomBarDefaults.DotSpacing + TileBottomBarDefaults.DotSize) / 2
        Crossfade(
            targetState = selected,
            animationSpec = tween(TintDurationMillis),
            modifier = Modifier.graphicsLayer { translationY = -shift.toPx() * lift },
            label = "tabIcon",
        ) { isSelected ->
            Image(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(contentColor),
                modifier = Modifier.size(TileBottomBarDefaults.IconSize),
            )
        }
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = (TileBottomBarDefaults.IconSize / 2 + TileBottomBarDefaults.DotSpacing).toPx() +
                        TileBottomBarDefaults.DotSize.toPx() / 2 - shift.toPx() * lift
                    val scale = lift.coerceAtLeast(0f)
                    scaleX = scale
                    scaleY = scale
                }
                .size(TileBottomBarDefaults.DotSize)
                .background(contentColor, CircleShape),
        )
    }
}

/** Spring for the tile's edge heading towards the new tab. */
private val LeadingSpring = spring<Float>(dampingRatio = 0.85f, stiffness = 600f)

/** Spring for the tile's edge leaving the old tab; softer, so the tile stretches in flight. */
private val TrailingSpring = spring<Float>(dampingRatio = 0.85f, stiffness = 240f)

private const val TintDurationMillis = 200
