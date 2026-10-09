package com.example.togofood.ui.screens.sellerhub

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.FoodIcons
import com.example.togofood.ui.components.PreparationCard
import com.example.togofood.ui.components.PrimaryButton
import com.example.togofood.ui.components.formatFcfa
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter

private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)
private val SoftMuted = Color(0xFF8A8A8A)
private val PageBg = Color(0xFFFAFAFA)
private val CardBg = Color.White
private val SurfaceBg = Color(0xFFF5F5F5)
private val OpenGreen = Color(0xFF2E7D32)
private val WarningBg = Color(0xFFFFF3E0)
private val WarningInk = Color(0xFFC2410C)
private val StarGold = Color(0xFFF08C00)
private val PageHPadding = 16.dp
private val Radius = 14.dp

@Composable
fun SellerHubScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit = {},
    onScheduleClick: () -> Unit = {},
    onAddPrepClick: () -> Unit = {},
    onEditPrepClick: (String) -> Unit = {},
    onViewPublicClick: (String) -> Unit = {},
    viewModel: SellerHubViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        HubTopBar(onBackClick = onBackClick)

        when {
            state.missingSession || state.seller == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.seller_hub_missing),
                        color = TextSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
            else -> {
                val seller = state.seller!!
                val openStatus = if (state.isOpen) SellerOpenStatus.OPEN else SellerOpenStatus.CLOSED
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        HubDecisionHeader(
                            seller = seller,
                            isOpen = state.isOpen,
                            scheduleSummary = state.scheduleSummary,
                            heroCategoryId = state.heroCategoryId,
                            onOpenChange = viewModel::setShopOpen,
                            modifier = Modifier.togoEnter(delayMs = 40)
                        )
                    }

                    if (state.showEmptyStockWarning) {
                        item {
                            HubEmptyStockBanner(
                                pausedCount = state.unavailableCount,
                                onCta = { viewModel.setPrepFilter(HubPrepFilter.UNAVAILABLE) },
                                modifier = Modifier.togoEnter(delayMs = 55)
                            )
                        }
                    }

                    item {
                        HubActionChips(
                            onEditProfile = onEditProfileClick,
                            onSchedule = onScheduleClick,
                            onViewPublic = { onViewPublicClick(seller.uuid) },
                            modifier = Modifier.togoEnter(delayMs = 70)
                        )
                    }

                    item {
                        HubPrepsHeader(
                            totalCount = state.preparations.size,
                            shownCount = state.filteredPreparations.size,
                            layout = state.catalogLayout,
                            onLayoutChange = viewModel::setCatalogLayout,
                            onAddClick = onAddPrepClick,
                            modifier = Modifier.togoEnter(delayMs = 90)
                        )
                    }

                    if (state.preparations.isNotEmpty()) {
                        item {
                            HubPrepFilterChips(
                                selected = state.prepFilter,
                                total = state.preparations.size,
                                available = state.availableCount,
                                unavailable = state.unavailableCount,
                                onSelect = viewModel::setPrepFilter,
                                modifier = Modifier.togoEnter(delayMs = 100)
                            )
                        }
                    }

                    if (state.preparations.isEmpty()) {
                        item {
                            HubEmptyPreps(
                                onAddClick = onAddPrepClick,
                                modifier = Modifier.togoEnter(delayMs = 110)
                            )
                        }
                    } else if (state.filteredPreparations.isEmpty()) {
                        item {
                            HubFilterEmpty(
                                onShowAll = { viewModel.setPrepFilter(HubPrepFilter.ALL) },
                                modifier = Modifier.togoEnter(delayMs = 110)
                            )
                        }
                    } else {
                        item {
                            Text(
                                stringResource(R.string.seller_hub_prep_tap_edit),
                                fontSize = 12.sp,
                                color = SoftMuted,
                                modifier = Modifier
                                    .padding(horizontal = PageHPadding)
                                    .padding(top = 4.dp, bottom = 10.dp)
                            )
                        }
                        item {
                            when (state.catalogLayout) {
                                HubCatalogLayout.LIST -> HubPrepList(
                                    preparations = state.filteredPreparations,
                                    isAvailable = viewModel::isPreparationAvailable,
                                    onToggle = viewModel::setPreparationAvailable,
                                    onEdit = onEditPrepClick,
                                    modifier = Modifier.togoEnter(delayMs = 120)
                                )
                                HubCatalogLayout.GRID -> HubPrepGrid(
                                    preparations = state.filteredPreparations,
                                    openStatus = openStatus,
                                    isAvailable = viewModel::isPreparationAvailable,
                                    onToggle = viewModel::setPreparationAvailable,
                                    onEdit = onEditPrepClick,
                                    modifier = Modifier.togoEnter(delayMs = 120)
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun HubTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                tint = TextPrimary
            )
        }
        Text(
            stringResource(R.string.seller_hub_title),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Ancre décisionnelle : statut Ouvert/Fermé d'abord, puis identité boutique.
 * Stats Plats/Dispo volontairement absentes — déjà dans les chips filtre.
 */
@Composable
private fun HubDecisionHeader(
    seller: Seller,
    isOpen: Boolean,
    scheduleSummary: String,
    heroCategoryId: String?,
    onOpenChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val heroBg = heroCategoryId?.let { FoodIcons.pastelBackgrounds[it] }
        ?: Color(0xFFFFF3E8)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBg)
            .padding(bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PageHPadding)
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(Radius))
                .background(if (isOpen) OpenGreen.copy(alpha = 0.10f) else SurfaceBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(
                        if (isOpen) R.string.seller_hub_open else R.string.seller_hub_closed
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = if (isOpen) OpenGreen else TextPrimary
                )
                Text(
                    stringResource(
                        if (isOpen) R.string.seller_hub_open_hint
                        else R.string.seller_hub_open_hint_closed
                    ),
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
            Switch(
                checked = isOpen,
                onCheckedChange = onOpenChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = OpenGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                )
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PageHPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(Radius))
                    .background(heroBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = heroCategoryId?.let { FoodIcons.emojiFor(it) } ?: "🏪",
                    fontSize = 34.sp
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    seller.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SoftMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.seller_zone_landmark, seller.zone, seller.landmark),
                        fontSize = 13.sp,
                        color = SoftMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (seller.rating > 0f) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            stringResource(
                                R.string.seller_rating_reviews,
                                seller.rating,
                                seller.reviewCount
                            ),
                            fontSize = 12.sp,
                            color = SoftMuted
                        )
                    }
                }
                if (scheduleSummary.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.seller_hub_hours_line, scheduleSummary),
                        fontSize = 12.sp,
                        color = SoftMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (seller.description.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                seller.description,
                fontSize = 13.sp,
                color = SoftMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = PageHPadding)
            )
        }
    }
}

