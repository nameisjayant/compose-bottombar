package com.nameisjayant.bottombar.buoy

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin

/**
 * A destination shown in a [BuoyBottomBar].
 *
 * @param label text shown under the icon while the item is selected, and read out for accessibility.
 * @param icon icon shown while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class BuoyBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [BuoyBottomBar]. See [BuoyBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class BuoyBottomBarColors(
    public val containerColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val buoyColor: Color = selectedContentColor,
)

public object BuoyBottomBarDefaults {
    /** Height of the bar, excluding the navigation bar inset. */
    public val BarHeight: Dp = 64.dp

    /** Diameter of the buoy; only its top half shows above the bar's bottom edge while it rests. */
    public val BuoySize: Dp = 10.dp

    /** How far below its resting spot the buoy dives while it travels to another tab. */
    public val DiveDepth: Dp = 8.dp

    /** Space between the bar's rounded ends and the outermost tab slots. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 24.dp)

    public val IconSize: Dp = 24.dp

    public val LabelStyle: TextStyle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): BuoyBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF4A6CE8),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        buoyColor: Color = selectedContentColor,
    ): BuoyBottomBarColors = BuoyBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        buoyColor = buoyColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF1F1F1F),
        selectedContentColor: Color = Color(0xFF4A6CF0),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        buoyColor: Color = selectedContentColor,
    ): BuoyBottomBarColors = BuoyBottomBarColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        buoyColor = buoyColor,
    )
}

/**
 * A floating pill of icon-only tabs. The selected icon fills in and lifts over its label, and a small
 * buoy floats half-sunk in the bar's bottom edge under it.
 *
 * When the selection changes the buoy dives below the edge, travels across under the bar and bobs back
 * up beneath the new tab. Every tab can be selected and every slot gets the same width, so 3 to 5 items
 * with short labels look best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar.
 */
@Composable
public fun BuoyBottomBar(
    items: List<BuoyBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: BuoyBottomBarColors = BuoyBottomBarDefaults.colors(),
    labelStyle: TextStyle = BuoyBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = BuoyBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "BuoyBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current
    val padding = BuoyBottomBarDefaults.ContentPadding
    val shape = RoundedCornerShape(percent = 50)

    // Fractional tab index the buoy sits under.
    val position = remember { Animatable(selectedIndex.toFloat()) }
    // Where the current trip started, so the dive can bottom out halfway there. Only read while drawing.
    var tripStart by remember { mutableFloatStateOf(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        tripStart = position.value
        position.animateTo(
            targetValue = selectedIndex.toFloat(),
            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        )
    }

    Row(
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(BuoyBottomBarDefaults.BarHeight)
            .shadow(shadowElevation, shape)
            .clip(shape)
            .background(colors.containerColor)
            .drawWithContent {
                drawContent()
                val start = padding.calculateStartPadding(layoutDirection).toPx()
                val slot = (size.width - start - padding.calculateEndPadding(layoutDirection).toPx()) / items.size
                val fromStart = start + slot * (position.value + 0.5f)
                val cx = if (layoutDirection == LayoutDirection.Ltr) fromStart else size.width - fromStart
                val distance = position.targetValue - tripStart
                val progress = if (distance == 0f) 1f else ((position.value - tripStart) / distance).coerceIn(0f, 1f)
                val dive = sin(PI.toFloat() * progress)
                drawCircle(
                    color = colors.buoyColor,
                    radius = BuoyBottomBarDefaults.BuoySize.toPx() / 2f,
                    // Rests centered on the bottom edge, so the bar's clip shows only its top half.
                    center = Offset(cx, size.height + BuoyBottomBarDefaults.DiveDepth.toPx() * dive),
                )
            }
            .padding(padding)
            .selectableGroup(),
    ) {
        items.forEachIndexed { index, item ->
            BuoyTab(
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
private fun BuoyTab(
    item: BuoyBarItem,
    selected: Boolean,
    colors: BuoyBottomBarColors,
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
                modifier = Modifier.size(BuoyBottomBarDefaults.IconSize),
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

/** How far the selected icon rises above the tab's center. */
private val IconLift = 9.dp

/** Distance from the tab's center to the label's center while it shows. */
private val LabelDrop = 13.dp

/** Extra distance the label rises from as it fades in. */
private val LabelRise = 6.dp

private const val TintDurationMillis = 250
