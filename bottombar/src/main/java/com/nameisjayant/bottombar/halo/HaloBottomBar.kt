package com.nameisjayant.bottombar.halo

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A destination shown in a [HaloBottomBar].
 *
 * @param label text under the icon; for the center halo item it is only used for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class HaloBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [HaloBottomBar]. See [HaloBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class HaloBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
    public val haloColor: Color,
    public val haloRingColor: Color,
    public val haloContentColor: Color,
)

public object HaloBottomBarDefaults {
    /** Height of the flat bar, excluding the halo overhang and the navigation bar inset. */
    public val BarHeight: Dp = 64.dp

    /** Outer diameter of the center halo, ring included. */
    public val HaloSize: Dp = 72.dp

    /** Thickness of the light ring around the halo. */
    public val HaloRingWidth: Dp = 5.dp

    /** How far the halo rises above the top edge of the bar. */
    public val HaloOverhang: Dp = 22.dp

    public val IconSize: Dp = 24.dp
    public val HaloIconSize: Dp = 28.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): HaloBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
        haloColor: Color = Color(0xFF5B3CE0),
        haloRingColor: Color = Color(0xFFB2C1F7),
        haloContentColor: Color = Color(0xFFFFFFFF),
    ): HaloBottomBarColors = HaloBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        haloColor = haloColor,
        haloRingColor = haloRingColor,
        haloContentColor = haloContentColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF262626),
        selectedContentColor: Color = Color(0xFF4A6CF0),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        unselectedLabelColor: Color = Color(0xFFE4E6EB),
        haloColor: Color = Color(0xFF5B3CE0),
        haloRingColor: Color = Color(0xFFB2C1F7),
        haloContentColor: Color = Color(0xFFFFFFFF),
    ): HaloBottomBarColors = HaloBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        haloColor = haloColor,
        haloRingColor = haloRingColor,
        haloContentColor = haloContentColor,
    )
}

/**
 * A flat bottom bar with a raised, ringed "halo" button in the middle.
 *
 * The middle entry of [items] becomes the halo and the rest are split evenly on either side, so
 * [items] must hold an odd number of entries (3, 5, 7...). The halo is a regular destination: it is
 * selected like any other tab and swaps to its [HaloBarItem.selectedIcon] when active.
 *
 * Switching tabs animates the icon swap, a spring bounce on the newly selected icon, the tint and
 * label colors, and a halo burst that ripples out of the center button when it gets selected.
 *
 * @param items destinations in display order; the middle one is drawn as the halo.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 */
@Composable
public fun HaloBottomBar(
    items: List<HaloBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: HaloBottomBarColors = HaloBottomBarDefaults.colors(),
    labelStyle: TextStyle = HaloBottomBarDefaults.LabelStyle,
    windowInsets: WindowInsets = HaloBottomBarDefaults.windowInsets,
) {
    require(items.size >= 3 && items.size % 2 == 1) {
        "HaloBottomBar needs an odd number of items (3, 5, 7...), got ${items.size}"
    }
    val haloIndex = items.size / 2

    Box(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(top = HaloBottomBarDefaults.HaloOverhang)
                .fillMaxWidth()
                .background(colors.containerColor)
                .windowInsetsPadding(windowInsets)
                .height(HaloBottomBarDefaults.BarHeight)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEachIndexed { index, item ->
                if (index == haloIndex) {
                    // Keeps the slot under the halo so the side tabs stay evenly spaced.
                    Spacer(Modifier.weight(1f))
                } else {
                    HaloTab(
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
        HaloButton(
            item = items[haloIndex],
            selected = haloIndex == selectedIndex,
            colors = colors,
            onClick = { onItemClick(haloIndex) },
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun HaloTab(
    item: HaloBarItem,
    selected: Boolean,
    colors: HaloBottomBarColors,
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
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.7f)) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.7f))
            },
            modifier = Modifier
                .size(HaloBottomBarDefaults.IconSize)
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
                modifier = Modifier.size(HaloBottomBarDefaults.IconSize),
            )
        }
        Spacer(Modifier.height(4.dp))
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
private fun HaloButton(
    item: HaloBarItem,
    selected: Boolean,
    colors: HaloBottomBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "haloPress",
    )
    val bounce = rememberSelectionBounce(selected, from = 0.85f)

    // 0 -> 1 progress of the burst ring that ripples outwards when the halo becomes selected.
    val burst = remember { Animatable(1f) }
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        if (selected && !wasSelected) {
            burst.snapTo(0f)
            burst.animateTo(1f, tween(BurstDurationMillis, easing = FastOutSlowInEasing))
        } else if (!selected) {
            // Deselected before the animation finished: it was cancelled mid-way, so clear it.
            burst.snapTo(1f)
        }
        wasSelected = selected
    }

    Box(
        modifier = modifier
            .size(HaloBottomBarDefaults.HaloSize)
            .graphicsLayer {
                val scale = bounce.value * pressScale
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                val radius = size.minDimension / 2f
                val ringWidth = HaloBottomBarDefaults.HaloRingWidth.toPx()
                val progress = burst.value
                if (progress < 1f) {
                    drawCircle(
                        color = colors.haloRingColor.copy(alpha = 0.6f * (1f - progress)),
                        radius = radius + radius * 0.45f * progress,
                        style = Stroke(width = ringWidth * (1f - progress * 0.5f)),
                    )
                }
                drawCircle(color = colors.haloRingColor, radius = radius)
                drawCircle(color = colors.haloColor, radius = radius - ringWidth)
            }
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
            label = "haloIcon",
        ) { isSelected ->
            Image(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.haloContentColor),
                modifier = Modifier.size(HaloBottomBarDefaults.HaloIconSize),
            )
        }
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

private const val TintDurationMillis = 250
private const val BurstDurationMillis = 520
