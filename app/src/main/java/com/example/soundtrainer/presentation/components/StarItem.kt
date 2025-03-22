package com.example.soundtrainer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@Composable
fun StarItem(
    position: Offset,
    density: Density,
    isCollected: Boolean,
    onCollect: () -> Unit
) {
    val xDp = with(density) { position.x.toDp() }
    val yDp = with(density) { position.y.toDp() }

    Box(
        modifier = Modifier
            .offset(x = xDp, y = yDp)
            .size(120.dp)
    ) {

        if (!isCollected) {
            // основная звезда
            AnimatedStar(
                isCollected = false,
                onCollect = onCollect
            )
        } else {
            // звездочки появляются после того, как собрали основную звезду
            AnimatedStar(
                isCollected = true,
                onCollect = {}
            )
        }
    }
}