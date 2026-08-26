package com.example.carpimadasi.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class BubbleLetter(
    val char: String,
    val topColor: Color,
    val bottomColor: Color,
    val shadowColor: Color,
    val rotation: Float
)

@Composable
fun MatematikAdasiLogo(
    modifier: Modifier = Modifier
) {
    // M A T E M A T İ K
    val row1Letters = listOf(
        BubbleLetter("M", Color(0xFFFF6584), Color(0xFFE11D48), Color(0xFF9F1239), -2.5f),
        BubbleLetter("A", Color(0xFFFBBF24), Color(0xFFF59E0B), Color(0xFFB45309), 2f),
        BubbleLetter("T", Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0369A1), -2f),
        BubbleLetter("E", Color(0xFF34D399), Color(0xFF059669), Color(0xFF047857), 2.5f),
        BubbleLetter("M", Color(0xFFA855F7), Color(0xFF7E22CE), Color(0xFF581C87), -1.5f),
        BubbleLetter("A", Color(0xFFFB923C), Color(0xFFEA580C), Color(0xFFC2410C), 2f),
        BubbleLetter("T", Color(0xFF2DD4BF), Color(0xFF0D9488), Color(0xFF115E59), -2f),
        BubbleLetter("İ", Color(0xFFF472B6), Color(0xFFDB2777), Color(0xFF9D174D), 1.5f),
        BubbleLetter("K", Color(0xFF60A5FA), Color(0xFF2563EB), Color(0xFF1D4ED8), -2f)
    )

    // A D A S I
    val row2Letters = listOf(
        BubbleLetter("A", Color(0xFFFACC15), Color(0xFFCA8A04), Color(0xFF854D0E), -2.5f),
        BubbleLetter("D", Color(0xFFEC4899), Color(0xFFDB2777), Color(0xFF9D174D), 2f),
        BubbleLetter("A", Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0369A1), -2f),
        BubbleLetter("S", Color(0xFF4ADE80), Color(0xFF16A34A), Color(0xFF166534), 2.5f),
        BubbleLetter("I", Color(0xFFA855F7), Color(0xFF7E22CE), Color(0xFF581C87), -1.5f)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.testTag("matematik_adasi_logo")
    ) {
        // Playful fixed badge container
        Box(
            modifier = Modifier
                .shadow(6.dp, RoundedCornerShape(26.dp))
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.96f),
                            Color(0xFFFEF3C7).copy(alpha = 0.92f)
                        )
                    )
                )
                .border(2.5.dp, Color(0xFFFCD34D), RoundedCornerShape(26.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // First Row: M A T E M A T İ K
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row1Letters.forEach { letter ->
                        CandyLetterTile(letter = letter, size = 32.dp, fontSize = 17.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Second Row: 🏝️ A D A S I 🌴
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏝️",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .offset(y = (-1).dp)
                            .rotate(-8f)
                    )

                    row2Letters.forEach { letter ->
                        CandyLetterTile(letter = letter, size = 36.dp, fontSize = 20.sp)
                    }

                    Text(
                        text = "🏝️",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .offset(y = (-1).dp)
                            .rotate(8f)
                    )
                }
            }
        }
    }
}

// Backward compatibility alias
@Composable
fun CarpimAdasiLogo(modifier: Modifier = Modifier) {
    MatematikAdasiLogo(modifier = modifier)
}

@Composable
private fun CandyLetterTile(
    letter: BubbleLetter,
    size: Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Box(
        modifier = Modifier
            .size(size)
            .rotate(letter.rotation)
            .shadow(3.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    listOf(letter.topColor, letter.bottomColor)
                )
            )
            .border(1.2.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Glossy top highlight for 3D candy effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(size / 2.6f)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)
                    )
                )
        )

        // 3D bottom shade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.5.dp)
                .align(Alignment.BottomCenter)
                .background(letter.shadowColor.copy(alpha = 0.45f))
        )

        // The Letter Character
        Text(
            text = letter.char,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

