package com.example.togofood.ui.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.domain.model.Seller
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter
import kotlin.math.roundToInt

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    onBackClick: () -> Unit,
    onSellerClick: (String) -> Unit
) {
    val sellers by viewModel.sellers.collectAsState()
    var selectedSeller by remember { mutableStateOf<Seller?>(null) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Animation d'entrée pour les marqueurs
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimation = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7F1))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    panOffset += pan
                }
            }
    ) {
        // --- Fake Map Background ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSpacing = 300f
            for (i in -5..10) {
                val x = i * gridSpacing + (panOffset.x % gridSpacing)
                drawLine(color = Color.White, start = Offset(x, -1000f), end = Offset(x, size.height + 1000f), strokeWidth = 40f)
                val y = i * gridSpacing + (panOffset.y % gridSpacing)
                drawLine(color = Color.White, start = Offset(-1000f, y), end = Offset(size.width + 1000f, y), strokeWidth = 40f)
            }
        }

        // --- Render Seller Pins ---
        sellers.forEachIndexed { index, seller ->
            val offsetX = 400f * Math.cos(index * 2.0).toFloat()
            val offsetY = 400f * Math.sin(index * 2.0).toFloat()

            val isSelected = selectedSeller == seller

            // Animation d'apparition en cascade
            val markerScale by animateFloatAsState(
                targetValue = if (startAnimation) 1f else 0f,
                animationSpec = tween(
                    durationMillis = TogoMotion.SlowMs,
                    delayMillis = index * 80
                ),
                label = "markerScale"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset {
                        IntOffset(
                            x = (offsetX + panOffset.x).roundToInt(),
                            y = (offsetY + panOffset.y).roundToInt()
                        )
                    }
                    .scale(markerScale),
                contentAlignment = Alignment.Center
            ) {
                MapMarker(
                    seller = seller,
                    isSelected = isSelected,
                    onClick = { selectedSeller = if (isSelected) null else seller }
                )
            }
        }

        // --- Top Bar (Floating) ---
        TopMapBar(onBackClick)

        // --- Seller Snippet (Bottom Disclosure) ---
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            AnimatedVisibility(
                visible = selectedSeller != null,
                enter = TogoMotion.sheetEnter(),
                exit = TogoMotion.sheetExit()
            ) {
                selectedSeller?.let { seller ->
                    SellerSnippetCard(
                        seller = seller,
                        onClick = { onSellerClick(seller.uuid) },
                        onClose = { selectedSeller = null }
                    )
                }
            }
        }
    }
}

@Composable
fun MapMarker(seller: Seller, isSelected: Boolean, onClick: () -> Unit) {
    // Animations de sélection
    val size by animateDpAsState(
        targetValue = if (isSelected) 56.dp else 40.dp,
        animationSpec = TogoMotion.tweenNormal(),
        label = "markerSize"
    )
    val bgColor by animateColorAsState(
        if (isSelected) BrandOrange else Color.White,
        TogoMotion.tweenNormal(),
        label = "markerBg"
    )
    val iconColor by animateColorAsState(
        if (isSelected) Color.White else BrandOrange,
        TogoMotion.tweenNormal(),
        label = "markerIcon"
    )
    val elevation by animateDpAsState(
        if (isSelected) 12.dp else 4.dp,
        TogoMotion.tweenNormal(),
        label = "markerElev"
    )

    Box(
        modifier = Modifier
            .size(size)
            .shadow(elevation, CircleShape)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = seller.name,
            tint = iconColor,
            modifier = Modifier.size(size / 2)
        )
    }
}

@Composable
private fun TopMapBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp)
            .togoEnter(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                stringResource(R.string.map_title_lome),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun SellerSnippetCard(seller: Seller, onClick: () -> Unit, onClose: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFDEEE9)),
                contentAlignment = Alignment.Center
            ) {
                Text("🏪", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = seller.name, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text(text = seller.zone, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐ ${String.format("%.1f", seller.rating)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
                    Text(text = " • ${seller.distanceMeters}m", color = Color.Gray, fontSize = 13.sp)
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_close),
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
