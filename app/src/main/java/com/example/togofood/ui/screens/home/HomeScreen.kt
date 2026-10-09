package com.example.togofood.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.data.repository.FavoritesRepository
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.ui.components.HomeCategoryRow
import com.example.togofood.ui.components.HomeEmptyResults
import com.example.togofood.ui.components.HomeFilterChips
import com.example.togofood.ui.components.HomePriceRangeBottomSheet
import com.example.togofood.ui.components.HomeResultsBar
import com.example.togofood.ui.components.MapSnippetCard
import com.example.togofood.ui.components.PreparationCard
import com.example.togofood.ui.components.PreparationCardSkeleton
import com.example.togofood.ui.components.SectionHeader
import com.example.togofood.ui.components.WidePromoCarousel
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onSellerClick: (String) -> Unit = {},
    onPreparationClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMapClick: () -> Unit = {}
) {
    val nearby by viewModel.nearby.collectAsState()
    val availableNow by viewModel.availableNow.collectAsState()
    val popular by viewModel.popular.collectAsState()
    val favIds by FavoritesRepository.ids.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val filterCounts by viewModel.filterCounts.collectAsState()
    val results by viewModel.results.collectAsState()
    val isFiltering by viewModel.isFiltering.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val openStatusBySeller by viewModel.openStatusBySeller.collectAsState()
    val priceRange by viewModel.priceRange.collectAsState()
    val showPriceSheet by viewModel.showPriceSheet.collectAsState()

    if (showPriceSheet) {
        HomePriceRangeBottomSheet(
            currentRange = priceRange,
            previewCount = viewModel::previewResultCount,
            onDismiss = viewModel::dismissPriceSheet,
            onConfirm = viewModel::setPriceRange
        )
    }

    HomeScreenContent(
        isLoading = isLoading,
        nearby = nearby,
        availableNow = availableNow,
        popular = popular,
        favIds = favIds,
        categories = categories,
        selectedCategory = selectedCategory,
        filters = filters,
        filterCounts = filterCounts,
        priceActive = HomeFilter.PRICE_RANGE in filters && priceRange != null,
        results = results,
        isFiltering = isFiltering,
        openStatusBySeller = openStatusBySeller,
        onCategorySelect = viewModel::selectCategory,
        onFilterClick = viewModel::onFilterClick,
        onReset = viewModel::resetAll,
        onPreparationClick = onPreparationClick,
        onSearchClick = onSearchClick,
        onMapClick = onMapClick
    )
}

