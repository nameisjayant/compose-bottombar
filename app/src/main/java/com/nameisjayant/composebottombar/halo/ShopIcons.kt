package com.nameisjayant.composebottombar.halo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Outlined / filled icon pairs for the Halo demo. Drawn in black on a 24x24 grid; the bar tints them.
 * Holes in the filled variants use even-odd fill so the bar color shows through.
 */
object ShopIcons {
    val Home: ImageVector by lazy {
        icon("Home") {
            stroke(HOUSE)
            stroke("M12 15.5v2")
        }
    }
    val HomeFilled: ImageVector by lazy {
        icon("HomeFilled") { fill("$HOUSE M11.2 15.3h1.6v2.9h-1.6z") }
    }

    val Search: ImageVector by lazy {
        icon("Search") {
            stroke(circle(cx = 11f, cy = 11f, r = 7.5f))
            stroke("M17 17l2.6 2.6")
        }
    }
    val SearchFilled: ImageVector by lazy {
        icon("SearchFilled") {
            fill(circle(cx = 11f, cy = 11f, r = 8f))
            stroke("M17.2 17.2l2.6 2.6", width = 1.8f)
        }
    }

    val Store: ImageVector by lazy {
        icon("Store") {
            stroke(AWNING)
            stroke("M9.2 3.8l-.7 5.8 M14.8 3.8l.7 5.8")
            stroke("M5 11.4V18a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-6.6")
            stroke("M10 20v-2.5a2 2 0 0 1 4 0V20")
        }
    }
    val StoreFilled: ImageVector by lazy {
        icon("StoreFilled") {
            fill(AWNING)
            fill("M5 12.2a3.6 3.6 0 0 0 3.5-.6a3.6 3.6 0 0 0 3.5.8a3.6 3.6 0 0 0 3.5-.8a3.6 3.6 0 0 0 3.5.6V18a2 2 0 0 1-2 2h-3v-2.5a2 2 0 0 0-4 0V20H7a2 2 0 0 1-2-2z")
        }
    }

    val Cart: ImageVector by lazy {
        icon("Cart") {
            stroke("M8.5 8.5L11 4 M15.5 8.5L13 4")
            stroke(BASKET)
            stroke("M10 13v3.5 M14 13v3.5")
        }
    }
    val CartFilled: ImageVector by lazy {
        icon("CartFilled") {
            stroke("M8.5 8.5L11 4 M15.5 8.5L13 4", width = 1.8f)
            fill("$BASKET M9.2 12.8h1.6v4h-1.6z M13.2 12.8h1.6v4h-1.6z")
        }
    }

    val Profile: ImageVector by lazy {
        icon("Profile") {
            stroke(circle(cx = 12f, cy = 7.8f, r = 3.8f))
            stroke("M4.8 20.5a7.2 7.2 0 0 1 14.4 0")
        }
    }
    val ProfileFilled: ImageVector by lazy {
        icon("ProfileFilled") {
            fill(circle(cx = 12f, cy = 7.6f, r = 4.2f))
            fill("M4.5 20.2a7.5 7.5 0 0 1 15 0a.9.9 0 0 1-.9.9H5.4a.9.9 0 0 1-.9-.9z")
        }
    }
}

private const val HOUSE =
    "M10.6 4.2a2.2 2.2 0 0 1 2.8 0l5.8 4.8a2.2 2.2 0 0 1 .8 1.7V18a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-7.3a2.2 2.2 0 0 1 .8-1.7z"

private const val AWNING =
    "M6.2 3.8h11.6a1.5 1.5 0 0 1 1.4 1l1.3 3.6a2.6 2.6 0 0 1-4.6 2.2a2.7 2.7 0 0 1-3.9.4a2.7 2.7 0 0 1-3.9-.4a2.6 2.6 0 0 1-4.6-2.2l1.3-3.6a1.5 1.5 0 0 1 1.4-1z"

private const val BASKET =
    "M3.5 8.5h17l-1.7 9.6a2.2 2.2 0 0 1-2.2 1.9H7.4a2.2 2.2 0 0 1-2.2-1.9z"

private fun circle(cx: Float, cy: Float, r: Float): String =
    "M${cx - r} ${cy}a$r $r 0 1 0 ${2 * r} 0a$r $r 0 1 0 ${-2 * r} 0z"

private inline fun icon(name: String, block: ImageVector.Builder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply(block).build()

private fun ImageVector.Builder.stroke(pathData: String, width: Float = 1.6f) {
    addPath(
        pathData = addPathNodes(pathData),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = width,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
}

private fun ImageVector.Builder.fill(pathData: String) {
    addPath(
        pathData = addPathNodes(pathData),
        fill = SolidColor(Color.Black),
        pathFillType = PathFillType.EvenOdd,
    )
}
