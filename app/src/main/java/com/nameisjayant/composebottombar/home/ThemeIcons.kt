package com.nameisjayant.composebottombar.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Sun / moon for the theme toggle. Drawn in black on a 24x24 grid; Icon tints them. */
object ThemeIcons {
    val Sun: ImageVector by lazy {
        icon(
            "Sun",
            "M12 7.5a4.5 4.5 0 1 0 0 9a4.5 4.5 0 1 0 0-9z " +
                "M12 2.5v1.8 M12 19.7v1.8 M2.5 12h1.8 M19.7 12h1.8 " +
                "M5.3 5.3l1.3 1.3 M17.4 17.4l1.3 1.3 M5.3 18.7l1.3-1.3 M17.4 6.6l1.3-1.3",
        )
    }
    val Moon: ImageVector by lazy {
        icon("Moon", "M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z")
    }
}

private fun icon(name: String, pathData: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(
        pathData = addPathNodes(pathData),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.7f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ).build()
