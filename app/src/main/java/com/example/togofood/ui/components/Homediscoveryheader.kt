package com.example.togofood.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.R
import com.example.togofood.domain.model.Category
import com.example.togofood.ui.screens.home.HomeFilter
import com.example.togofood.ui.screens.home.HomeFilterCounts
import com.example.togofood.ui.theme.TogoMotion

private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)
private val Outline = Color(0xFFE0E0E0)
private val ChipBg = Color(0xFFF5F5F5)
private val SoftMuted = Color(0xFF8A8A8A)
private val Radius = 14.dp

// ───────────────────────── Catégories calmes (ne volent pas les plats) ─────────────────────────

@Composable
fun HomeCategoryRow(
    categories: List<Category>,
    selected: Category?,
    onSelect: (Category?) -> Unit
) {
    if (categories.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { cat ->
            val isSelected = selected?.id == cat.id
            val pastel = FoodIcons.pastelBackgrounds[cat.id] ?: FoodIcons.defaultPastel
            val borderColor by animateColorAsState(
                if (isSelected) BrandOrange else Color.Transparent,
                TogoMotion.tweenNormal(),
                label = "catBorder"
            )
            val bg by animateColorAsState(
                if (isSelected) pastel else ChipBg,
                TogoMotion.tweenNormal(),
                label = "catBg"
            )

            Column(
                Modifier
                    .width(64.dp)
                    .clip(RoundedCornerShape(Radius))
                    .selectable(selected = isSelected, role = Role.Tab) {
                        onSelect(if (isSelected) null else cat)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(Radius))
                        .background(bg)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(Radius)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(FoodIcons.emojiFor(cat.id), fontSize = 22.sp)
                }
                Spacer(Modifier.size(4.dp))
                Text(
                    cat.label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TextPrimary else SoftMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ───────────────────────── Filtres + counts ─────────────────────────

@Composable
fun HomeFilterChips(
    active: Set<HomeFilter>,
    counts: HomeFilterCounts,
    priceActive: Boolean,
    onFilterClick: (HomeFilter) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(HomeFilter.entries.toList()) { filter ->
            val isOn = when (filter) {
                HomeFilter.PRICE_RANGE -> priceActive
                else -> filter in active
            }
            val count = counts.forFilter(filter)
            val label = stringResource(filter.labelRes)
            val display = if (count >= 0) {
                stringResource(R.string.home_filter_with_count, label, count)
            } else {
                label
            }

            val bgColor by animateColorAsState(
                if (isOn) TextPrimary else ChipBg,
                TogoMotion.tweenNormal(),
                label = "filterBg"
            )
            val textColor by animateColorAsState(
                if (isOn) Color.White else TextPrimary,
                TogoMotion.tweenNormal(),
                label = "filterText"
            )

            Row(
                Modifier
                    .heightIn(min = 40.dp)
                    .clip(RoundedCornerShape(Radius))
                    .background(bgColor)
                    .clickable(role = Role.Checkbox) { onFilterClick(filter) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    display,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun HomeResultsBar(count: Int, onReset: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (count) {
                0 -> stringResource(R.string.home_results_none)
                1 -> stringResource(R.string.home_results_one)
                else -> stringResource(R.string.home_results_many, count)
            },
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Box(
            Modifier
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(Radius))
                .background(ChipBg)
                .clickable(role = Role.Button, onClick = onReset)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.home_reset_filters),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun HomeEmptyResults() {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🍽️", fontSize = 40.sp)
        Spacer(Modifier.size(8.dp))
        Text(
            stringResource(R.string.home_empty_title),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextPrimary
        )
        Spacer(Modifier.size(2.dp))
        Text(
            stringResource(R.string.home_empty_subtitle),
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

private data class PricePreset(val labelRes: Int, val range: IntRange)

private val PricePresets = listOf(
    PricePreset(R.string.home_price_lt_500, 0..500),
    PricePreset(R.string.home_price_500_1500, 500..1500),
    PricePreset(R.string.home_price_1500_3000, 1500..3000),
    PricePreset(R.string.home_price_3000_5000, 3000..5000),
    PricePreset(R.string.home_price_gt_5000, 5000..50_000)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomePriceRangeBottomSheet(
    currentRange: IntRange?,
    previewCount: (IntRange?) -> Int,
    onDismiss: () -> Unit,
    onConfirm: (IntRange?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draft by remember(currentRange) { mutableStateOf(currentRange) }
    val liveCount = remember(draft) { previewCount(draft) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                stringResource(R.string.home_price_sheet_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.home_price_sheet_subtitle),
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(Modifier.height(20.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriceChip(
                    label = stringResource(R.string.home_price_any),
                    selected = draft == null,
                    onClick = { draft = null }
                )
                PricePresets.forEach { preset ->
                    PriceChip(
                        label = stringResource(preset.labelRes),
                        selected = draft == preset.range,
                        onClick = { draft = preset.range }
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                label = stringResource(R.string.home_price_cta, liveCount),
                onClick = { onConfirm(draft) }
            )
        }
    }
}

@Composable
private fun PriceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        if (selected) TextPrimary else ChipBg,
        TogoMotion.tweenNormal(),
        label = "priceChipBg"
    )
    val fg by animateColorAsState(
        if (selected) Color.White else TextPrimary,
        TogoMotion.tweenNormal(),
        label = "priceChipFg"
    )
    Box(
        Modifier
            .clip(RoundedCornerShape(Radius))
            .background(bg)
            .border(1.dp, if (selected) TextPrimary else Outline, RoundedCornerShape(Radius))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}
