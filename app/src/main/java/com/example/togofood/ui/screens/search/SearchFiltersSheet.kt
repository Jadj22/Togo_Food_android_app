package com.example.togofood.ui.screens.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.R
import com.example.togofood.ui.components.PrimaryButton
import com.example.togofood.ui.theme.TogoMotion

private val Ink = Color(0xFF111111)
private val Muted = Color(0xFF6B6B6B)
private val SurfaceBg = Color(0xFFF5F5F5)

/**
 * Sheet unique (mid→staff) : prix + note visibles ensemble,
 * counts avant apply via CTA « Voir N résultats ».
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchFiltersSheet(
    initialMaxPrice: Int?,
    initialMinRating: Float?,
    previewCount: (maxPrice: Int?, minRating: Float?) -> Int,
    onDismiss: () -> Unit,
    onApply: (maxPrice: Int?, minRating: Float?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draftMaxPrice by remember(initialMaxPrice) { mutableStateOf(initialMaxPrice) }
    var draftMinRating by remember(initialMinRating) { mutableStateOf(initialMinRating) }
    val liveCount = remember(draftMaxPrice, draftMinRating) {
        previewCount(draftMaxPrice, draftMinRating)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = stringResource(R.string.search_filters_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.search_filters_subtitle),
                fontSize = 14.sp,
                color = Muted
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.search_filters_price_section),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OptionChip(
                    label = stringResource(R.string.search_price_any),
                    selected = draftMaxPrice == null,
                    onClick = { draftMaxPrice = null }
                )
                SearchPriceCap.entries.forEach { cap ->
                    OptionChip(
                        label = stringResource(R.string.search_price_up_to, cap.maxFcfa),
                        selected = draftMaxPrice == cap.maxFcfa,
                        onClick = { draftMaxPrice = cap.maxFcfa }
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.search_filters_rating_section),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OptionChip(
                    label = stringResource(R.string.search_rating_any),
                    selected = draftMinRating == null,
                    onClick = { draftMinRating = null }
                )
                OptionChip(
                    label = stringResource(R.string.search_rating_4_plus),
                    selected = draftMinRating == 4.0f,
                    onClick = { draftMinRating = 4.0f }
                )
                OptionChip(
                    label = stringResource(R.string.search_rating_45_plus),
                    selected = draftMinRating == 4.5f,
                    onClick = { draftMinRating = 4.5f }
                )
            }

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                label = stringResource(R.string.search_filters_cta, liveCount),
                onClick = { onApply(draftMaxPrice, draftMinRating) }
            )
        }
    }
}

@Composable
private fun OptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (selected) Ink else SurfaceBg,
        animationSpec = TogoMotion.tweenNormal(),
        label = "optBg"
    )
    val fg by animateColorAsState(
        targetValue = if (selected) Color.White else Ink,
        animationSpec = TogoMotion.tweenNormal(),
        label = "optFg"
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
