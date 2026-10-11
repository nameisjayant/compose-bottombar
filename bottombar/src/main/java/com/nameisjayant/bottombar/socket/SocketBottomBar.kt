package com.nameisjayant.bottombar.socket

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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.absoluteOffset
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * A destination shown in a [SocketBottomBar].
 *
 * @param label text under the icon, shown for every tab including the selected one.
 * @param icon icon shown in the bar while the item is not selected, usually an outlined glyph.
 * @param selectedIcon icon shown inside the bubble while the item is selected, usually the filled twin of [icon].
 */
@Immutable
public class SocketBarItem(
    public val label: String,
    public val icon: ImageVector,
    public val selectedIcon: ImageVector = icon,
)

/** Colors used by a [SocketBottomBar]. See [SocketBottomBarDefaults] for the light and dark palettes. */
@Immutable
public class SocketBottomBarColors(
    public val containerColor: Color,
    public val bubbleColor: Color,
    public val selectedContentColor: Color,
    public val unselectedContentColor: Color,
    public val unselectedLabelColor: Color,
)

public object SocketBottomBarDefaults {
    /** Height of the pill-shaped bar body, excluding the bubble overhang and the navigation bar inset. */
    public val BarHeight: Dp = 72.dp

    /** Diameter of the bubble that carries the selected icon. */
    public val BubbleSize: Dp = 52.dp

    /** How far the bubble rises above the top edge of the bar body. */
    public val BubbleOverhang: Dp = 23.dp

    /** Width of the see-through ring between the bubble and the socket cut into the bar. */
    public val SocketGap: Dp = 4.dp

    /** Radius of the rounded shoulders that blend the socket into the top edge. */
    public val SocketShoulderRadius: Dp = 23.dp

    /** Space between the bar's rounded ends and the outermost tabs. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 36.dp)

    public val IconSize: Dp = 26.dp
    public val BubbleIconSize: Dp = 26.dp

    public val LabelStyle: TextStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars

    /** Light or dark palette, following the system setting unless [darkTheme] is given. */
    @Composable
    public fun colors(darkTheme: Boolean = isSystemInDarkTheme()): SocketBottomBarColors =
        if (darkTheme) darkColors() else lightColors()

    public fun lightColors(
        containerColor: Color = Color(0xFFFFFFFF),
        bubbleColor: Color = containerColor,
        selectedContentColor: Color = Color(0xFF3F63EA),
        unselectedContentColor: Color = Color(0xFFA9B5D1),
        unselectedLabelColor: Color = Color(0xFFA9B5D1),
    ): SocketBottomBarColors = SocketBottomBarColors(
        containerColor = containerColor,
        bubbleColor = bubbleColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
    )

    public fun darkColors(
        containerColor: Color = Color(0xFF222222),
        bubbleColor: Color = containerColor,
        selectedContentColor: Color = Color(0xFF4A6CF0),
        unselectedContentColor: Color = Color(0xFFC3CCE0),
        unselectedLabelColor: Color = Color(0xFFE4E8F0),
    ): SocketBottomBarColors = SocketBottomBarColors(
        containerColor = containerColor,
        bubbleColor = bubbleColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        unselectedLabelColor = unselectedLabelColor,
    )
}

/**
 * A floating, pill-shaped bar where the selected icon lifts out of the bar into a bubble that sits in
 * a socket cut into the top edge, with a thin see-through ring between the two.
 *
 * Every tab shows its label; unselected tabs also show their icon. When the selection changes, the
 * bubble and its socket glide over to the new tab, the tab's icon rises into the bubble as its filled
 * twin, and the label takes the selected color. Any number of items works, though 3 to 5 fit best.
 *
 * @param items destinations in display order.
 * @param selectedIndex index into [items] of the selected destination.
 * @param onItemClick called with the index of the tapped item, including re-taps of the selected one.
 * @param shadowElevation elevation of the shadow under the bar. The socket makes the outline concave,
 * which Android can only cast a shadow for on API 30+; older versions draw the bar without one.
 */