@Composable
fun HomeScreenContent(
    isLoading: Boolean,
    nearby: List<Preparation>,
    availableNow: List<Preparation>,
    popular: List<Preparation>,
    favIds: Set<String> = emptySet(),
    categories: List<Category> = emptyList(),
    selectedCategory: Category? = null,
    filters: Set<HomeFilter> = emptySet(),
    filterCounts: HomeFilterCounts = HomeFilterCounts(),
    priceActive: Boolean = false,
    results: List<Preparation> = emptyList(),
    isFiltering: Boolean = false,
    openStatusBySeller: Map<String, SellerOpenStatus> = emptyMap(),
    onCategorySelect: (Category?) -> Unit = {},
    onFilterClick: (HomeFilter) -> Unit = {},
    onReset: () -> Unit = {},
    onPreparationClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMapClick: () -> Unit = {}
) {
    val currentLocation = stringResource(R.string.home_location_placeholder)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        item {
            Column(modifier = Modifier.togoEnter()) {
                Spacer(modifier = Modifier.height(12.dp))
                HomeHeader(
                    currentLocation = currentLocation,
                    onLocationClick = { /* TODO: navigate to location picker */ },
                    onSearchClick = onSearchClick,
                    onProfileClick = { /* TODO: navigate to profile */ },
                    onNotificationsClick = { /* TODO: navigate to notifications */ },
                    onCartClick = { /* TODO: navigate to cart */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
                HomeCategoryRow(
                    categories = categories,
                    selected = selectedCategory,
                    onSelect = onCategorySelect
                )
                Spacer(modifier = Modifier.height(10.dp))
                HomeFilterChips(
                    active = filters,
                    counts = filterCounts,
                    priceActive = priceActive,
                    onFilterClick = onFilterClick
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        item {
            AnimatedContent(
                targetState = isFiltering,
                transitionSpec = { TogoMotion.contentSwitch() },
                modifier = Modifier.animateContentSize(TogoMotion.tweenNormal()),
                label = "homeContentTransition"
            ) { filtering ->
                if (filtering) {
                    ResultsContent(
                        isLoading = isLoading,
                        results = results,
                        favIds = favIds,
                        openStatusBySeller = openStatusBySeller,
                        onReset = onReset,
                        onPreparationClick = onPreparationClick,
                        onMapClick = onMapClick
                    )
                } else {
                    DiscoveryContent(
                        isLoading = isLoading,
                        popular = popular,
                        availableNow = availableNow,
                        nearby = nearby,
                        favIds = favIds,
                        openStatusBySeller = openStatusBySeller,
                        onPreparationClick = onPreparationClick,
                        onMapClick = onMapClick
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun DiscoveryContent(
    isLoading: Boolean,
    popular: List<Preparation>,
    availableNow: List<Preparation>,
    nearby: List<Preparation>,
    favIds: Set<String>,
    openStatusBySeller: Map<String, SellerOpenStatus>,
    onPreparationClick: (String) -> Unit,
    onMapClick: () -> Unit
) {
    Column {
        FoodSection(
            isLoading = isLoading,
            title = stringResource(R.string.home_section_popular),
            items = popular,
            favIds = favIds,
            openStatusBySeller = openStatusBySeller,
            onPreparationClick = onPreparationClick,
            modifier = Modifier.togoEnter(delayMs = 40)
        )

        if (isLoading || availableNow.isNotEmpty()) {
            Column(modifier = Modifier.togoEnter(delayMs = 90)) {
                WidePromoCarousel(
                    preparations = availableNow,
                    promoLabel = stringResource(R.string.home_promo_label),
                    onPreparationClick = { onPreparationClick(it.uuid) }
                )
                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .togoEnter(delayMs = 130)
        ) {
            MapSnippetCard(onClick = onMapClick)
        }
        Spacer(modifier = Modifier.height(28.dp))

        FoodSection(
            isLoading = isLoading,
            title = stringResource(R.string.home_section_nearby),
            items = nearby,
            favIds = favIds,
            openStatusBySeller = openStatusBySeller,
            onPreparationClick = onPreparationClick,
            modifier = Modifier.togoEnter(delayMs = 170)
        )
    }
}

@Composable
private fun ResultsContent(
    isLoading: Boolean,
    results: List<Preparation>,
    favIds: Set<String>,
    openStatusBySeller: Map<String, SellerOpenStatus>,
    onReset: () -> Unit,
    onPreparationClick: (String) -> Unit,
    onMapClick: () -> Unit
) {
    Column {
        HomeResultsBar(count = if (isLoading) 0 else results.size, onReset = onReset)
        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Column(
                    Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PreparationCardSkeleton()
                            PreparationCardSkeleton()
                        }
                    }
                }
            }

            results.isEmpty() -> HomeEmptyResults()

            else -> {
                Column(
                    Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    results.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { prep ->
                                PreparationCard(
                                    preparation = prep,
                                    isFavorite = prep.uuid in favIds,
                                    sellerOpenStatus = openStatusBySeller[prep.sellerId]
                                        ?: SellerOpenStatus.OPEN,
                                    onClick = { onPreparationClick(prep.uuid) },
                                    onFavoriteClick = { FavoritesRepository.toggle(prep.uuid) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            MapSnippetCard(onClick = onMapClick)
        }
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun FoodSection(
    isLoading: Boolean,
    title: String,
    items: List<Preparation>,
    favIds: Set<String>,
    openStatusBySeller: Map<String, SellerOpenStatus>,
    onPreparationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isLoading && items.isEmpty()) return

    Column(modifier = modifier) {
        SectionHeader(title = title, onSeeAll = null)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (isLoading) {
                items(4) { PreparationCardSkeleton() }
            } else {
                items(items, key = { it.uuid }) { prep ->
                    PreparationCard(
                        preparation = prep,
                        isFavorite = prep.uuid in favIds,
                        sellerOpenStatus = openStatusBySeller[prep.sellerId]
                            ?: SellerOpenStatus.OPEN,
                        onClick = { onPreparationClick(prep.uuid) },
                        onFavoriteClick = { FavoritesRepository.toggle(prep.uuid) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun LocationHeader(address: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = stringResource(R.string.cd_location),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = address,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun HomeHeader(
    currentLocation: String,
    onLocationClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Column {
        // Top row: Greeting + Profile/Notifications/Cart
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_greeting),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.home_search_prompt),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onNotificationsClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = onCartClick) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Panier", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Default.Person, contentDescription = "Profil", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                }
            }
        }

        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clickable { onSearchClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.cd_search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                stringResource(R.string.home_search_placeholder),
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        // Clickable location header
        Spacer(modifier = Modifier.height(8.dp))
        LocationHeader(address = currentLocation, onClick = onLocationClick)
    }
}
