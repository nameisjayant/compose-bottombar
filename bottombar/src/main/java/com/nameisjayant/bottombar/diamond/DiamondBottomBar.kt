package com.nameisjayant.bottombar.diamond

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import kotlin.math.sqrt

/**
 * A destination shown in a [DiamondBottomBar].
 *
 * @param label text under the icon; for the center diamond it is only used for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class DiamondBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [DiamondBottomBar]. See [DiamondBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class DiamondBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
    public val diamondColor: Color,
    public val diamondContentColor: Color,
    public val diamondGlowColor: Color,
)

public object DiamondBottomBarDefaults {
    /** Height of the flat bar body, excluding the diamond overhang and the navigation bar inset. */
    public val BarHeight: Dp = 72.dp

    /** Side length of the center diamond, measured before it is turned on its point. */
    public val DiamondSize: Dp = 48.dp

    /** Corner radius of the center diamond. */
    public val DiamondCornerRadius: Dp = 10.dp

    public val IconSize: Dp = 24.dp
    public val DiamondIconSize: Dp = 26.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): DiamondBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF5B3CE0),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
        diamondColor: Color = Color(0xFF5B3CE0),
        diamondContentColor: Color = Color(0xFFFFFFFF),
        diamondGlowColor: Color = Color(0xFF5B3CE0),
    ): DiamondBottomBarColors = DiamondBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        diamondColor = diamondColor,
        diamondContentColor = diamondContentColor,
        diamondGlowColor = diamondGlowColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        selectedContentColor: Color = Color(0xFF6A4CF2),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        unselectedLabelColor: Color = Color(0xFFE4E8F0),
        diamondColor: Color = Color(0xFF5B3CE0),
        diamondContentColor: Color = Color(0xFFFFFFFF),
        diamondGlowColor: Color = Color(0xFF6A4CF2),
    ): DiamondBottomBarColors = DiamondBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
        diamondColor = diamondColor,
        diamondContentColor = diamondContentColor,
        diamondGlowColor = diamondGlowColor,
    )
}

/**
 * A flat bar with a glowing diamond (a rounded square turned on its point) in the middle, centered on
 * the bar's top edge.
 *
 * The middle entry of [items] becomes the diamond and the rest are split evenly on either side, so
 * [items] must hold an odd number of entries (3, 5, 7...). Side tabs always show their icon and label;
 * the selected one switches to its filled icon with a bounce and its label takes the accent color. The
 * diamond is a regular destination too: when selected it spins a quarter turn into place and swaps to
 * its [DiamondBarItem.selectedIcon].
 *
 * @param items destinations in display order; the middle one is drawn as the diamond.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 */
@Composable
public fun DiamondBottomBar(
    items: List<DiamondBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: DiamondBottomBarColors = DiamondBottomBarDefaults.colors(),
    labelStyle: TextStyle = DiamondBottomBarDefaults.LabelStyle,
    windowInsets: WindowInsets = DiamondBottomBarDefaults.windowInsets,
) {
    require(items.size >= 3 && items.size % 2 == 1) {
        "DiamondBottomBar needs an odd number of items (3, 5, 7...), got ${items.size}"
    }
    val diamondIndex = items.size / 2

    Box(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(top = DiamondOverhang)
                .fillMaxWidth()
                .background(colors.containerColor)
                .windowInsetsPadding(windowInsets)
                .height(DiamondBottomBarDefaults.BarHeight)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEachIndexed { index, item ->
                if (index == diamondIndex) {
                    // Keeps the slot under the diamond so the side tabs stay evenly spaced.
                    Spacer(Modifier.weight(1f))
                } else {
                    DiamondTab(
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
        Diamond(
            item = items[diamondIndex],
            selected = diamondIndex == selectedIndex,
            colors = colors,
            onClick = { onItemClick(diamondIndex) },
            // The diamond's bounding box is centered on the bar's top edge.
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = DiamondOverhang - DiamondBounds / 2),
        )
    }
}

@Composable
private fun DiamondTab(
    item: DiamondBarItem,
    selected: Boolean,
    colors: DiamondBottomBarColors,
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
                .size(DiamondBottomBarDefaults.IconSize)
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
                modifier = Modifier.size(DiamondBottomBarDefaults.IconSize),
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
private fun Diamond(
    item: DiamondBarItem,
    selected: Boolean,
    colors: DiamondBottomBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "diamondPress",
    )
    val bounce = rememberSelectionBounce(selected, from = 0.82f)

    // Extra turn on top of the 45° that stands the square on its point. A quarter turn lands on the
    // same silhouette, so the diamond spins into place when it becomes selected.
    val spin = remember { Animatable(0f) }
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        if (selected && !wasSelected) {
            spin.snapTo(-90f)
            spin.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
        } else if (!selected) {
            // Deselected mid-spin: the spin above was cancelled, so settle back instead of staying tilted.
            spin.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
        }
        wasSelected = selected
    }
    val shape = remember { RoundedCornerShape(DiamondBottomBarDefaults.DiamondCornerRadius) }

    Box(
        modifier = modifier
            .size(DiamondBounds)
            .semantics { contentDescription = item.label }
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .graphicsLayer {
                val scale = bounce.value * pressScale
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center,
    ) {
        // Turned separately from the icon so the glyph stays upright.
        Box(
            Modifier
                .size(DiamondBottomBarDefaults.DiamondSize)
                .graphicsLayer { rotationZ = 45f + spin.value }
                .shadow(
                    elevation = 14.dp,
                    shape = shape,
                    ambientColor = colors.diamondGlowColor,
                    spotColor = colors.diamondGlowColor,
                )
                .background(colors.diamondColor, shape),
        )
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                (fadeIn(tween(240)) + scaleIn(tween(240), initialScale = 0.5f)) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 1.3f))
            },
            label = "diamondIcon",
        ) { isSelected ->
            Image(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.diamondContentColor),
                modifier = Modifier.size(DiamondBottomBarDefaults.DiamondIconSize),
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

/** Width and height of the diamond once turned on its point: the diagonal of the square. */
private val DiamondBounds: Dp = DiamondBottomBarDefaults.DiamondSize * sqrt(2f)

/**
 * How far the diamond's tip rises above the bar. The rounded corners pull the tip in from the square's
 * sharp corner by `r * (√2 - 1)`.
 */
private val DiamondOverhang: Dp =
    DiamondBounds / 2 - DiamondBottomBarDefaults.DiamondCornerRadius * (sqrt(2f) - 1f)

/** Space from the bar's top edge down to the tab icons. */
private val TabTopPadding = 15.dp
private val IconLabelGap = 2.dp

private const val TintDurationMillis = 250