@Composable
public fun SocketBottomBar(
    items: List<SocketBarItem>,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: SocketBottomBarColors = SocketBottomBarDefaults.colors(),
    labelStyle: TextStyle = SocketBottomBarDefaults.LabelStyle,
    shadowElevation: Dp = 0.dp,
    windowInsets: WindowInsets = SocketBottomBarDefaults.windowInsets,
) {
    require(items.isNotEmpty()) { "SocketBottomBar needs at least one item" }
    val layoutDirection = LocalLayoutDirection.current
    val padding = SocketBottomBarDefaults.ContentPadding
    val startPadding = padding.calculateStartPadding(layoutDirection)
    val endPadding = padding.calculateEndPadding(layoutDirection)

    // Fractional tab index the bubble and its socket sit over; springs from tab to tab.
    val position by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "socketPosition",
    )
    // Center of the bubble from the bar's left edge, for a bar [width] px wide.
    val bubbleCenterX: Density.(width: Float) -> Float = { width ->
        val start = startPadding.toPx()
        val slot = (width - start - endPadding.toPx()) / items.size
        val fromStart = start + slot * (position + 0.5f)
        if (layoutDirection == LayoutDirection.Ltr) fromStart else width - fromStart
    }
    var barWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier
            .windowInsetsPadding(windowInsets)
            .fillMaxWidth()
            .height(SocketBottomBarDefaults.BubbleOverhang + SocketBottomBarDefaults.BarHeight)
            .onSizeChanged { barWidth = it.width }
            .graphicsLayer {
                // Set here rather than with Modifier.shadow so the outline follows the socket as it moves.
                this.shadowElevation = shadowElevation.toPx()
                shape = SocketShape(bubbleCenterX(size.width))
            }
            .drawBehind {
                // Drawn by hand rather than with background(shape) so the socket can follow the bubble
                // without recomposing the bar every frame.
                drawPath(socketPath(size, this, bubbleCenterX(size.width)), colors.containerColor)
            },
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(SocketBottomBarDefaults.BarHeight)
                .padding(padding)
                .selectableGroup(),
        ) {
            items.forEachIndexed { index, item ->
                SocketTab(
                    item = item,
                    selected = index == selectedIndex,
                    colors = colors,
                    labelStyle = labelStyle,
                    onClick = { onItemClick(index) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        SocketBubble(
            items = items,
            selectedIndex = selectedIndex,
            colors = colors,
            onClick = { onItemClick(selectedIndex) },
            // bubbleCenterX already mirrors for RTL, so place the bubble in absolute coordinates.
            modifier = Modifier
                .align(AbsoluteAlignment.TopLeft)
                .absoluteOffset {
                    val half = SocketBottomBarDefaults.BubbleSize.toPx() / 2f
                    IntOffset((bubbleCenterX(barWidth.toFloat()) - half).roundToInt(), 0)
                },
        )
    }
}

@Composable
private fun SocketTab(
    item: SocketBarItem,
    selected: Boolean,
    colors: SocketBottomBarColors,
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
    // 1 while the icon sits in the bar, 0 once it has lifted into the bubble.
    val iconPresence by animateFloatAsState(
        targetValue = if (selected) 0f else 1f,
        animationSpec = if (selected) tween(IconLiftMillis) else spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tabIconPresence",
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
        Image(
            imageVector = item.icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(colors.unselectedContentColor),
            modifier = Modifier
                .size(SocketBottomBarDefaults.IconSize)
                .graphicsLayer {
                    val scale = (0.5f + 0.5f * iconPresence) * pressScale
                    scaleX = scale
                    scaleY = scale
                    alpha = iconPresence.coerceIn(0f, 1f)
                    translationY = -IconLiftDistance.toPx() * (1f - iconPresence)
                },
        )
        Spacer(Modifier.height(IconLabelGap))
        BasicText(
            text = item.label,
            style = labelStyle.copy(
                color = labelColor,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            ),
            maxLines = 1,
            modifier = Modifier.graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        )
    }
}

@Composable
private fun SocketBubble(
    items: List<SocketBarItem>,
    selectedIndex: Int,
    colors: SocketBottomBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "bubblePress",
    )
    val bounce = rememberSelectionBounce(selectedIndex)

    Box(
        modifier = modifier
            .size(SocketBottomBarDefaults.BubbleSize)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .background(colors.bubbleColor, CircleShape)
            // The selected tab underneath already carries the semantics; this is only a bigger target.
            .clearAndSetSemantics {}
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = {
                (fadeIn(tween(220, delayMillis = 60)) +
                    scaleIn(tween(260, delayMillis = 60), initialScale = 0.4f) +
                    slideInVertically(tween(260, delayMillis = 60)) { it / 2 }) togetherWith
                    (fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.4f))
            },
            modifier = Modifier.graphicsLayer {
                scaleX = bounce.value
                scaleY = bounce.value
            },
            label = "bubbleIcon",
        ) { index ->
            val item = items.getOrNull(index)
            if (item != null) {
                Image(
                    imageVector = item.selectedIcon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colors.selectedContentColor),
                    modifier = Modifier.size(SocketBottomBarDefaults.BubbleIconSize),
                )
            } else {
                Spacer(Modifier.size(SocketBottomBarDefaults.BubbleIconSize))
            }
        }
    }
}