@Composable
private fun HubEmptyStockBanner(
    pausedCount: Int,
    onCta: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PageHPadding)
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(Radius))
            .background(WarningBg)
            .padding(14.dp)
    ) {
        Text(
            stringResource(R.string.seller_hub_empty_stock_title),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            color = WarningInk
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.seller_hub_empty_stock_body),
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 18.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            stringResource(R.string.seller_hub_empty_stock_cta, pausedCount),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = BrandOrange,
            modifier = Modifier.clickable(onClick = onCta)
        )
    }
}

@Composable
private fun HubActionChips(
    onEditProfile: () -> Unit,
    onSchedule: () -> Unit,
    onViewPublic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .background(CardBg)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = PageHPadding, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionChip(
            label = stringResource(R.string.seller_hub_edit_profile),
            onClick = onEditProfile
        )
        ActionChip(
            label = stringResource(R.string.seller_hub_schedule),
            onClick = onSchedule
        )
        ActionChip(
            label = stringResource(R.string.seller_hub_view_public),
            onClick = onViewPublic
        )
    }
}

@Composable
private fun ActionChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius))
            .background(SurfaceBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun HubPrepsHeader(
    totalCount: Int,
    shownCount: Int,
    layout: HubCatalogLayout,
    onLayoutChange: (HubCatalogLayout) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = PageHPadding, top = 20.dp, bottom = 8.dp, end = PageHPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.seller_hub_preps_title),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = TextPrimary
            )
            if (totalCount > 0) {
                Text(
                    if (shownCount == totalCount) {
                        stringResource(R.string.seller_hub_preps_count, totalCount)
                    } else {
                        stringResource(R.string.seller_hub_preps_shown, shownCount, totalCount)
                    },
                    fontSize = 12.sp,
                    color = SoftMuted
                )
            }
        }
        if (totalCount > 0) {
            HubLayoutToggle(
                layout = layout,
                onLayoutChange = onLayoutChange
            )
            Spacer(Modifier.width(8.dp))
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius))
                .background(BrandOrange.copy(alpha = 0.12f))
                .clickable(onClick = onAddClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = BrandOrange,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                stringResource(R.string.seller_hub_add_prep),
                color = BrandOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun HubLayoutToggle(
    layout: HubCatalogLayout,
    onLayoutChange: (HubCatalogLayout) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius))
            .background(SurfaceBg)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        LayoutChip(
            label = stringResource(R.string.seller_hub_layout_list),
            selected = layout == HubCatalogLayout.LIST,
            onClick = { onLayoutChange(HubCatalogLayout.LIST) }
        )
        LayoutChip(
            label = stringResource(R.string.seller_hub_layout_grid),
            selected = layout == HubCatalogLayout.GRID,
            onClick = { onLayoutChange(HubCatalogLayout.GRID) }
        )
    }
}

