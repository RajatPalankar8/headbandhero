package com.proto.headbandhero.model

import androidx.compose.ui.graphics.Color

data class PlatformSpec(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val minX: Float = x,
    val maxX: Float = x,
    val minY: Float = y,
    val maxY: Float = y,
    var dx: Float = 0f,
    var dy: Float = 0f
)

data class SpikeSpec(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

data class EnemySpec(
    var x: Float,
    var y: Float,
    val width: Float = 28f,
    val height: Float = 24f,
    var vx: Float,
    val minX: Float,
    val maxX: Float,
    var alive: Boolean = true,
    var squishTimer: Float = 0f
)

data class BirdSpec(
    var x: Float,
    var y: Float,
    val baseY: Float = y,
    val width: Float = 30f,
    val height: Float = 22f,
    var vx: Float,
    val minX: Float,
    val maxX: Float,
    val phase: Float = 0f,
    var alive: Boolean = true,
    var squishTimer: Float = 0f
)

data class CoinSpec(
    val x: Float,
    val y: Float,
    val r: Float = 10f,
    var collected: Boolean = false
)

data class DoorSpec(
    val x: Float,
    val y: Float,
    val width: Float = 36f,
    val height: Float = 52f
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val size: Float,
    var life: Float,
    val color: Color
)
