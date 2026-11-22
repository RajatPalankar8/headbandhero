package com.proto.headbandhero.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HudBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(Color(0x661E1E24), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SmallHudButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(Color(0x772B2D42), RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFFF4F1DE),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun HoldThumbButton(
    label: String,
    modifier: Modifier,
    isAccent: Boolean = false,
    fontSize: TextUnit = 14.sp,
    onStateChange: (Boolean) -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val bg = when {
        pressed && isAccent -> Color(0xFFF3E5AB).copy(alpha = 0.85f)
        pressed -> Color(0xFFE07A5F).copy(alpha = 0.85f)
        isAccent -> Color(0xFFE07A5F).copy(alpha = 0.5f)
        else -> Color(0x221E1E24)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.5.dp, Color(0xFFF3E5AB).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    pressed = true; onStateChange(true)
                    do {
                        val event = awaitPointerEvent()
                    } while (event.changes.any { it.pressed })
                    pressed = false; onStateChange(false)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (pressed || isAccent) Color(0xFF1E1E24) else Color(0xFFF3E5AB),
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}
