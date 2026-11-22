package com.proto.headbandhero.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.proto.headbandhero.game.LOGICAL_H
import com.proto.headbandhero.game.LOGICAL_W
import com.proto.headbandhero.model.BirdSpec
import com.proto.headbandhero.model.CoinSpec
import com.proto.headbandhero.model.DoorSpec
import com.proto.headbandhero.model.EnemySpec
import kotlin.math.sin

fun DrawScope.drawCanvasGrid(levelWidth: Float = LOGICAL_W) {
    val gridColor = Color(0x0F2B2D42)
    var gx = 0f
    while (gx < levelWidth) {
        drawLine(gridColor, Offset(gx, 0f), Offset(gx, LOGICAL_H), strokeWidth = 1f)
        gx += 40f
    }
    var gy = 0f
    while (gy < LOGICAL_H) {
        drawLine(gridColor, Offset(0f, gy), Offset(levelWidth, gy), strokeWidth = 1f)
        gy += 40f
    }
}

fun DrawScope.drawDoor(door: DoorSpec) {
    drawRect(Color(0xFF2A9D8F), Offset(door.x, door.y), Size(door.width, door.height))
    drawRect(Color(0xFF2B2D42), Offset(door.x, door.y), Size(door.width, door.height), style = Stroke(3f))
    drawRect(Color(0xFF21867A), Offset(door.x + 6f, door.y + 6f), Size(door.width - 12f, door.height - 10f))
    drawCircle(Color(0xFFF3E5AB), 3f, Offset(door.x + door.width - 10f, door.y + door.height / 2f + 2f))
}

fun DrawScope.drawCoins(coins: List<CoinSpec>, animClock: Float) {
    val bob = sin(animClock) * 3f
    for (c in coins) {
        if (!c.collected) {
            val center = Offset(c.x, c.y + bob)
            drawCircle(Color(0xFFE9C46A), c.r, center)
            drawCircle(Color(0xFF2B2D42), c.r, center, style = Stroke(2.2f))
            drawCircle(Color(0xFFF4F1DE), 2.5f, Offset(c.x - 2.5f, c.y + bob - 2.5f))
        }
    }
}

fun DrawScope.drawEnemies(enemies: List<EnemySpec>, animClock: Float) {
    for ((i, e) in enemies.withIndex()) {
        if (!e.alive) {
            if (e.squishTimer > 0f) {
                val alpha = (e.squishTimer / 18f).coerceIn(0f, 1f)
                drawRoundRect(
                    Color(0xFF8D5B4C).copy(alpha = alpha),
                    Offset(e.x - 3f, e.y + e.height - 8f),
                    Size(e.width + 6f, 8f),
                    CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    Color(0xFF2B2D42).copy(alpha = alpha),
                    Offset(e.x - 3f, e.y + e.height - 8f),
                    Size(e.width + 6f, 8f),
                    CornerRadius(3f, 3f),
                    style = Stroke(2f)
                )
            }
            continue
        }

        // Critter body
        drawRoundRect(Color(0xFF8D5B4C), Offset(e.x, e.y), Size(e.width, e.height - 4f), CornerRadius(8f, 8f))
        drawRoundRect(Color(0xFF2B2D42), Offset(e.x, e.y), Size(e.width, e.height - 4f), CornerRadius(8f, 8f), style = Stroke(2.2f))

        // Animated feet
        val step = sin(animClock * 3f + i) * 2.5f
        drawRect(Color(0xFF2B2D42), Offset(e.x + 3f + step, e.y + e.height - 5f), Size(7f, 5f))
        drawRect(Color(0xFF2B2D42), Offset(e.x + e.width - 10f - step, e.y + e.height - 5f), Size(7f, 5f))

        // Angry eyes
        val dir = if (e.vx >= 0f) 1.5f else -1.5f
        drawRect(Color.White, Offset(e.x + 5f + dir, e.y + 7f), Size(6f, 6f))
        drawRect(Color.White, Offset(e.x + 17f + dir, e.y + 7f), Size(6f, 6f))
        drawRect(Color(0xFF2B2D42), Offset(e.x + 7f + dir, e.y + 9f), Size(3f, 3f))
        drawRect(Color(0xFF2B2D42), Offset(e.x + 19f + dir, e.y + 9f), Size(3f, 3f))

        // Angry eyebrows
        drawLine(Color(0xFF2B2D42), Offset(e.x + 4f + dir, e.y + 5f), Offset(e.x + 12f + dir, e.y + 8f), strokeWidth = 2f)
        drawLine(Color(0xFF2B2D42), Offset(e.x + 24f + dir, e.y + 5f), Offset(e.x + 16f + dir, e.y + 8f), strokeWidth = 2f)
    }
}

