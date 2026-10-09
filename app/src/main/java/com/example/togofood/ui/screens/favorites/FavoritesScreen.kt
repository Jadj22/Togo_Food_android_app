package com.example.togofood.ui.screens.favorites

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.togofood.ui.components.PreparationCard
import com.example.togofood.ui.components.UnderlineTabs
import com.example.togofood.ui.components.formatDistance
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter

private val Ink = Color(0xFF111111)
private val Muted = Color(0xFF666666)
private val SoftCream = Color(0xFFFFF8F3)
private val OrangeTint = Color(0xFFFFEDE3)
private val OpenGreen = Color(0xFF2E7D32)

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel(),
    onPreparationClick: (String) -> Unit = {},
    onSellerClick: (String) -> Unit = {},
    onDiscoverClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    AnimatedContent(
        targetState = state.isCompletelyEmpty,
        transitionSpec = { TogoMotion.contentSwitch() },
        label = "favoritesContentTransition"
    ) { isEmpty ->
        if (isEmpty) {
            EmptyFavorites(onDiscoverClick = onDiscoverClick)
        } else {
            FavoritesContent(
                state = state,
                onTabSelected = viewModel::selectTab,
                onAssietteFilter = viewModel::setAssietteFilter,
                onPreparationClick = onPreparationClick,
                onSellerClick = onSellerClick,
                onToggleFavorite = viewModel::toggle,
                onToggleSeller = viewModel::toggleSeller,
                onDiscoverClick = onDiscoverClick
            )
        }
    }
}

@Composable
private fun FavoritesContent(
    state: FavoritesUiState,
    onTabSelected: (FavoritesTab) -> Unit,
    onAssietteFilter: (AssietteFilter) -> Unit,
    onPreparationClick: (String) -> Unit,
    onSellerClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleSeller: (String) -> Unit,
    onDiscoverClick: () -> Unit
) {
    val tabIndex = if (state.selectedTab == FavoritesTab.ASSIETTES) 0 else 1

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        FavoritesHeader(
            prepCount = state.preparations.size,
            spotCount = state.sellers.size,
            openSpots = state.openSpots
        )

        UnderlineTabs(
            titles = listOf(
                stringResource(R.string.favorites_tab_assiettes),
                stringResource(R.string.favorites_tab_spots)
            ),
            counts = listOf(state.preparations.size, state.sellers.size),
            selectedIndex = tabIndex,
            onTabClick = { index ->
                onTabSelected(if (index == 0) FavoritesTab.ASSIETTES else FavoritesTab.SPOTS)
            }
        )

        AnimatedContent(
            targetState = state.selectedTab,
            transitionSpec = { TogoMotion.contentSwitch() },
            label = "favoritesTabBody",
            modifier = Modifier.weight(1f)
        ) { tab ->
            when (tab) {
                FavoritesTab.ASSIETTES -> AssiettesPane(
                    preparations = state.preparations,
                    filtered = state.filteredPreparations,
                    filter = state.assietteFilter,
                    onFilter = onAssietteFilter,
                    onPreparationClick = onPreparationClick,
                    onToggleFavorite = onToggleFavorite,
                    onDiscoverClick = onDiscoverClick
                )
                FavoritesTab.SPOTS -> SpotsPane(
                    sellers = state.sellers,
                    onSellerClick = onSellerClick,
                    onToggleSeller = onToggleSeller,
                    onDiscoverClick = onDiscoverClick
                )
            }
        }
    }
}

@Composable
private fun FavoritesHeader(
    prepCount: Int,
    spotCount: Int,
    openSpots: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SoftCream, Color.White)
                )
            )
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
            .togoEnter()
    ) {
        Text(
            text = stringResource(R.string.favorites_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Ink
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.favorites_subtitle),
            fontSize = 14.sp,
            color = Muted,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = when {
                openSpots > 0 -> stringResource(
                    R.string.favorites_summary_open,
                    prepCount,
                    spotCount,
                    openSpots
                )
                else -> stringResource(R.string.favorites_summary, prepCount, spotCount)
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandOrange
        )
    }
}