/** Bar outline with the socket at [cx], used for the shadow; the fill itself is drawn from [socketPath]. */
private class SocketShape(private val cx: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(socketPath(size, density, cx))
}

/**
 * Bar outline: a pill starting [SocketBottomBarDefaults.BubbleOverhang] below the top, with a round
 * socket cut into its top edge around the bubble centered at [cx]. The socket is concentric with the
 * bubble and [SocketBottomBarDefaults.SocketGap] wider, and blends into the top edge through two
 * rounded shoulders.
 */
private fun socketPath(size: Size, density: Density, cx: Float): Path = with(density) {
    val top = SocketBottomBarDefaults.BubbleOverhang.toPx()
    val bubbleR = SocketBottomBarDefaults.BubbleSize.toPx() / 2f
    val socketR = bubbleR + SocketBottomBarDefaults.SocketGap.toPx()
    val shoulderR = SocketBottomBarDefaults.SocketShoulderRadius.toPx()
    val socketCy = bubbleR

    val pill = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(0f, top, size.width, size.height),
                cornerRadius = CornerRadius((size.height - top) / 2f),
            ),
        )
    }
    // Shoulder circles hang just under the top edge and touch the socket circle from outside.
    val dy = top + shoulderR - socketCy
    val dx = sqrt((socketR + shoulderR) * (socketR + shoulderR) - dy * dy)
    val tangent = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
    // Everything above the socket's edge; cutting it out of the pill leaves the socket behind.
    val cutter = Path().apply {
        moveTo(cx - dx, 0f)
        lineTo(cx - dx, top)
        arcTo(circle(cx - dx, top + shoulderR, shoulderR), 270f, 90f - tangent, false)
        arcTo(circle(cx, socketCy, socketR), 180f - tangent, 2 * tangent - 180f, false)
        arcTo(circle(cx + dx, top + shoulderR, shoulderR), 180f + tangent, 90f - tangent, false)
        lineTo(cx + dx, 0f)
        close()
    }
    Path().apply { op(pill, cutter, PathOperation.Difference) }
}

private fun circle(cx: Float, cy: Float, r: Float) = Rect(cx - r, cy - r, cx + r, cy + r)

/**
 * Scale that dips and springs back to 1 each time [key] changes. It stays at 1 on first composition so
 * the bubble doesn't bounce when the screen opens.
 */
@Composable
private fun rememberSelectionBounce(key: Int): Animatable<Float, *> {
    val scale = remember { Animatable(1f) }
    var previous by remember { mutableStateOf(key) }
    LaunchedEffect(key) {
        if (key != previous) {
            scale.snapTo(0.82f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        }
        previous = key
    }
    return scale
}

/** Space from the bar's top edge down to the tab icons. */
private val TabTopPadding = 17.dp
private val IconLabelGap = 2.dp

/** How far an icon rises while it lifts out of the bar into the bubble. */
private val IconLiftDistance = 14.dp

private const val TintDurationMillis = 250
private const val IconLiftMillis = 160