fun DrawScope.drawBirds(birds: List<BirdSpec>, animClock: Float) {
    for (b in birds) {
        if (!b.alive) {
            if (b.squishTimer > 0f) {
                val alpha = (b.squishTimer / 18f).coerceIn(0f, 1f)
                drawRoundRect(
                    Color(0xFF5E548E).copy(alpha = alpha),
                    Offset(b.x, b.y + 8f),
                    Size(b.width, 10f),
                    CornerRadius(4f, 4f)
                )
                drawRoundRect(
                    Color(0xFF2B2D42).copy(alpha = alpha),
                    Offset(b.x, b.y + 8f),
                    Size(b.width, 10f),
                    CornerRadius(4f, 4f),
                    style = Stroke(2f)
                )
            }
            continue
        }

        val facingRight = b.vx >= 0f
        val flap = sin(animClock * 5f + b.phase) * 7f
        val cx = b.x + b.width / 2f
        val cy = b.y + b.height / 2f

        withTransform({
            translate(cx, cy)
            if (!facingRight) scale(-1f, 1f, pivot = Offset.Zero)
        }) {
            // Flapping wing
            val wingPath = Path().apply {
                moveTo(-4f, -2f)
                quadraticTo(-10f, -14f + flap, 4f, -4f)
                close()
            }
            drawPath(wingPath, Color(0xFF9F86C0))
            drawPath(wingPath, Color(0xFF2B2D42), style = Stroke(2f))

            // Body oval
            drawOval(Color(0xFF5E548E), Offset(-13f, -8f), Size(26f, 18f))
            drawOval(Color(0xFF2B2D42), Offset(-13f, -8f), Size(26f, 18f), style = Stroke(2f))

            // Tail feathers
            val tailPath = Path().apply {
                moveTo(-11f, 0f)
                lineTo(-17f, -4f)
                lineTo(-15f, 3f)
                close()
            }
            drawPath(tailPath, Color(0xFF5E548E))
            drawPath(tailPath, Color(0xFF2B2D42), style = Stroke(2f))

            // Beak
            val beakPath = Path().apply {
                moveTo(11f, -2f)
                lineTo(18f, 1f)
                lineTo(11f, 4f)
                close()
            }
            drawPath(beakPath, Color(0xFFF4A261))
            drawPath(beakPath, Color(0xFF2B2D42), style = Stroke(2f))

            // Eye & brow
            drawCircle(Color.White, 3.2f, Offset(6f, -2f))
            drawCircle(Color(0xFF2B2D42), 1.6f, Offset(7f, -2f))
            drawLine(Color(0xFF2B2D42), Offset(3f, -6f), Offset(9f, -4f), strokeWidth = 1.8f)
        }
    }
}

