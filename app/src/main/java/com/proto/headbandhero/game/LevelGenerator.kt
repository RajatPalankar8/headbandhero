package com.proto.headbandhero.game

import com.proto.headbandhero.model.BirdSpec
import com.proto.headbandhero.model.CoinSpec
import com.proto.headbandhero.model.DoorSpec
import com.proto.headbandhero.model.EnemySpec
import com.proto.headbandhero.model.LevelData
import com.proto.headbandhero.model.PlatformSpec
import com.proto.headbandhero.model.SpikeSpec
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun overlaps(
    ax: Float, ay: Float, aw: Float, ah: Float,
    bx: Float, by: Float, bw: Float, bh: Float
): Boolean {
    return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by
}

private class Mulberry32Rng(seed: Long) {
    private var state = seed.toInt()

    fun nextFloat(): Float {
        state += 0x6D2B79F5.toInt()
        var r = (state xor (state ushr 15)) * (1 or state)
        r = r xor (r + ((r xor (r ushr 7)) * 61))
        val res = (r xor (r ushr 14)).toLong() and 0xFFFFFFFFL
        return res.toFloat() / 4294967296f
    }
}

fun buildLevel(index: Int): LevelData {
    return when (index) {
        0 -> LevelData(
            name = "Level 1: Easy Warm-Up",
            spawnX = 50f,
            spawnY = FLOOR_Y - 36f,
            platforms = listOf(
                PlatformSpec(0f, FLOOR_Y, 320f, LOGICAL_H - FLOOR_Y),
                PlatformSpec(400f, FLOOR_Y, LOGICAL_W - 400f, LOGICAL_H - FLOOR_Y),
                PlatformSpec(220f, 240f, 140f, 18f),
                PlatformSpec(420f, 185f, 150f, 18f),
                PlatformSpec(600f, 135f, 160f, 18f)
            ),
            spikes = listOf(SpikeSpec(520f, FLOOR_Y, 48f, 16f)),
            enemies = emptyList(),
            birds = emptyList(),
            coins = listOf(CoinSpec(290f, 208f), CoinSpec(495f, 153f), CoinSpec(635f, 103f)),
            door = DoorSpec(690f, 135f - 52f, 36f, 52f),
            levelWidth = 800f
        )
        1 -> LevelData(
            name = "Level 2: Patrol Stomp",
            spawnX = 45f,
            spawnY = FLOOR_Y - 36f,
            platforms = listOf(
                PlatformSpec(0f, FLOOR_Y, 210f, LOGICAL_H - FLOOR_Y),
                PlatformSpec(265f, 250f, 120f, 18f, vx = 1.3f, minX = 245f, maxX = 330f),
                PlatformSpec(445f, 200f, 160f, 18f),
                PlatformSpec(260f, 145f, 140f, 18f),
                PlatformSpec(465f, 105f, 230f, 18f)
            ),
            spikes = listOf(SpikeSpec(135f, FLOOR_Y, 32f, 16f)),
            enemies = listOf(
                EnemySpec(500f, 200f - 24f, vx = 1.1f, minX = 455f, maxX = 595f),
                EnemySpec(520f, 105f - 24f, vx = -1.1f, minX = 475f, maxX = 625f)
            ),
            birds = emptyList(),
            coins = listOf(CoinSpec(325f, 210f), CoinSpec(330f, 113f), CoinSpec(545f, 70f)),
            door = DoorSpec(645f, 105f - 52f, 36f, 52f),
            levelWidth = 800f
        )
        2 -> LevelData(
            name = "Level 3: Sky Gauntlet",
            spawnX = 40f,
            spawnY = FLOOR_Y - 36f,
            platforms = listOf(
                PlatformSpec(0f, FLOOR_Y, 150f, LOGICAL_H - FLOOR_Y),
                PlatformSpec(205f, 255f, 120f, 18f),
                PlatformSpec(375f, 210f, 115f, 18f, vx = 1.6f, minX = 360f, maxX = 435f),
                PlatformSpec(545f, 165f, 140f, 18f),
                PlatformSpec(380f, 120f, 115f, 18f, vx = -1.6f, minX = 355f, maxX = 430f),
                PlatformSpec(550f, 85f, 200f, 18f)
            ),
            spikes = listOf(
                SpikeSpec(250f, 255f, 32f, 16f),
                SpikeSpec(585f, 85f, 32f, 16f)
            ),
            enemies = listOf(EnemySpec(585f, 165f - 24f, vx = 1.3f, minX = 555f, maxX = 675f)),
            birds = listOf(BirdSpec(320f, 155f, vx = 1.7f, minX = 250f, maxX = 510f)),
            coins = listOf(CoinSpec(222f, 222f), CoinSpec(430f, 175f), CoinSpec(650f, 53f)),
            door = DoorSpec(700f, 85f - 52f, 36f, 52f),
            levelWidth = 800f
        )
        3 -> LevelData(
            name = "Level 4: Final Summit",
            spawnX = 35f,
            spawnY = FLOOR_Y - 36f,
            platforms = listOf(
                PlatformSpec(0f, FLOOR_Y, 135f, LOGICAL_H - FLOOR_Y),
                PlatformSpec(190f, 255f, 105f, 18f, vx = 1.4f, minX = 175f, maxX = 235f),
                PlatformSpec(355f, 215f, 145f, 18f),
                PlatformSpec(555f, 175f, 115f, 18f, vx = -1.6f, minX = 530f, maxX = 615f),
                PlatformSpec(390f, 130f, 130f, 18f),
                PlatformSpec(205f, 95f, 130f, 18f),
                PlatformSpec(45f, 75f, 110f, 18f)
            ),
            spikes = listOf(
                SpikeSpec(410f, 215f, 32f, 16f),
                SpikeSpec(255f, 95f, 32f, 16f)
            ),
            enemies = listOf(EnemySpec(420f, 130f - 24f, vx = 1.3f, minX = 400f, maxX = 510f)),
            birds = listOf(BirdSpec(310f, 160f, vx = -1.9f, minX = 190f, maxX = 500f)),
            coins = listOf(CoinSpec(470f, 180f), CoinSpec(455f, 98f), CoinSpec(220f, 63f)),
            door = DoorSpec(66f, 75f - 52f, 36f, 52f),
            levelWidth = 800f
        )
        else -> generateProceduralLevel(index)
    }
}

