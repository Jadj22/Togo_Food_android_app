package com.example.togofood.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MapSnippetCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0F2)), // Soft map-like water/land background
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            // ── Fausse carte dessinée (Rues et routes) ──
            Canvas(modifier = Modifier.fillMaxSize()) {
                val streetColor = Color.White
                
                // Route principale diagonale
                drawLine(
                    color = streetColor,
                    start = Offset(-50f, size.height),
                    end = Offset(size.width * 0.7f, -50f),
                    strokeWidth = 40f,
                    cap = StrokeCap.Round
                )
                // Route secondaire
                drawLine(
                    color = streetColor,
                    start = Offset(size.width * 0.4f, size.height + 50f),
                    end = Offset(size.width + 50f, size.height * 0.3f),
                    strokeWidth = 25f,
                    cap = StrokeCap.Round
                )
                // Petite route
                drawLine(
                    color = streetColor,
                    start = Offset(size.width * 0.2f, -20f),
                    end = Offset(size.width * 0.5f, size.height * 0.6f),
                    strokeWidth = 15f
                )
            }

            // ── Marqueurs de plats simulés (Pins) ──
            MapPin(emoji = "🍚", modifier = Modifier.align(Alignment.CenterStart).offset(x = 40.dp, y = (-20).dp))
            MapPin(emoji = "🍗", modifier = Modifier.align(Alignment.TopEnd).offset(x = (-60).dp, y = 20.dp))
            MapPin(emoji = "🥤", modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-30).dp, y = (-30).dp))

            // ── Bouton d'action central ──
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗺️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voir sur la carte",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111111)
                    )
                }
            }
        }
    }
}

@Composable
private fun MapPin(emoji: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(32.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 16.sp)
    }
}
