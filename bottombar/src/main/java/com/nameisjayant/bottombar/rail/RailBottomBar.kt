package com.nameisjayant.bottombar.rail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A destination shown in a [RailBottomBar].
 *
 * @param label text shown next to the icon while the item is selected, and read out for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class RailBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [RailBottomBar]. See [RailBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class RailBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val railColor: Color,
    public val unselectedContentColor: Color,
)

public object RailBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 66.dp

    /** Vertical distance between the two rails framing the selected tab. */
    public val RailSpacing: Dp = 38.dp

    /** Thickness of each rail. */
    public val RailThickness: Dp = 1.dp

    /** How far the rails reach past the selected tab's icon and label on each side. */
    public val RailOverhang: Dp = 18.dp

    /** Space between the bar's sides and the outermost tab slots. */
    public val HorizontalPadding: Dp = 8.dp

    public val IconSize: Dp = 24.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): RailBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF3F63EA),
        railColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
    ): RailBottomBarColors = RailBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        railColor = railColor,
        unselectedContentColor = unselectedContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        selectedContentColor: Color = Color(0xFF4A6CF0),
        railColor: Color = Color(0xFF4A6CF0),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
    ): RailBottomBarColors = RailBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        railColor = railColor,
        unselectedContentColor = unselectedContentColor,
    )
}

/**
 * A flat bar of icon-only tabs. The selected tab widens to show its label beside a filled icon, and is
 * framed by two thin rails, one above and one below, that grow out from its center.
 *
 * Every tab can be selected and slots share the width, with the selected one taking a larger share, so
 * 3 to 5 items with short labels look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 */
@Composable
public fun RailBottomBar(
    items: List<RailBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: RailBottomBarColors = RailBottomBarDefaults.colors(),
    labelStyle: TextStyle = RailBottomBarDefaults.LabelStyle,
    windowInsets: WindowInsets = RailBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "RailBottomBar needs at least one item" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.containerColor)
            .windowInsetsPadding(windowInsets)
            .height(RailBottomBarDefaults.BarHeight)
            .padding(horizontal = RailBottomBarDefaults.HorizontalPadding)
            .selectableGroup(),
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            val weight by animateFloatAsState(
                targetValue = if (selected) SelectedWeight else 1f,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
                label = "tabWeight",
            )
            RailTab(
                item = item,
                selected = selected,
                colors = colors,
                labelStyle = labelStyle,
                onClick = { onItemClick(index) },
                modifier = Modifier.weight(weight),
            )
        }
    }
}

@Composable
private fun RailTab(
    item: RailBarItem,
    selected: Boolean,
    colors: RailBottomBarColors,
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
    // 0 = no rails, 1 = rails spanning the icon and label. Overshoots a little as they snap into place.
    val rails by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "tabRails",
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
            .semantics { if (!selected) contentDescription = item.label },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(RailBottomBarDefaults.RailSpacing)
                .drawBehind {
                    if (rails <= 0f) return@drawBehind
                    val thickness = RailBottomBarDefaults.RailThickness.toPx()
                    val halfWidth = (size.width / 2f) * rails
                    val start = center.x - halfWidth
                    val end = center.x + halfWidth
                    val top = thickness / 2f
                    val bottom = size.height - thickness / 2f
                    val alpha = rails.coerceIn(0f, 1f)
                    drawLine(colors.railColor, Offset(start, top), Offset(end, top), thickness, alpha = alpha)
                    drawLine(colors.railColor, Offset(start, bottom), Offset(end, bottom), thickness, alpha = alpha)
                }
                .padding(horizontal = RailBottomBarDefaults.RailOverhang)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.6f)) togetherWith
                        (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.6f))
                },
                label = "tabIcon",
            ) { isSelected ->
                Image(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(contentColor),
                    modifier = Modifier.size(RailBottomBarDefaults.IconSize),
                )
            }
            AnimatedVisibility(
                visible = selected,
                enter = expandHorizontally(tween(LabelDurationMillis)) + fadeIn(tween(LabelDurationMillis)),
                exit = shrinkHorizontally(tween(LabelDurationMillis)) + fadeOut(tween(LabelDurationMillis / 2)),
            ) {
                BasicText(
                    text = item.label,
                    style = labelStyle.copy(color = contentColor, fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(start = LabelGap),
                )
            }
        }
    }
}

/** Share of the bar's width the selected slot takes, relative to the other slots' 1. */
private const val SelectedWeight = 1.6f

/** Space between the selected tab's icon and its label. */
private val LabelGap = 10.dp

private const val TintDurationMillis = 250
private const val LabelDurationMillis = 260
