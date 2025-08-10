package com.bcan.switchfi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StrengthBar(level: Int, modifier: Modifier = Modifier) {
    val clamped = level.coerceIn(0, 100)
    val color = when {
        clamped >= 70 -> Color(0xFF2E7D32)
        clamped >= 40 -> Color(0xFFF9A825)
        else -> Color(0xFFC62828)
    }
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(Color.LightGray.copy(alpha = 0.4f))
            .height(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped / 100f)
                .height(8.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color)
        )
    }
}


