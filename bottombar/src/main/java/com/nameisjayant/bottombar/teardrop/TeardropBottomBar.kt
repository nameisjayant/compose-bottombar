package com.nameisjayant.bottombar.teardrop

import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * A destination shown in a [TeardropBottomBar].
 *
 * @param label read out for accessibility; the bar itself only shows icons.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown inside the teardrop while the item is selected, usually the filled twin
 * of [icon].
 */
@Immutable
public class TeardropBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [TeardropBottomBar]. See [TeardropBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class TeardropBottomBarColors(
    public val containerColor: Color,
    public val indicatorColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
)

public object TeardropBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 64.dp

    /** Width and height of the teardrop behind the selected icon. */
    public val IndicatorSize: Dp = 46.dp

    /** Radius of the teardrop's pointed bottom-end corner. */
    public val TipRadius: Dp = 3.dp

    /** Space between the bar's ends and the outermost tab slots. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 12.dp)

    /** Size of the outlined icons of the unselected tabs. */
    public val IconSize: Dp = 26.dp

    /** Size of the filled icon inside the teardrop. */
    public val SelectedIconSize: Dp = 22.dp

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): TeardropBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        indicatorColor: Color = Color(0xFF5B3CE0),
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
    ): TeardropBottomBarColors = TeardropBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        indicatorColor: Color = Color(0xFF5B3CE0),
        selectedContentColor: Color = Color(0xFFFFFFFF),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
    ): TeardropBottomBarColors = TeardropBottomBarColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )
}

/**
 * A flat bar of icon-only tabs. The selected icon fills in and sits in an accent teardrop: a circle
 * whose bottom-end corner comes to a point, like a chat bubble's tail.
 *
 * When the selection changes the old teardrop shrinks back into its tip and the new one grows out of
 * its tip with a springy twist, the way a chat bubble pops in. Every tab can be selected and every slot
 * gets the same width, so 3 to 5 items look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar.
 */
@Composable
public fun TeardropBottomBar(
    items: List<TeardropBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: TeardropBottomBarColors = TeardropBottomBarDefaults.colors(),
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = TeardropBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "TeardropBottomBar needs at least one item" }

    Row(
        modifier = modifier
            .shadow(shadowElevation)
            .background(colors.containerColor)
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(TeardropBottomBarDefaults.BarHeight)
            .padding(TeardropBottomBarDefaults.ContentPadding)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            TeardropTab(
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
private fun TeardropTab(
    item: TeardropBarItem,
    selected: Boolean,
    colors: TeardropBottomBarColors,
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
    // 0 = no teardrop, 1 = full teardrop. Grows with an overshoot but shrinks on a plain tween, so it
    // never dips below zero and flips inside out.
    val grow by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = if (selected) GrowSpring else tween(ShrinkDurationMillis),
        label = "tabTeardrop",
    )
    // The teardrop grows out of, and shrinks back into, its pointed corner.
    val tipOrigin = if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
        TransformOrigin(1f, 1f)
    } else {
        TransformOrigin(0f, 1f)
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
            .semantics { contentDescription = item.label }
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(TeardropBottomBarDefaults.IndicatorSize)
                .graphicsLayer {
                    val scale = grow.coerceAtLeast(0f)
                    scaleX = scale
                    scaleY = scale
                    // Swings round into place as it grows, the way a chat bubble pops in.
                    rotationZ = TwistDegrees * (1f - grow)
                    transformOrigin = tipOrigin
                }
                .background(colors.indicatorColor, TeardropShape),
        )
        Crossfade(
            targetState = selected,
            animationSpec = tween(IconFadeDurationMillis),
            label = "tabIcon",
        ) { isSelected ->
            if (isSelected) {
                Image(
                    imageVector = item.selectedIcon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colors.selectedContentColor),
                    modifier = Modifier
                        .size(TeardropBottomBarDefaults.SelectedIconSize)
                        .graphicsLayer {
                            // Pops in a beat behind the teardrop.
                            val scale = 0.6f + 0.4f * grow
                            scaleX = scale
                            scaleY = scale
                        },
                )
            } else {
                Image(
                    imageVector = item.icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colors.unselectedContentColor),
                    modifier = Modifier.size(TeardropBottomBarDefaults.IconSize),
                )
            }
        }
    }
}

/** A circle whose bottom-end corner is pulled out into a soft point. */
private val TeardropShape = RoundedCornerShape(
    topStart = TeardropBottomBarDefaults.IndicatorSize / 2,
    topEnd = TeardropBottomBarDefaults.IndicatorSize / 2,
    bottomEnd = TeardropBottomBarDefaults.TipRadius,
    bottomStart = TeardropBottomBarDefaults.IndicatorSize / 2,
)

private val GrowSpring = spring<Float>(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow)

/** How far the teardrop is turned when it starts growing. */
private const val TwistDegrees = -35f

private const val ShrinkDurationMillis = 180
private const val IconFadeDurationMillis = 220