private fun generateProceduralLevel(index: Int): LevelData {
    val levelNum = index + 1
    val rng = Mulberry32Rng(levelNum * 9973L + 1337L)
    val progress = (levelNum - 5) / (TOTAL_LEVELS - 5).toFloat()

    val levelWidth = (850f + progress * 1250f).roundToInt().toFloat() // 850px to 2100px
    val platCount = (5 + (progress * 5.8f).toInt()).coerceIn(5, 11)   // 5 to 11 platforms

    val tierName = when {
        levelNum <= 20 -> "Foothills"
        levelNum <= 40 -> "Ridge Run"
        levelNum <= 60 -> "Skyline"
        levelNum <= 80 -> "Apex Trial"
        levelNum <= 100 -> "Summit Gauntlet"
        else -> "Grand Finale"
    }

    val platforms = mutableListOf<PlatformSpec>()
    val spikes = mutableListOf<SpikeSpec>()
    val enemies = mutableListOf<EnemySpec>()
    val birds = mutableListOf<BirdSpec>()
    val coins = mutableListOf<CoinSpec>()

    var curY = FLOOR_Y

    val firstW = (120f - progress * 40f).coerceAtLeast(75f).roundToInt().toFloat()
    platforms.add(PlatformSpec(0f, FLOOR_Y, firstW, LOGICAL_H - FLOOR_Y))

    if (levelNum >= 15 && firstW >= 90f && rng.nextFloat() < 0.45f) {
        spikes.add(SpikeSpec(firstW - 36f, FLOOR_Y, 24f, 14f))
    }

    var curX = firstW

    for (s in 1 until platCount) {
        val isLast = s == platCount - 1
        val minGap = (68f + progress * 28f).roundToInt().toFloat()
        val maxGap = (92f + progress * 32f).roundToInt().toFloat()
        var gap = (minGap + rng.nextFloat() * (maxGap - minGap)).roundToInt().toFloat()

        val targetW = if (isLast) {
            (125f - progress * 25f).coerceAtLeast(75f).roundToInt().toFloat()
        } else {
            ((105f - progress * 45f) + (rng.nextFloat() - 0.5f) * 16f).coerceAtLeast(50f).roundToInt().toFloat()
        }
        var w = targetW.toFloat()

        val remainingPlats = platCount - 1 - s
        val minRemainingSpace = remainingPlats * (60f + minGap)
        val maxBound = levelWidth - 15f
        if (curX + gap + w + minRemainingSpace > maxBound) {
            gap = max(55f, maxBound - curX - minRemainingSpace - w)
            if (curX + gap + w > maxBound) {
                w = max(50f, maxBound - curX - gap)
            }
        }

        val platX = curX + gap
        val dy = (-70f + rng.nextFloat() * 125f).roundToInt().toFloat()
        curY = (curY + dy).coerceIn(95f, FLOOR_Y - 14f)

        val moveRoll = rng.nextFloat()
        val moveThreshold = 0.35f + progress * 0.4f
        val isMovingH = !isLast && moveRoll < moveThreshold * 0.7f
        val isMovingV = !isLast && !isMovingH && levelNum >= 15 && moveRoll < moveThreshold

        if (isMovingH) {
            val travel = (24f + progress * 35f).roundToInt().toFloat()
            val minX = max(curX + 40f, platX - (travel * 0.4f).roundToInt().toFloat())
            val maxX = min(platX + travel, levelWidth - 15f - w)
            val speed = (1.35f + progress * 1.6f) * (if (rng.nextFloat() < 0.5f) 1f else -1f)
            platforms.add(
                PlatformSpec(
                    x = platX,
                    y = curY,
                    width = w,
                    height = 18f,
                    vx = speed,
                    minX = min(minX, platX),
                    maxX = max(maxX, platX)
                )
            )
        } else if (isMovingV) {
            val vTravel = (22f + progress * 24f).roundToInt().toFloat()
            val minY = max(85f, curY - vTravel)
            val maxY = min(FLOOR_Y - 18f, curY + vTravel)
            val vySpeed = (0.9f + progress * 1.1f) * (if (rng.nextFloat() < 0.5f) 1f else -1f)
            platforms.add(
                PlatformSpec(
                    x = platX,
                    y = curY,
                    width = w,
                    height = 18f,
                    vy = vySpeed,
                    minY = minY,
                    maxY = maxY
                )
            )
        } else {
            platforms.add(PlatformSpec(platX, curY, w, 18f))

            val hazardRoll = rng.nextFloat()
            if (!isLast && hazardRoll < 0.82f) {
                if (w >= 78f && rng.nextFloat() < 0.55f) {
                    enemies.add(
                        EnemySpec(
                            x = (platX + w / 2f - 14f).roundToInt().toFloat(),
                            y = curY - 24f,
                            minX = platX + 6f,
                            maxX = platX + w - 6f,
                            vx = (1.2f + progress * 1.35f) * (if (rng.nextFloat() < 0.5f) 1f else -1f)
                        )
                    )
                } else if (w >= 65f) {
                    val spikeW = if (w >= 95f && progress > 0.3f) 48f else 32f
                    val spikeX = (platX + max(24f, (w - spikeW) / 2f)).roundToInt().toFloat()
                    if (spikeX + spikeW <= platX + w - 6f) {
                        spikes.add(SpikeSpec(spikeX, curY, spikeW, 16f))
                    }
                }
            } else if (isLast && w >= 95f && progress > 0.2f) {
                enemies.add(
                    EnemySpec(
                        x = (platX + 16f).roundToInt().toFloat(),
                        y = curY - 24f,
                        minX = platX + 8f,
                        maxX = platX + w - 48f,
                        vx = 1.1f + progress * 1.1f
                    )
                )
            }
        }

        if (coins.size < 3 && (isLast || s % 2 == 1 || rng.nextFloat() < 0.65f)) {
            val highBonus = if (!isLast && rng.nextFloat() < 0.45f) 24f else 0f
            coins.add(
                CoinSpec(
                    x = (platX + (if (isLast) 30f else w / 2f)).roundToInt().toFloat(),
                    y = curY - 34f - highBonus
                )
            )
        }

        curX = platX + w
        if (isLast) break
    }

    while (coins.size < 3) {
        val p = platforms[min(platforms.size - 1, coins.size + 1)]
        coins.add(
            CoinSpec(
                x = (p.x + p.width / 2f).roundToInt().toFloat(),
                y = p.y - 34f
            )
        )
    }

    val lastPlat = platforms.last()
    val door = DoorSpec(
        x = (lastPlat.x + lastPlat.width - 46f).roundToInt().toFloat(),
        y = lastPlat.y - 52f,
        width = 36f,
        height = 52f
    )

    val birdCount = when {
        levelNum < 20 -> 1
        levelNum < 60 -> 2
        else -> 3
    }
    for (b in 0 until birdCount) {
        val anchorIdx = ((b + 1) * platforms.size / (birdCount + 1)).coerceIn(1, platforms.size - 2)
        val anchor = platforms[anchorIdx]
        val minBX = max(95f, anchor.x - 75f)
        val maxBX = min(levelWidth - 65f, anchor.x + anchor.width + 75f)
        val birdY = max(70f, min(260f, anchor.y - 68f - (rng.nextFloat() * 30f).roundToInt()))
        val birdSpeed = (1.55f + progress * 1.6f) * (if (b % 2 == 0) 1f else -1f)
        birds.add(
            BirdSpec(
                x = ((minBX + maxBX) / 2f).roundToInt().toFloat(),
                y = birdY,
                baseY = birdY,
                minX = minBX,
                maxX = maxBX,
                vx = birdSpeed,
                phase = b * 1.7f
            )
        )
    }

    return LevelData(
        name = "Level $levelNum: $tierName",
        spawnX = 26f,
        spawnY = FLOOR_Y - 36f,
        platforms = platforms,
        spikes = spikes,
        enemies = enemies,
        birds = birds,
        coins = coins,
        door = door,
        levelWidth = levelWidth
    )
}