@Composable
private fun AssiettesPane(
    preparations: List<Preparation>,
    filtered: List<Preparation>,
    filter: AssietteFilter,
    onFilter: (AssietteFilter) -> Unit,
    onPreparationClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDiscoverClick: () -> Unit
) {
    when {
        preparations.isEmpty() -> TabEmptyState(
            icon = Icons.Default.FavoriteBorder,
            title = stringResource(R.string.favorites_empty_assiettes_title),
            subtitle = stringResource(R.string.favorites_empty_assiettes_subtitle),
            cta = stringResource(R.string.favorites_discover),
            onCta = onDiscoverClick
        )
        else -> Column(Modifier.fillMaxSize()) {
            AssietteFilterRow(selected = filter, onSelect = onFilter)
            if (filtered.isEmpty()) {
                TabEmptyState(
                    icon = Icons.Default.FavoriteBorder,
                    title = stringResource(R.string.favorites_filter_empty_title),
                    subtitle = stringResource(R.string.favorites_filter_empty_subtitle),
                    cta = null,
                    onCta = {}
                )
            } else {
                AssiettesGrid(
                    favorites = filtered,
                    onPreparationClick = onPreparationClick,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

@Composable
private fun AssietteFilterRow(
    selected: AssietteFilter,
    onSelect: (AssietteFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MoodChip(
            label = stringResource(R.string.favorites_filter_all),
            selected = selected == AssietteFilter.TOUT,
            onClick = { onSelect(AssietteFilter.TOUT) }
        )
        MoodChip(
            label = stringResource(R.string.favorites_filter_hot),
            selected = selected == AssietteFilter.CHAUD,
            onClick = { onSelect(AssietteFilter.CHAUD) }
        )
        MoodChip(
            label = stringResource(R.string.favorites_filter_nearby),
            selected = selected == AssietteFilter.A_DEUX_PAS,
            onClick = { onSelect(AssietteFilter.A_DEUX_PAS) }
        )
    }
}

@Composable
private fun MoodChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (selected) Ink else SoftCream,
        animationSpec = TogoMotion.tweenNormal(),
        label = "moodBg"
    )
    val fg by animateColorAsState(
        targetValue = if (selected) Color.White else Ink,
        animationSpec = TogoMotion.tweenNormal(),
        label = "moodFg"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AssiettesGrid(
    favorites: List<Preparation>,
    onPreparationClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(favorites, key = { it.uuid }) { prep ->
            PreparationCard(
                preparation = prep,
                isFavorite = true,
                onClick = { onPreparationClick(prep.uuid) },
                onFavoriteClick = { onToggleFavorite(prep.uuid) },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            )
        }
    }
}

@Composable
private fun SpotsPane(
    sellers: List<Seller>,
    onSellerClick: (String) -> Unit,
    onToggleSeller: (String) -> Unit,
    onDiscoverClick: () -> Unit
) {
    if (sellers.isEmpty()) {
        TabEmptyState(
            icon = Icons.Default.LocationOn,
            title = stringResource(R.string.favorites_empty_spots_title),
            subtitle = stringResource(R.string.favorites_empty_spots_subtitle),
            cta = stringResource(R.string.favorites_discover),
            onCta = onDiscoverClick
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(sellers, key = { it.uuid }) { seller ->
            FavoriteSpotRow(
                seller = seller,
                onClick = { onSellerClick(seller.uuid) },
                onToggleFavorite = { onToggleSeller(seller.uuid) }
            )
            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun FavoriteSpotRow(
    seller: Seller,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isOpen = seller.SellerOpenStatus == SellerOpenStatus.OPEN
    val statusLabel = when (seller.SellerOpenStatus) {
        SellerOpenStatus.OPEN -> stringResource(R.string.seller_open)
        SellerOpenStatus.CLOSED -> stringResource(R.string.seller_closed)
        SellerOpenStatus.OPENING_SOON -> stringResource(R.string.seller_opening_soon)
    }
    val statusColor = when (seller.SellerOpenStatus) {
        SellerOpenStatus.OPEN -> OpenGreen
        SellerOpenStatus.OPENING_SOON -> BrandOrange
        SellerOpenStatus.CLOSED -> Muted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isOpen) OrangeTint else Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            Text("🏪", fontSize = 26.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = seller.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (seller.isVerified) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "✓",
                        color = BrandOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(
                    R.string.favorites_spot_meta,
                    seller.zone,
                    seller.distanceMeters.formatDistance()
                ),
                fontSize = 13.sp,
                color = Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = statusLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }

        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.cd_favorite),
                tint = BrandOrange
            )
        }
    }
}

@Composable
private fun TabEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    cta: String?,
    onCta: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .togoEnter(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = BrandOrange.copy(alpha = 0.45f),
            modifier = Modifier.size(52.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Muted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        if (cta != null) {
            Spacer(Modifier.height(22.dp))
            Button(
                onClick = onCta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(cta, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun EmptyFavorites(onDiscoverClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SoftCream, Color.White)
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(32.dp)
            .togoEnter(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = BrandOrange.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.favorites_empty_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.favorites_empty_subtitle),
            fontSize = 14.sp,
            color = Muted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onDiscoverClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                stringResource(R.string.favorites_discover),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
