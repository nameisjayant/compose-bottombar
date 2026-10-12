package com.nameisjayant.bottombar.arch

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlinx.coroutines.launch

/**
 * A destination shown in an [ArchBottomBar].
 *
 * @param label read out for accessibility; the bar itself only shows icons.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown inside the arch while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class ArchBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by an [ArchBottomBar]. See [ArchBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class ArchBottomBarColors(
    public val containerColor: Color,
    public val indicatorColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
)

public object ArchBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 64.dp

    /** Width of the arch, which is also the diameter of its rounded top. */
    public val ArchWidth: Dp = 46.dp

    /** Height of the arch, measured up from the bar's bottom edge. */
    public val ArchHeight: Dp = 50.dp

    /** Size of the unselected tab icons. */
    public val IconSize: Dp = 24.dp

    /** Size of the icon inside the arch. */
    public val SelectedIconSize: Dp = 20.dp

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): ArchBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        indicatorColor: Color = Color(0xFF5B3FDF),
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFA3AEC2),
    ): ArchBottomBarColors = ArchBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF212121),
        indicatorColor: Color = Color(0xFF5B3FDF),
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFA3AEC2),
    ): ArchBottomBarColors = ArchBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )
}

/**
 * A flat bar of icon-only tabs. An accent arch, flat at the bottom and round on top, rises out of the
 * bar's bottom edge behind the selected tab, and its icon fills in inside the arch's rounded top.
 *
 * When the selection changes the arch sinks back into the edge, glides to the new tab and springs up
 * again. Every tab can be selected and every slot gets the same width, so 3 to 5 items look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar.
 */
@Composable
public fun ArchBottomBar(
    items: List<ArchBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: ArchBottomBarColors = ArchBottomBarDefaults.colors(),
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = ArchBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "ArchBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current

    // Fractional tab index the arch is centered on, and how far it stands out of the bottom edge
    // (1 = full height). The arch dips while it glides and springs back up once it is on its way.
    val position = remember { Animatable(selectedIndex.toFloat()) }
    val rise = remember { Animatable(1f) }
    LaunchedEffect(selectedIndex) {
        val target = selectedIndex.toFloat()
        if (position.value == target) return@LaunchedEffect
        launch {
            rise.animateTo(SunkenRise, tween(SinkDurationMillis))
            rise.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow))
        }
        position.animateTo(target, spring(dampingRatio = 0.8f, stiffness = 320f))
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
                .height(ArchBottomBarDefaults.BarHeight)
                .drawBehind {
                    val slot = size.width / items.size
                    val center = slot * (position.value + 0.5f)
                    val cx = if (layoutDirection == LayoutDirection.Ltr) center else size.width - center
                    val halfWidth = ArchBottomBarDefaults.ArchWidth.toPx() / 2
                    val top = size.height - ArchBottomBarDefaults.ArchHeight.toPx() * rise.value
                    val corner = CornerRadius(halfWidth)
                    val arch = Path().apply {
                        addRoundRect(
                            RoundRect(
                                left = cx - halfWidth,
                                top = top,
                                right = cx + halfWidth,
                                bottom = size.height,
                                topLeftCornerRadius = corner,
                                topRightCornerRadius = corner,
                            ),
                        )
                    }
                    drawPath(arch, colors.indicatorColor)
                }
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                ArchTab(
                    item = item,
                    selected = index == selectedIndex,
                    // How much the arch sits behind this tab: 1 when it stands fully raised on it.
                    coverage = {
                        val across = (1f - abs(position.value - index) * CoverageFalloff).coerceIn(0f, 1f)
                        val up = ((rise.value - SunkenRise) / (1f - SunkenRise)).coerceIn(0f, 1f)
                        across * up
                    },
                    colors = colors,
                    onClick = { onItemClick(index) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ArchTab(
    item: ArchBarItem,
    selected: Boolean,
    coverage: () -> Float,
    colors: ArchBottomBarColors,
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
    val amount = coverage()

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
        Image(
            imageVector = if (amount > 0.5f) item.selectedIcon else item.icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(lerp(colors.unselectedContentColor, colors.selectedContentColor, amount)),
            modifier = Modifier
                .graphicsLayer {
                    // Sit the icon in the middle of the arch's rounded top once the arch is under it.
                    val archTopCenter = ArchBottomBarDefaults.BarHeight - ArchBottomBarDefaults.ArchHeight +
                        ArchBottomBarDefaults.ArchWidth / 2
                    translationY = (archTopCenter - ArchBottomBarDefaults.BarHeight / 2).toPx() * amount
                    val scale = 1f + (ArchBottomBarDefaults.SelectedIconSize / ArchBottomBarDefaults.IconSize - 1f) * amount
                    scaleX = scale
                    scaleY = scale
                }
                .size(ArchBottomBarDefaults.IconSize),
        )
    }
}

/** Share of [ArchBottomBarDefaults.ArchHeight] the arch keeps while it glides between tabs. */
private const val SunkenRise = 0.3f

/** How quickly a tab stops counting as covered as the arch moves away from it, per slot. */
private const val CoverageFalloff = 1.6f

private const val SinkDurationMillis = 140