@Composable
private fun LayoutChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg by animateColorAsState(
        if (selected) TextPrimary else Color.Transparent,
        TogoMotion.tweenNormal(),
        label = "hubLayoutBg"
    )
    val fg by animateColorAsState(
        if (selected) Color.White else SoftMuted,
        TogoMotion.tweenNormal(),
        label = "hubLayoutFg"
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(11.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

@Composable
private fun HubPrepFilterChips(
    selected: HubPrepFilter,
    total: Int,
    available: Int,
    unavailable: Int,
    onSelect: (HubPrepFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = PageHPadding)
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            label = stringResource(R.string.seller_hub_filter_all, total),
            selected = selected == HubPrepFilter.ALL,
            onClick = { onSelect(HubPrepFilter.ALL) }
        )
        FilterChip(
            label = stringResource(R.string.seller_hub_filter_available, available),
            selected = selected == HubPrepFilter.AVAILABLE,
            onClick = { onSelect(HubPrepFilter.AVAILABLE) }
        )
        FilterChip(
            label = stringResource(R.string.seller_hub_filter_unavailable, unavailable),
            selected = selected == HubPrepFilter.UNAVAILABLE,
            onClick = { onSelect(HubPrepFilter.UNAVAILABLE) }
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg by animateColorAsState(
        if (selected) TextPrimary else SurfaceBg,
        TogoMotion.tweenNormal(),
        label = "hubFilterBg"
    )
    val fg by animateColorAsState(
        if (selected) Color.White else TextPrimary,
        TogoMotion.tweenNormal(),
        label = "hubFilterFg"
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

@Composable
private fun HubFilterEmpty(
    onShowAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PageHPadding, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.seller_hub_filter_empty),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.seller_hub_filter_empty_cta),
            color = BrandOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.clickable(onClick = onShowAll)
        )
    }
}

@Composable
private fun HubEmptyPreps(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PageHPadding, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(Radius))
                .background(Color(0xFFFFF3E8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = BrandOrange,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.seller_hub_preps_empty),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.seller_hub_preps_empty_hint),
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            label = stringResource(R.string.seller_hub_add_prep),
            onClick = onAddClick,
        )
    }
}

@Composable
private fun HubPrepList(
    preparations: List<Preparation>,
    isAvailable: (Preparation) -> Boolean,
    onToggle: (String, Boolean) -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PageHPadding)
    ) {
        preparations.forEach { prep ->
            HubPrepListRow(
                preparation = prep,
                available = isAvailable(prep),
                onToggle = { onToggle(prep.uuid, it) },
                onClick = { onEdit(prep.uuid) }
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun HubPrepListRow(
    preparation: Preparation,
    available: Boolean,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius))
            .background(CardBg)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .alpha(if (available) 1f else 0.6f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(Radius))
                .background(
                    FoodIcons.pastelBackgrounds[preparation.categoryId] ?: SurfaceBg
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(FoodIcons.emojiFor(preparation.categoryId), fontSize = 28.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                preparation.name,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                stringResource(R.string.card_price_from, preparation.basePrice.formatFcfa()),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = OpenGreen
            )
            Text(
                stringResource(
                    if (available) R.string.seller_hub_prep_available
                    else R.string.seller_hub_prep_paused
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (available) OpenGreen else SoftMuted
            )
        }
        Switch(
            checked = available,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = OpenGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBDBDBD),
            )
        )
    }
}

@Composable
private fun HubPrepGrid(
    preparations: List<Preparation>,
    openStatus: SellerOpenStatus,
    isAvailable: (Preparation) -> Boolean,
    onToggle: (String, Boolean) -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PageHPadding)
    ) {
        preparations.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { prep ->
                    HubPrepCard(
                        preparation = prep,
                        openStatus = openStatus,
                        available = isAvailable(prep),
                        onToggle = { onToggle(prep.uuid, it) },
                        onClick = { onEdit(prep.uuid) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HubPrepCard(
    preparation: Preparation,
    openStatus: SellerOpenStatus,
    available: Boolean,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.alpha(if (available) 1f else 0.55f)) {
        PreparationCard(
            preparation = preparation,
            isFavorite = false,
            sellerOpenStatus = openStatus,
            onClick = onClick,
            showFavorite = false,
            showSellerOpenStatus = false,
            showSellerName = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius))
                .background(SurfaceBg)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(
                    if (available) R.string.seller_hub_prep_available
                    else R.string.seller_hub_prep_paused
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (available) OpenGreen else SoftMuted,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = available,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = OpenGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                )
            )
        }
    }
}
