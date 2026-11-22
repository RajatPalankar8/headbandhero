package com.proto.headbandhero.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proto.headbandhero.game.TOTAL_LEVELS

@Composable
fun OverlayDialog(
    title: String,
    subtitle: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    onLevels: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x881E1E24))
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF2B2D42), RoundedCornerShape(14.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, color = Color(0xFFF3E5AB), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFFF4F1DE), fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onPrimary,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A5F))
                ) {
                    Text(primaryLabel, color = Color(0xFF1E1E24), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onLevels,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D405B))
                ) {
                    Text("Levels", color = Color(0xFFF4F1DE))
                }
            }
        }
    }
}

@Composable
fun WelcomePageOverlay(
    unlockedLevel: Int,
    clearedCount: Int,
    totalDeaths: Int,
    onStartGame: () -> Unit,
    onSelectLevel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE1E1E24))
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF2B2D42), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0x66F3E5AB), RoundedCornerShape(16.dp))
                .padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE07A5F), RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFFF3E5AB), RoundedCornerShape(12.dp))
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Text(
                    "HEADBAND HERO",
                    color = Color(0xFF1E1E24),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                "101 Levels of Fair Platforming",
                color = Color(0xFFF3E5AB),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                "Unlocked Level: ${unlockedLevel + 1}  |  Cleared: $clearedCount / $TOTAL_LEVELS  |  Deaths: $totalDeaths",
                color = Color(0xFFC9C5B1),
                fontSize = 11.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onStartGame,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A5F)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        if (unlockedLevel > 0) "Continue (Level ${unlockedLevel + 1})" else "Start Game",
                        color = Color(0xFF1E1E24),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onSelectLevel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D405B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Select Level (1–101)", color = Color(0xFFF4F1DE), fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun WelcomeBackDialog(
    unlockedLevel: Int,
    clearedCount: Int,
    totalDeaths: Int,
    onContinue: () -> Unit,
    onLevelSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x881E1E24))
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF2B2D42), RoundedCornerShape(14.dp))
                .border(2.dp, Color(0x4DF3E5AB), RoundedCornerShape(14.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Welcome Back, Hero!",
                color = Color(0xFFF3E5AB),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Unlocked Level: ${unlockedLevel + 1}  |  Cleared: $clearedCount / $TOTAL_LEVELS  |  Deaths: $totalDeaths",
                color = Color(0xFFF4F1DE),
                fontSize = 12.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A5F))
                ) {
                    Text("Continue (Level ${unlockedLevel + 1})", color = Color(0xFF1E1E24), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onLevelSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D405B))
                ) {
                    Text("Level Select", color = Color(0xFFF4F1DE))
                }
            }
        }
    }
}

@Composable
fun LevelSelectOverlay(
    unlockedLevel: Int,
    clearedSet: Set<Int>,
    currentLevel: Int,
    onSelect: (Int) -> Unit,
    onResetSave: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA1E1E24))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.88f)
                .background(Color(0xFF2B2D42), RoundedCornerShape(14.dp))
                .border(2.dp, Color(0x4DF3E5AB), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Select Level (1–101)", color = Color(0xFFF3E5AB), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Unlocked: ${unlockedLevel + 1} / 101  |  Cleared: ${clearedSet.size}",
                        color = Color(0xFFC9C5B1),
                        fontSize = 11.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallHudButton("Reset Save", onResetSave)
                    SmallHudButton("Close", onClose)
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(56.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(TOTAL_LEVELS) { idx ->
                    val unlocked = idx <= unlockedLevel
                    val cleared = clearedSet.contains(idx)
                    val isCurrent = idx == currentLevel
                    val bg = when {
                        !unlocked -> Color(0xFF1E1E24)
                        isCurrent -> Color(0xFFE07A5F)
                        cleared -> Color(0xFF2A9D8F)
                        else -> Color(0xFF3D405B)
                    }
                    val subtext = when {
                        !unlocked -> "Locked"
                        cleared -> "Cleared"
                        isCurrent -> "Now"
                        else -> "Play"
                    }
                    val textColor = when {
                        !unlocked -> Color(0xFF6C6F82)
                        isCurrent || cleared -> Color(0xFF1E1E24)
                        else -> Color(0xFFF4F1DE)
                    }

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .then(
                                if (isCurrent) Modifier.border(2.dp, Color(0xFFF3E5AB), RoundedCornerShape(8.dp))
                                else Modifier
                            )
                            .clickable(enabled = unlocked) { onSelect(idx) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${idx + 1}",
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                subtext,
                                color = textColor.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
