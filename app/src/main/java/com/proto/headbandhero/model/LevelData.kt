package com.proto.headbandhero.model

data class LevelData(
    val name: String,
    val spawnX: Float,
    val spawnY: Float,
    val platforms: List<PlatformSpec>,
    val spikes: List<SpikeSpec>,
    val enemies: List<EnemySpec>,
    val birds: List<BirdSpec>,
    val coins: List<CoinSpec>,
    val door: DoorSpec,
    val levelWidth: Float = 800f
)