fun DrawScope.drawPlayer(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    facing: Int,
    vx: Float,
    vy: Float,
    onGround: Boolean,
    runPhase: Float,
    scaleX: Float,
    scaleY: Float,
    doubleJumpReady: Boolean,
    bonked: Boolean,
    blinkTimer: Float,
    animClock: Float
) {
    val cx = x + w / 2f
    val bottomY = y + h
    val runBob = if (onGround && kotlin.math.abs(vx) > 0.5f) kotlin.math.abs(sin(runPhase)) * 2.5f else 0f
    val tilt = (vx / 3.3f) * 0.1f

    withTransform({
        translate(cx, bottomY - runBob)
        rotate(Math.toDegrees(tilt.toDouble()).toFloat(), pivot = Offset.Zero)
        scale(scaleX, scaleY, pivot = Offset.Zero)
    }) {
        // Feet stride
        val stride = if (onGround) sin(runPhase) * 5f else (if (vy < 0f) -3f else 3f)
        drawRoundRect(Color(0xFF2B2D42), Offset(-11f + stride, -6f), Size(9f, 6f), CornerRadius(2.5f, 2.5f))
        drawRoundRect(Color(0xFF2B2D42), Offset(2f - stride, -6f), Size(9f, 6f), CornerRadius(2.5f, 2.5f))

        // Trailing scarf / cape
        val scarfDir = -facing
        val wave = sin(animClock * 2.5f + runPhase) * 2.5f
        val scarfColor = if (doubleJumpReady) Color(0xFF4ECDC4) else Color(0xFF2A9D8F)
        val scarfPath = Path().apply {
            moveTo(scarfDir * 12f, -h + 11f)
            quadraticTo(scarfDir * 20f, -h + 9f + wave, scarfDir * 25f, -h + 13f - wave)
        }
        drawPath(scarfPath, scarfColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

        // Main body
        val bodyH = h - 4f
        drawRoundRect(Color(0xFFE07A5F), Offset(-w / 2f, -bodyH - 2f), Size(w, bodyH), CornerRadius(9f, 9f))

        // Head highlight
        drawRoundRect(Color(0x2EFFFFFF), Offset(-w / 2f, -bodyH - 2f), Size(w, 7f), CornerRadius(9f, 9f))

        // Body outline
        drawRoundRect(Color(0xFF2B2D42), Offset(-w / 2f, -bodyH - 2f), Size(w, bodyH), CornerRadius(9f, 9f), style = Stroke(2.5f))

        // Scarf collar band
        drawRect(scarfColor, Offset(-w / 2f + 2f, -bodyH + 6f), Size(w - 4f, 4f))

        // Face & eyes
        val lookX = facing * 3.5f
        val lookY = if (!onGround) (if (vy < 0f) -2f else 2f) else 0f
        val eyeY = -bodyH + 17f + lookY * 0.5f
        val leftEyeX = -6f + lookX
        val rightEyeX = 6f + lookX

        // Blush cheeks
        drawCircle(Color(0x47D62828), 2.5f, Offset(leftEyeX - 3f, eyeY + 5f))
        drawCircle(Color(0x47D62828), 2.5f, Offset(rightEyeX + 3f, eyeY + 5f))

        if (bonked) {
            // Bonk eyes
            drawLine(Color(0xFF2B2D42), Offset(leftEyeX - 3f, eyeY - 2f), Offset(leftEyeX + 2f, eyeY), strokeWidth = 2f)
            drawLine(Color(0xFF2B2D42), Offset(leftEyeX + 2f, eyeY), Offset(leftEyeX - 3f, eyeY + 2f), strokeWidth = 2f)
            drawLine(Color(0xFF2B2D42), Offset(rightEyeX + 3f, eyeY - 2f), Offset(rightEyeX - 2f, eyeY), strokeWidth = 2f)
            drawLine(Color(0xFF2B2D42), Offset(rightEyeX - 2f, eyeY), Offset(rightEyeX + 3f, eyeY + 2f), strokeWidth = 2f)
        } else if (blinkTimer < 0f && onGround) {
            // Blinking eyes
            drawLine(Color(0xFF2B2D42), Offset(leftEyeX - 3f, eyeY), Offset(leftEyeX + 3f, eyeY), strokeWidth = 2f)
            drawLine(Color(0xFF2B2D42), Offset(rightEyeX - 3f, eyeY), Offset(rightEyeX + 3f, eyeY), strokeWidth = 2f)
        } else {
            // Normal / Jumping eyes
            val eyeRadius = if (!onGround && vy < 0f) 4.2f else 3.6f
            drawCircle(Color.White, eyeRadius, Offset(leftEyeX, eyeY))
            drawCircle(Color.White, eyeRadius, Offset(rightEyeX, eyeY))
            drawCircle(Color(0xFF2B2D42), 2.1f, Offset(leftEyeX + facing * 1.1f, eyeY + lookY * 0.35f))
            drawCircle(Color(0xFF2B2D42), 2.1f, Offset(rightEyeX + facing * 1.1f, eyeY + lookY * 0.35f))
        }
    }
}
