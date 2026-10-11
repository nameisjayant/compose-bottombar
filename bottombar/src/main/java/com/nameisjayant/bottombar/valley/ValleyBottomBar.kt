package com.nameisjayant.bottombar.valley

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
import androidx.compose.foundation.layout.PaddingValues
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

/**
 * A destination shown in a [ValleyBottomBar].
 *
 * @param label text under the icon; for the center orb it is only used for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class ValleyBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [ValleyBottomBar]. See [ValleyBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class ValleyBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
    public val orbColor: Color,
    public val orbContentColor: Color,
)

public object ValleyBottomBarDefaults {
    /** Height of the bar body at its outer edges, excluding the orb overhang and the navigation bar inset. */
    public val BarHeight: Dp = 94.dp

    /** How far the top edge sags at the center, measured from its outer corners. */
    public val ValleyDepth: Dp = 22.dp

    /** Diameter of the center orb. */
    public val OrbSize: Dp = 60.dp

    /** How far the orb rises above the outer corners of the bar body. */
    public val OrbOverhang: Dp = 12.dp

    /** Elevation of the colored glow under the orb. */
    public val OrbElevation: Dp = 12.dp

    /** Space between the bar's sides and the outermost tabs. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 10.dp)

    public val IconSize: Dp = 24.dp
    public val OrbIconSize: Dp = 24.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): ValleyBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
        orbColor: Color = Color(0xFF5B3CE0),
        orbContentColor: Color = Color(0xFFFFFFFF),
    ): ValleyBottomBarColors = ValleyBottomBarColors(
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
    ): ValleyBottomBarColors = ValleyBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        orbColor = orbColor,
        orbContentColor = orbContentColor,
    )
}

/**
 * A flat-bottomed bar whose whole top edge sags in one smooth curve, with a glowing orb resting in the
 * low point.
 *
 * The middle entry of [items] becomes the orb and the rest are split evenly on either side, so [items]
 * must hold an odd number of entries (3, 5, 7...). Side tabs always show their icon and label; the
 * selected one switches to its filled icon and takes the selected color. The orb is a regular
 * destination too: when it becomes selected it presses down into the valley, which sinks with it and
 * springs back.
 *
 * @param items destinations in display order; the middle one is drawn as the orb.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The sagging edge makes the outline
 * concave, which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun ValleyBottomBar(
    items: List<ValleyBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: ValleyBottomBarColors = ValleyBottomBarDefaults.colors(),
    labelStyle: TextStyle = ValleyBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = ValleyBottomBarDefaults.windowInsets,
) {
    require(items.size >= 3 && items.size % 2 == 1) {
        "ValleyBottomBar needs an odd number of items (3, 5, 7...), got ${items.size}"
    }
    val orbIndex = items.size / 2
    val orbSelected = orbIndex == selectedIndex
    val shape = remember { ValleyShape() }

    // 0 -> 1 -> 0 extra sag of the valley while the orb presses into it on selection.
    val press = remember { Animatable(0f) }
    var wasOrbSelected by remember { mutableStateOf(orbSelected) }
    LaunchedEffect(orbSelected) {
        if (orbSelected && !wasOrbSelected) {
            press.animateTo(1f, tween(PressDownMillis, easing = FastOutSlowInEasing))
            press.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            )
        } else if (!orbSelected && press.value != 0f) {
            // Deselected mid-press: the animation above was cancelled, so settle back.
            press.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
        }
        wasOrbSelected = orbSelected
    }

    Box(
        modifier
            .fillMaxWidth()
            .shadow(shadowElevation, shape, clip = false)
            .drawBehind {
                // Drawn by hand rather than with background(shape) so the sag can follow the press
                // animation without recomposing the bar every frame.
                val extra = ValleyPressDepth.toPx() * press.value
                drawPath(valleyPath(size, this, extra), colors.containerColor)
            },
    ) {
        Row(
            modifier = Modifier
                .padding(top = ValleyBottomBarDefaults.OrbOverhang)
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .height(ValleyBottomBarDefaults.BarHeight)
                .padding(ValleyBottomBarDefaults.ContentPadding)
                .selectableGroup(),
        ) {
            items.forEachIndexed { index, item ->
                if (index == orbIndex) {
                    // Keeps the slot under the orb so the side tabs stay evenly spaced.
                    Spacer(Modifier.weight(1f))
                } else {
                    ValleyTab(
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
        ValleyOrb(
            item = items[orbIndex],
            selected = orbSelected,
            colors = colors,
            onClick = { onItemClick(orbIndex) },
            pressOffset = { ValleyPressDepth.toPx() * press.value },
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun ValleyTab(
    item: ValleyBarItem,
    selected: Boolean,
    colors: ValleyBottomBarColors,
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
            )
            .padding(top = TabTopPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.7f)) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.7f))
            },
            modifier = Modifier
                .size(ValleyBottomBarDefaults.IconSize)
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
                modifier = Modifier.size(ValleyBottomBarDefaults.IconSize),
            )
        }
        Spacer(Modifier.height(IconLabelGap))
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
private fun ValleyOrb(
    item: ValleyBarItem,
    selected: Boolean,
    colors: ValleyBottomBarColors,
    onClick: () -> Unit,
    pressOffset: Density.() -> Float,
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

    Box(
        modifier = modifier
            .size(ValleyBottomBarDefaults.OrbSize)
            .graphicsLayer {
                val scale = bounce.value * pressScale
                scaleX = scale
                scaleY = scale
                translationY = pressOffset()
            }
            .shadow(
                elevation = ValleyBottomBarDefaults.OrbElevation,
                shape = CircleShape,
                ambientColor = colors.orbColor,
                spotColor = colors.orbColor,
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
                modifier = Modifier.size(ValleyBottomBarDefaults.OrbIconSize),
            )
        }
    }
}

/** Resting bar outline, used for the shadow; the fill itself is drawn from [valleyPath]. */
private class ValleyShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(valleyPath(size, density, extraDepth = 0f))
}

/**
 * Bar outline: a rectangle starting [ValleyBottomBarDefaults.OrbOverhang] below the top, whose top edge
 * is one quadratic curve from corner to corner that sags [ValleyBottomBarDefaults.ValleyDepth] (plus
 * [extraDepth] px) at the center.
 */
private fun valleyPath(size: Size, density: Density, extraDepth: Float): Path = with(density) {
    val top = ValleyBottomBarDefaults.OrbOverhang.toPx()
    val depth = ValleyBottomBarDefaults.ValleyDepth.toPx() + extraDepth
    Path().apply {
        moveTo(0f, top)
        // A quadratic's midpoint sits halfway to its control point, so the control goes twice as deep.
        quadraticTo(size.width / 2f, top + 2 * depth, size.width, top)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
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

/** Space from the bar's outer top corners down to the tab icons. */
private val TabTopPadding = 29.dp
private val IconLabelGap = 7.dp

/** How much deeper the valley sags while the orb presses into it. */
private val ValleyPressDepth = 8.dp

private const val TintDurationMillis = 250
private const val PressDownMillis = 140
