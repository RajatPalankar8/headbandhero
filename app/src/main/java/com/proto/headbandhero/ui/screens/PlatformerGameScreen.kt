package com.proto.headbandhero.ui.screens

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proto.headbandhero.game.KEY_CLEARED
import com.proto.headbandhero.game.KEY_DEATHS
import com.proto.headbandhero.game.KEY_HAS_PLAYED
import com.proto.headbandhero.game.KEY_UNLOCKED
import com.proto.headbandhero.game.LOGICAL_H
import com.proto.headbandhero.game.LOGICAL_W
import com.proto.headbandhero.game.PREFS_NAME
import com.proto.headbandhero.game.SoundEffectPlayer
import com.proto.headbandhero.game.TOTAL_LEVELS
import com.proto.headbandhero.game.buildLevel
import com.proto.headbandhero.game.overlaps
import com.proto.headbandhero.model.Particle
import com.proto.headbandhero.model.PlatformSpec
import com.proto.headbandhero.ui.components.AdMobBanner
import com.proto.headbandhero.ui.components.HoldThumbButton
import com.proto.headbandhero.ui.components.HudBadge
import com.proto.headbandhero.ui.components.LevelSelectOverlay
import com.proto.headbandhero.ui.components.OverlayDialog
import com.proto.headbandhero.ui.components.SmallHudButton
import com.proto.headbandhero.ui.components.WelcomeBackDialog
import com.proto.headbandhero.ui.components.drawBirds
import com.proto.headbandhero.ui.components.drawCanvasGrid
import com.proto.headbandhero.ui.components.drawCoins
import com.proto.headbandhero.ui.components.drawDoor
import com.proto.headbandhero.ui.components.drawEnemies
import com.proto.headbandhero.ui.components.drawPlayer
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun PlatformerGameScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var unlockedLevel by remember {
        mutableIntStateOf(prefs.getInt(KEY_UNLOCKED, 0).coerceIn(0, TOTAL_LEVELS - 1))
    }
    var clearedSet by remember {
        val raw = prefs.getStringSet(KEY_CLEARED, emptySet()) ?: emptySet()
        mutableStateOf(raw.mapNotNull { it.toIntOrNull() }.toSet())
    }
    var deaths by remember {
        mutableIntStateOf(prefs.getInt(KEY_DEATHS, 0))
    }

    var showWelcomeBack by remember {
        mutableStateOf(prefs.getBoolean(KEY_HAS_PLAYED, false) && (unlockedLevel > 0 || clearedSet.isNotEmpty() || deaths > 0))
    }

    var currentLevelIndex by remember { mutableIntStateOf(unlockedLevel) }
    var elapsedSeconds by remember { mutableFloatStateOf(0f) }
    var doubleJumpReady by remember { mutableStateOf(true) }
    var coinsCollected by remember { mutableIntStateOf(0) }
    var totalLevelCoins by remember { mutableIntStateOf(3) }

    var levelWon by remember { mutableStateOf(false) }
    var levelFailed by remember { mutableStateOf(false) }
    var failReason by remember { mutableStateOf("") }
    var showLevelSelect by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(SoundEffectPlayer.isMuted) }

    // Multi-touch input states
    var leftPressed by remember { mutableStateOf(false) }
    var rightPressed by remember { mutableStateOf(false) }
    var jumpPressed by remember { mutableStateOf(false) }
    var jumpEdge by remember { mutableStateOf(false) }

    // Mutable game world state
    var activeLevel by remember { mutableStateOf(buildLevel(currentLevelIndex)) }
    var playerX by remember { mutableFloatStateOf(activeLevel.spawnX) }
    var playerY by remember { mutableFloatStateOf(activeLevel.spawnY) }
    var playerVx by remember { mutableFloatStateOf(0f) }
    var playerVy by remember { mutableFloatStateOf(0f) }
    var onGround by remember { mutableStateOf(true) }
    var facing by remember { mutableIntStateOf(1) }
    var scaleX by remember { mutableFloatStateOf(1f) }
    var scaleY by remember { mutableFloatStateOf(1f) }
    var runPhase by remember { mutableFloatStateOf(0f) }
    var bonkTimer by remember { mutableFloatStateOf(0f) }
    var blinkTimer by remember { mutableFloatStateOf(120f) }
    var dustTimer by remember { mutableFloatStateOf(0f) }
    var animClock by remember { mutableFloatStateOf(0f) }
    var flashTimer by remember { mutableIntStateOf(0) }
    var shakeTimer by remember { mutableIntStateOf(0) }
    var isDying by remember { mutableStateOf(false) }
    var deathTimer by remember { mutableIntStateOf(0) }
    var dashSpot by remember { mutableStateOf<Pair<Float, Float>?>(null) }
    var dashAge by remember { mutableIntStateOf(0) }
    val particles = remember { mutableStateListOf<Particle>() }
    var standingPlat by remember { mutableStateOf<PlatformSpec?>(null) }

    fun saveProgress() {
        prefs.edit()
            .putInt(KEY_UNLOCKED, unlockedLevel)
            .putStringSet(KEY_CLEARED, clearedSet.map { it.toString() }.toSet())
            .putInt(KEY_DEATHS, deaths)
            .putBoolean(KEY_HAS_PLAYED, true)
            .apply()
    }

    fun resetProgress() {
        unlockedLevel = 0
        clearedSet = emptySet()
        deaths = 0
        saveProgress()
    }

    fun spawnDust(cx: Float, cy: Float, count: Int, spreadX: Float, colorOverride: Color? = null) {
        val rng = Random
        for (i in 0 until count) {
            particles.add(
                Particle(
                    x = cx + (rng.nextFloat() - 0.5f) * 10f,
                    y = cy - 2f,
                    vx = (rng.nextFloat() - 0.5f) * spreadX,
                    vy = -0.5f - rng.nextFloat() * 1.4f,
                    size = 3.5f + rng.nextFloat() * 3f,
                    life = 0.75f,
                    color = colorOverride ?: Color(0xFFD8C99B)
                )
            )
        }
    }

    fun spawnCoinSparkle(cx: Float, cy: Float) {
        for (i in 0 until 8) {
            val angle = (PI * 2.0 * i) / 8.0
            particles.add(
                Particle(
                    x = cx,
                    y = cy,
                    vx = (cos(angle) * 2.2).toFloat(),
                    vy = (sin(angle) * 2.2 - 1.0).toFloat(),
                    size = 4f,
                    life = 0.9f,
                    color = Color(0xFFE9C46A)
                )
            )
        }
    }

    fun resetCurrentLevel(resetTimer: Boolean = false) {
        val lvl = buildLevel(currentLevelIndex)
        activeLevel = lvl
        totalLevelCoins = lvl.coins.size
        playerX = lvl.spawnX
        playerY = lvl.spawnY
        playerVx = 0f
        playerVy = 0f
        onGround = true
        facing = 1
        scaleX = 1f
        scaleY = 1f
        runPhase = 0f
        bonkTimer = 0f
        blinkTimer = 120f
        dustTimer = 0f
        doubleJumpReady = true
        coinsCollected = 0
        levelWon = false
        levelFailed = false
        isDying = false
        deathTimer = 0
        dashSpot = null
        dashAge = 0
        standingPlat = null
        particles.clear()
        if (resetTimer) elapsedSeconds = 0f
    }

    fun goToLevel(idx: Int) {
        currentLevelIndex = idx.coerceIn(0, TOTAL_LEVELS - 1)
        resetCurrentLevel(resetTimer = true)
    }

    fun triggerHit(reason: String) {
        if (isDying || levelFailed || levelWon) return
        deaths += 1
        saveProgress()
        isDying = true
        deathTimer = 36
        flashTimer = 10
        shakeTimer = 10
        failReason = reason
        dashSpot = Pair(playerX, min(playerY, LOGICAL_H - 46f))
        dashAge = 0
        particles.clear()
        val cx = playerX + 16f
        val cy = min(playerY, LOGICAL_H - 46f) + 18f
        for (i in 0 until 24) {
            val angle = (2.0 * PI * i) / 24.0
            val spd = 2.5f + (i % 4) * 1.1f
            particles.add(
                Particle(
                    x = cx,
                    y = cy,
                    vx = (cos(angle) * spd).toFloat(),
                    vy = (sin(angle) * spd - 2.2).toFloat(),
                    size = 6f,
                    life = 1f,
                    color = if (i % 3 == 0) Color(0xFFD62828) else (if (i % 2 == 0) Color(0xFFE07A5F) else Color(0xFF2B2D42))
                )
            )
        }
        SoundEffectPlayer.playHit()
    }

    // 60 FPS game loop
    LaunchedEffect(currentLevelIndex) {
        var lastNs = withFrameNanos { it }
        while (true) {
            val nowNs = withFrameNanos { it }
            val dt = ((nowNs - lastNs) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
            lastNs = nowNs
            val dtScale = dt * 60f
            animClock += dt * 4f

            if (flashTimer > 0) flashTimer -= 1
            if (shakeTimer > 0) shakeTimer -= 1

            for (i in particles.indices.reversed()) {
                val pt = particles[i]
                pt.x += pt.vx * dtScale
                pt.y += pt.vy * dtScale
                pt.vy += 0.22f * dtScale
                pt.life -= 0.032f * dtScale
                if (pt.life <= 0f) particles.removeAt(i)
            }

            if (dashSpot != null) dashAge += 1

            if (isDying) {
                deathTimer -= 1
                if (deathTimer <= 0) {
                    isDying = false
                    levelFailed = true
                }
                continue
            }

            if (levelWon || levelFailed || showLevelSelect || showWelcomeBack) continue

            elapsedSeconds += dt

            // Moving platforms
            for (p in activeLevel.platforms) {
                if (p.vx != 0f) {
                    val oldX = p.x
                    p.x += p.vx * dtScale
                    if (p.x <= p.minX) {
                        p.x = p.minX; p.vx = abs(p.vx)
                    } else if (p.x >= p.maxX) {
                        p.x = p.maxX; p.vx = -abs(p.vx)
                    }
                    p.dx = p.x - oldX
                } else p.dx = 0f

                if (p.vy != 0f) {
                    val oldY = p.y
                    p.y += p.vy * dtScale
                    if (p.y <= p.minY) {
                        p.y = p.minY; p.vy = abs(p.vy)
                    } else if (p.y >= p.maxY) {
                        p.y = p.maxY; p.vy = -abs(p.maxY)
                    }
                    p.dy = p.y - oldY
                } else p.dy = 0f
            }

            standingPlat?.let { sp ->
                playerX += sp.dx
                playerY += sp.dy
            }

            // Patrol enemies & birds
            for (e in activeLevel.enemies) {
                if (!e.alive) {
                    if (e.squishTimer > 0f) e.squishTimer -= dtScale
                    continue
                }
                e.x += e.vx * dtScale
                if (e.x <= e.minX) {
                    e.x = e.minX; e.vx = abs(e.vx)
                } else if (e.x + e.width >= e.maxX) {
                    e.x = e.maxX - e.width; e.vx = -abs(e.vx)
                }
            }
            for (b in activeLevel.birds) {
                if (!b.alive) {
                    if (b.squishTimer > 0f) b.squishTimer -= dtScale
                    continue
                }
                b.x += b.vx * dtScale
                if (b.x <= b.minX) {
                    b.x = b.minX; b.vx = abs(b.vx)
                } else if (b.x + b.width >= b.maxX) {
                    b.x = b.maxX - b.width; b.vx = -abs(b.vx)
                }
                b.y = b.baseY + sin(animClock * 1.6f + b.phase) * 10f
            }

            // Player horizontal input
            val accel = 0.55f * dtScale
            if (leftPressed && !rightPressed) {
                playerVx = max(-3.3f, playerVx - accel)
                facing = -1
            } else if (rightPressed && !leftPressed) {
                playerVx = min(3.3f, playerVx + accel)
                facing = 1
            } else {
                playerVx *= 0.68f.pow(dtScale)
                if (abs(playerVx) < 0.08f) playerVx = 0f
            }

            // Jump & 1x Double Jump
            if (jumpPressed && onGround) {
                playerVy = -11.2f
                onGround = false
                standingPlat = null
                jumpEdge = false
                scaleX = 0.82f; scaleY = 1.22f
                spawnDust(playerX + 16f, playerY + 36f, 5, 2.8f)
                SoundEffectPlayer.playJump()
            } else if (jumpEdge && !onGround && doubleJumpReady) {
                doubleJumpReady = false
                jumpEdge = false
                playerVy = -10.5f
                scaleX = 0.8f; scaleY = 1.24f
                spawnDust(playerX + 16f, playerY + 36f, 8, 4.2f, Color(0xFF4ECDC4))
                SoundEffectPlayer.playDoubleJump()
            }
            jumpEdge = false

            playerVy = min(13f, playerVy + 0.54f * dtScale)

            // Horizontal move + collision
            playerX = (playerX + playerVx * dtScale).coerceIn(0f, activeLevel.levelWidth - 32f)
            for (p in activeLevel.platforms) {
                if (overlaps(playerX, playerY, 32f, 36f, p.x, p.y, p.width, p.height)) {
                    if (playerVx > 0f) {
                        playerX = p.x - 32f
                        if (playerVx > 1.2f) { scaleX = 0.85f; scaleY = 1.12f }
                        playerVx = 0f
                    } else if (playerVx < 0f) {
                        playerX = p.x + p.width
                        if (playerVx < -1.2f) { scaleX = 0.85f; scaleY = 1.12f }
                        playerVx = 0f
                    }
                }
            }

            // Vertical move + collision
            val prevBottom = playerY + 36f
            val wasGrounded = onGround
            onGround = false
            standingPlat = null
            playerY += playerVy * dtScale

            for (p in activeLevel.platforms) {
                if (overlaps(playerX, playerY, 32f, 36f, p.x, p.y, p.width, p.height)) {
                    if (playerVy > 0f) {
                        if (!wasGrounded && playerVy > 2.5f) {
                            scaleX = 1.24f; scaleY = 0.76f
                            spawnDust(playerX + 16f, p.y, 6, 3.4f)
                        }
                        playerY = p.y - 36f
                        playerVy = 0f
                        onGround = true
                        standingPlat = p
                    } else if (playerVy < 0f) {
                        playerY = p.y + p.height
                        playerVy = 0f
                        bonkTimer = 14f
                        scaleX = 1.18f; scaleY = 0.82f
                        spawnDust(playerX + 16f, p.y + p.height + 4f, 4, 2.5f)
                    }
                }
            }

            // Spring animations & blinking
            val spring = min(1f, 0.22f * dtScale)
            scaleX += (1f - scaleX) * spring
            scaleY += (1f - scaleY) * spring
            if (bonkTimer > 0f) bonkTimer -= dtScale
            blinkTimer -= dtScale
            if (blinkTimer < -6f) blinkTimer = 110f + Random.nextFloat() * 90f

            if (onGround && abs(playerVx) > 0.6f) {
                runPhase += abs(playerVx) * 0.14f * dtScale
                dustTimer -= dtScale
                if (dustTimer <= 0f) {
                    spawnDust(playerX + 16f - facing * 8f, playerY + 36f, 1, 1.2f)
                    dustTimer = 7f
                }
            } else runPhase = 0f

            // Coins
            for (c in activeLevel.coins) {
                if (!c.collected && overlaps(playerX, playerY, 32f, 36f, c.x - c.r, c.y - c.r, c.r * 2, c.r * 2)) {
                    c.collected = true
                    coinsCollected += 1
                    spawnCoinSparkle(c.x, c.y)
                    SoundEffectPlayer.playCoin()
                }
            }

            // Enemies & birds (Mario stomp)
            for (e in activeLevel.enemies) {
                if (e.alive && overlaps(playerX, playerY, 32f, 36f, e.x, e.y, e.width, e.height)) {
                    if (playerVy > 0f && prevBottom <= e.y + 12f) {
                        e.alive = false; e.squishTimer = 18f
                        playerVy = -8.6f; onGround = false
                        scaleX = 1.2f; scaleY = 0.8f
                        spawnDust(e.x + e.width / 2f, e.y + 4f, 8, 3.8f, Color(0xFF8D5B4C))
                        SoundEffectPlayer.playStomp()
                    } else {
                        triggerHit("You ran into an enemy!")
                    }
                }
            }
            for (b in activeLevel.birds) {
                if (b.alive && overlaps(playerX, playerY, 32f, 36f, b.x, b.y, b.width, b.height)) {
                    if (playerVy > 0f && prevBottom <= b.y + 12f) {
                        b.alive = false; b.squishTimer = 18f
                        playerVy = -8.8f; onGround = false
                        scaleX = 1.2f; scaleY = 0.8f
                        spawnDust(b.x + b.width / 2f, b.y + 6f, 8, 3.8f, Color(0xFF5E548E))
                        SoundEffectPlayer.playStomp()
                    } else {
                        triggerHit("You hit a flying bird!")
                    }
                }
            }

            // Pit & Spikes
            if (playerY > LOGICAL_H - 4f) {
                triggerHit("You fell into a gap.")
            } else {
                for (s in activeLevel.spikes) {
                    if (overlaps(playerX, playerY, 32f, 36f, s.x + 4f, s.y - s.height + 4f, s.width - 8f, s.height - 4f)) {
                        triggerHit("You touched a spike!")
                        break
                    }
                }
            }

            // Goal Door
            val d = activeLevel.door
            if (!isDying && overlaps(playerX, playerY, 32f, 36f, d.x, d.y, d.width, d.height)) {
                levelWon = true
                SoundEffectPlayer.playWin()
                clearedSet = clearedSet + currentLevelIndex
                if (currentLevelIndex + 1 > unlockedLevel && currentLevelIndex + 1 < TOTAL_LEVELS) {
                    unlockedLevel = currentLevelIndex + 1
                }
                saveProgress()
            }
        }
    }

    // Camera offset tracking player
    val cameraX = (playerX - LOGICAL_W / 2f + 16f).coerceIn(0f, activeLevel.levelWidth - LOGICAL_W)

    // Full-screen Box Layout with transparent floating scoreboard, thumb buttons, and AdMob banner
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5AB))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sx = size.width / LOGICAL_W
            val sy = size.height / LOGICAL_H
            withTransform({
                scale(sx, sy, pivot = Offset.Zero)
                if (shakeTimer > 0) {
                    translate((Random.nextFloat() - 0.5f) * 6f, (Random.nextFloat() - 0.5f) * 6f)
                }
                translate(-cameraX, 0f)
            }) {
                // Background grid lines
                drawCanvasGrid(activeLevel.levelWidth)

                // Platforms
                for (p in activeLevel.platforms) {
                    val moving = p.vx != 0f || p.vy != 0f
                    drawRect(if (moving) Color(0xFF3D405B) else Color(0xFF2B2D42), Offset(p.x, p.y), Size(p.width, p.height))
                    drawRect(if (moving) Color(0xFFE9C46A) else Color(0xFF3D405B), Offset(p.x, p.y), Size(p.width, 5f))
                }

                // Spikes
                for (s in activeLevel.spikes) {
                    val count = max(1, (s.width / 16f).toInt())
                    val sw = s.width / count
                    for (i in 0 until count) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(s.x + i * sw, s.y)
                            lineTo(s.x + i * sw + sw / 2f, s.y - s.height)
                            lineTo(s.x + (i + 1) * sw, s.y)
                            close()
                        }
                        drawPath(path, Color(0xFFD62828))
                        drawPath(path, Color(0xFF2B2D42), style = Stroke(2f))
                    }
                }

                // Enemies & Birds & Coins & Door
                drawEnemies(activeLevel.enemies, animClock)
                drawBirds(activeLevel.birds, animClock)
                drawCoins(activeLevel.coins, animClock)
                drawDoor(activeLevel.door)

                // Player character
                if (!isDying && !levelFailed) {
                    drawPlayer(
                        x = playerX,
                        y = playerY,
                        w = 32f,
                        h = 36f,
                        facing = facing,
                        vx = playerVx,
                        vy = playerVy,
                        onGround = onGround,
                        runPhase = runPhase,
                        scaleX = scaleX,
                        scaleY = scaleY,
                        doubleJumpReady = doubleJumpReady,
                        bonked = bonkTimer > 0f,
                        blinkTimer = blinkTimer,
                        animClock = animClock
                    )
                }

                // Dashed impact spot + pulse ring
                dashSpot?.let { (dx, dy) ->
                    val pulse = min(1f, dashAge / 16f)
                    val expand = pulse * 10f
                    drawRect(
                        color = Color(0xFFD62828),
                        topLeft = Offset(dx - expand * 0.4f, dy - expand * 0.4f),
                        size = Size(32f + expand * 0.8f, 36f + expand * 0.8f),
                        style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), -dashAge * 0.8f))
                    )
                    drawCircle(
                        color = Color(0xFFD62828).copy(alpha = (1f - pulse * 0.7f).coerceIn(0.25f, 1f)),
                        radius = 12f + pulse * 24f,
                        center = Offset(dx + 16f, dy + 18f),
                        style = Stroke(3f)
                    )
                }

                // Particles
                for (pt in particles) {
                    drawRect(pt.color.copy(alpha = pt.life.coerceIn(0f, 1f)), Offset(pt.x - pt.size / 2f, pt.y - pt.size / 2f), Size(pt.size, pt.size))
                }

                // Flash overlay on hit
                if (flashTimer > 0) {
                    drawRect(Color(0xFFD62828).copy(alpha = (flashTimer / 25f).coerceIn(0f, 1f)), Offset(cameraX, 0f), Size(LOGICAL_W, LOGICAL_H))
                }
            }
        }

        // Right-Side Touch-to-Jump Area (Tap anywhere on the right half of the screen to jump)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.55f)
                .align(Alignment.CenterEnd)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        if (!showWelcomeBack && !showLevelSelect && !levelFailed && !levelWon) {
                            if (!jumpPressed) jumpEdge = true
                            jumpPressed = true
                        }
                        do {
                            val event = awaitPointerEvent()
                        } while (event.changes.any { it.pressed })
                        jumpPressed = false
                    }
                }
        )

        // Slim Transparent Floating Top Scoreboard
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .background(Color(0x551E1E24), RoundedCornerShape(999.dp))
                .border(1.dp, Color(0x38F3E5AB), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                HudBadge(activeLevel.name, Color(0xFFF3E5AB))
                HudBadge("Lv ${currentLevelIndex + 1}/$TOTAL_LEVELS", Color(0xFF2A9D8F))
                HudBadge(if (doubleJumpReady) "2x: Ready" else "2x: Used", if (doubleJumpReady) Color(0xFF4ECDC4) else Color(0xFF9EA2B3))
                HudBadge("Coins: $coinsCollected/$totalLevelCoins", Color(0xFFE9C46A))
                HudBadge("Deaths: $deaths", Color(0xFFE07A5F))
                HudBadge("${"%.1f".format(elapsedSeconds)}s", Color(0xFFF3E5AB))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SmallHudButton(if (isMuted) "Sound: Off" else "Sound: On") {
                    isMuted = !isMuted
                    SoundEffectPlayer.isMuted = isMuted
                }
                SmallHudButton("Levels") { showLevelSelect = true }
                SmallHudButton("Reset") { resetCurrentLevel(resetTimer = true) }
            }
        }

        // Bottom-Left Movement Buttons (Enlarged to 92.dp x 68.dp for easy comfortable touching)
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Start))
                .padding(start = 12.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HoldThumbButton("◀", Modifier.size(92.dp, 68.dp), fontSize = 22.sp) { leftPressed = it }
            HoldThumbButton("▶", Modifier.size(92.dp, 68.dp), fontSize = 22.sp) { rightPressed = it }
        }

        // Bottom-Center AdMob Banner Ad
        AdMobBanner(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .padding(bottom = 2.dp)
        )

        // Bottom-Right Visual Jump Button
        HoldThumbButton(
            label = "JUMP",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.End))
                .padding(end = 12.dp, bottom = 10.dp)
                .size(96.dp, 68.dp),
            isAccent = true,
            fontSize = 16.sp
        ) { pressed ->
            if (pressed && !jumpPressed) jumpEdge = true
            jumpPressed = pressed
        }

        // Welcome Back / Win / Fail / Level Select Overlays
        if (showWelcomeBack) {
            WelcomeBackDialog(
                unlockedLevel = unlockedLevel,
                clearedCount = clearedSet.size,
                totalDeaths = deaths,
                onContinue = {
                    showWelcomeBack = false
                    saveProgress()
                    goToLevel(unlockedLevel)
                },
                onLevelSelect = {
                    showWelcomeBack = false
                    saveProgress()
                    showLevelSelect = true
                }
            )
        } else if (levelFailed) {
            OverlayDialog(
                title = "You Dashed!",
                subtitle = failReason,
                primaryLabel = "Restart Level",
                onPrimary = { resetCurrentLevel() },
                onLevels = { showLevelSelect = true }
            )
        } else if (levelWon) {
            val isLast = currentLevelIndex == TOTAL_LEVELS - 1
            OverlayDialog(
                title = if (isLast) "You Beat All 101 Levels!" else "Level ${currentLevelIndex + 1} Cleared & Saved!",
                subtitle = "Time: ${"%.1f".format(elapsedSeconds)}s  |  Coins: $coinsCollected/$totalLevelCoins",
                primaryLabel = if (isLast) "Play Again" else "Next Level",
                onPrimary = { goToLevel(if (isLast) 0 else currentLevelIndex + 1) },
                onLevels = { showLevelSelect = true }
            )
        }

        if (showLevelSelect) {
            LevelSelectOverlay(
                unlockedLevel = unlockedLevel,
                clearedSet = clearedSet,
                currentLevel = currentLevelIndex,
                onSelect = { idx -> showLevelSelect = false; goToLevel(idx) },
                onResetSave = { resetProgress(); goToLevel(0) },
                onClose = { showLevelSelect = false }
            )
        }
    }
}
