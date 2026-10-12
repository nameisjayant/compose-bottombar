package com.nameisjayant.bottombar.bloom

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * A destination shown in a [BloomBottomBar].
 *
 * @param label text shown beside the orb while the item is selected, and read out for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown inside the orb while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class BloomBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [BloomBottomBar]. See [BloomBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class BloomBottomBarColors(
    public val containerColor: Color,
    public val orbColor: Color,
    public val orbContentColor: Color,
    public val unselectedContentColor: Color,
    public val labelColor: Color = orbColor,
)

public object BloomBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 72.dp

    /** Diameter of the orb behind the selected icon. */
    public val OrbSize: Dp = 40.dp

    /** Space between the orb and the selected label. */
    public val LabelSpacing: Dp = 8.dp

    /** Space between the bar's edges and the outermost tabs. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 24.dp)

    public val IconSize: Dp = 22.dp

    public val LabelStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
    )

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): BloomBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        orbColor: Color = Color(0xFF8B4DF0),
        orbContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        labelColor: Color = orbColor,
    ): BloomBottomBarColors = BloomBottomBarColors(
        containerColor = containerColor,
        orbColor = orbColor,
        orbContentColor = orbContentColor,
        unselectedContentColor = unselectedContentColor,
        labelColor = labelColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF212121),
        orbColor: Color = Color(0xFF9257F5),
        orbContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        labelColor: Color = orbColor,
    ): BloomBottomBarColors = BloomBottomBarColors(
        containerColor = containerColor,
        orbColor = orbColor,
        orbContentColor = orbContentColor,
        unselectedContentColor = unselectedContentColor,
        labelColor = labelColor,
    )
}

/**
 * A flat bar of icon-only tabs. The selected icon fills in on an accent orb, with its label beside it.
 *
 * When the selection changes the new orb blooms out from the icon with a little overshoot while the label
 * unrolls to its side, and the old tab folds back to a plain icon. The tabs are spread across the bar, so
 * they make room for the label as it grows. Every tab can be selected; 3 to 5 items with short labels
 * look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar.
 */
@Composable
public fun BloomBottomBar(
    items: List<BloomBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: BloomBottomBarColors = BloomBottomBarDefaults.colors(),
    labelStyle: TextStyle = BloomBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = BloomBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "BloomBottomBar needs at least one item" }

    Row(
        modifier = modifier
            .shadow(shadowElevation)
            .background(colors.containerColor)
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(BloomBottomBarDefaults.BarHeight)
            .padding(BloomBottomBarDefaults.ContentPadding)
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            BloomTab(
                item = item,
                selected = index == selectedIndex,
                colors = colors,
                labelStyle = labelStyle,
                onClick = { onItemClick(index) },
            )
        }
    }
}

@Composable
private fun BloomTab(
    item: BloomBarItem,
    selected: Boolean,
    colors: BloomBottomBarColors,
    labelStyle: TextStyle,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabPress",
    )
    // Orb radius as a fraction of its full size; overshoots a little as it blooms.
    val bloom by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = if (selected) {
            spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)
        } else {
            tween(OrbFoldMillis)
        },
        label = "tabBloom",
    )
    // How much of the label is unrolled, 0 = hidden, 1 = full width.
    val reveal by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessMediumLow),
        label = "tabReveal",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) colors.labelColor else colors.unselectedContentColor,
        animationSpec = tween(TintDurationMillis),
        label = "tabLabelColor",
    )

    Row(
        modifier = Modifier
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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(BloomBottomBarDefaults.OrbSize)
                .drawBehind {
                    if (bloom > 0f) drawCircle(colors.orbColor, radius = size.minDimension / 2f * bloom)
                },
            contentAlignment = Alignment.Center,
        ) {
            Crossfade(targetState = selected, animationSpec = tween(TintDurationMillis), label = "tabIcon") { isSelected ->
                Image(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = null,
                    // Turns from the idle tint to the orb's content color as the orb grows under it.
                    colorFilter = ColorFilter.tint(
                        lerp(colors.unselectedContentColor, colors.orbContentColor, bloom.coerceIn(0f, 1f)),
                    ),
                    modifier = Modifier.size(BloomBottomBarDefaults.IconSize),
                )
            }
        }
        BasicText(
            text = item.label,
            style = labelStyle.copy(color = labelColor),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .clipToBounds()
                .layout { measurable, constraints ->
                    // Measured at full width, laid out only as wide as it has unrolled so far.
                    val placeable = measurable.measure(constraints.copy(minWidth = 0, maxWidth = Constraints.Infinity))
                    val width = (placeable.width * reveal.coerceIn(0f, 1f)).roundToInt()
                    layout(width, placeable.height) { placeable.placeRelative(0, 0) }
                }
                .graphicsLayer { alpha = reveal.coerceIn(0f, 1f) }
                .padding(start = BloomBottomBarDefaults.LabelSpacing),
        )
    }
}

private const val TintDurationMillis = 250

private const val OrbFoldMillis = 180
