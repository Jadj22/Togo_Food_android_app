package com.example.togofood.ui.screens.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.domain.model.SellerScheduleResolver
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.MenuItemRow
import com.example.togofood.ui.components.SearchResultSkeleton
import com.example.togofood.ui.components.UnderlineTabs
import com.example.togofood.ui.components.formatDistance
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter
import kotlinx.coroutines.launch

private val Ink           = Color(0xFF111111)
private val Muted         = Color(0xFF6B6B6B)
private val SurfaceBg     = Color(0xFFF5F5F5)
private val OrangeStrong  = Color(0xFFC2410C)
private val OrangeTint    = Color(0xFFFFEDE3)
private val OpenGreen     = Color(0xFF2E7D32)
private val ClosedRed     = Color(0xFFC62828)
private val SoftMuted     = Color(0xFF8A8A8A)
private val StarGold      = Color(0xFFF08C00)
private val SoonOrange    = Color(0xFFF08C00)
private val Radius        = 14.dp

private val TextPrimary = Color(0xFF111111)
private val ChipBg = Color(0xFFF5F5F5)

private const val TAB_PREPARATIONS = 0
private const val TAB_SELLERS = 1

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onItemClick: (String) -> Unit = {},
    onSellerClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    if (state.showFiltersSheet) {
        SearchFiltersSheet(
            initialMaxPrice = state.filterMaxPrice,
            initialMinRating = state.filterMinRating,
            previewCount = viewModel::previewResultCount,
            onDismiss = viewModel::dismissFiltersSheet,
            onApply = viewModel::applyAdvancedFilters
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔍", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (state.query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            color = Muted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    BasicTextField(
                        value = state.query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            color = Ink,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(Color(0xFFE65100))
                    )
                }
                if (state.query.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.search_clear),
                        tint = Muted,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.onQueryChange("") }
                    )
                }
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SearchFilterChip(
                    label = stringResource(
                        R.string.search_filter_with_count,
                        stringResource(R.string.search_filter_open),
                        state.countOpen
                    ),
                    isSelected = state.filterOpenNow,
                    onClick = { viewModel.toggleFilterOpenNow() }
                )
            }
            item {
                SearchFilterChip(
                    label = stringResource(
                        R.string.search_filter_with_count,
                        stringResource(R.string.search_filter_popular),
                        state.countPopular
                    ),
                    isSelected = state.filterPopular,
                    onClick = { viewModel.toggleFilterPopular() }
                )
            }
            item {
                SearchFilterChip(
                    label = stringResource(
                        R.string.search_filter_with_count,
                        stringResource(R.string.search_filter_nearby),
                        state.countNearby
                    ),
                    isSelected = state.filterNearby,
                    onClick = { viewModel.toggleFilterNearby() }
                )
            }
            item {
                AdvancedFiltersChip(
                    active = state.hasAdvancedFilter,
                    onClick = viewModel::openFiltersSheet
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = state.isSearching,
            transitionSpec = { TogoMotion.contentSwitch() },
            label = "searchContent",
            modifier = Modifier.weight(1f)
        ) { searching ->
            if (!searching) {
                DiscoveryContent(state = state, viewModel = viewModel)
            } else {
                ResultsContent(
                    state = state,
                    pagerState = pagerState,
                    onTabClick = { page ->
                        scope.launch { pagerState.animateScrollToPage(page) }
                    },
                    onItemClick = onItemClick,
                    onSellerClick = onSellerClick,
                    onClear = viewModel::clearFilters
                )
            }
        }
    }
}

@Composable
private fun AdvancedFiltersChip(
    active: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (active) Ink else SurfaceBg,
        animationSpec = TogoMotion.tweenNormal(),
        label = "advBg"
    )
    val fg by animateColorAsState(
        targetValue = if (active) Color.White else Ink,
        animationSpec = TogoMotion.tweenNormal(),
        label = "advFg"
    )
    val filtersCd = stringResource(R.string.search_filters_cd)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius))
            .background(bg)
            .semantics { contentDescription = filtersCd }
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.search_filter_more),
            color = fg,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        if (active) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(OrangeStrong)
            )
        }
    }
}

@Composable
private fun DiscoveryContent(
    state: SearchUiState,
    viewModel: SearchViewModel
) {
    Column(Modifier.fillMaxSize().togoEnter()) {
        Text(
            text = stringResource(R.string.search_discover_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.categories) { category ->
                CategorySearchCard(
                    category = category,
                    onClick = { viewModel.searchCategory(category.label) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ResultsContent(
    state: SearchUiState,
    pagerState: PagerState,
    onTabClick: (Int) -> Unit,
    onItemClick: (String) -> Unit,
    onSellerClick: (String) -> Unit,
    onClear: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        UnderlineTabs(
            titles = listOf(
                stringResource(R.string.search_tab_preparations),
                stringResource(R.string.search_tab_sellers)
            ),
            counts = listOf(state.results.size, state.sellerResults.size),
            selectedIndex = pagerState.currentPage,
            selectedOffsetFraction = pagerState.currentPageOffsetFraction,
            onTabClick = onTabClick
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val list = if (page == TAB_PREPARATIONS) state.results else state.sellerResults

            when {
                state.isLoading -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(6) {
                            SearchResultSkeleton()
                            HorizontalDivider(
                                color = Color(0xFFF0F0F0),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                list.isEmpty() -> {
                    EmptyResults(
                        emoji = if (page == TAB_PREPARATIONS) "🍽️" else "🏪",
                        title = stringResource(
                            if (page == TAB_PREPARATIONS) {
                                R.string.search_empty_preparations
                            } else {
                                R.string.search_empty_sellers
                            }
                        ),
                        hasFilters = state.hasActiveFilter,
                        onClear = onClear
                    )
                }

                else -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        if (page == TAB_PREPARATIONS) {
                            items(state.results) { item ->
                                MenuItemRow(
                                    preparation = item,
                                    onClick = { onItemClick(item.uuid) }
                                )
                                HorizontalDivider(
                                    color = Color(0xFFF0F0F0),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        } else {
                            items(state.sellerResults) { seller ->
                                SellerResultRow(
                                    seller = seller,
                                    onClick = { onSellerClick(seller.uuid) }
                                )
                                HorizontalDivider(
                                    color = Color(0xFFF0F0F0),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Ink else SurfaceBg,
        animationSpec = TogoMotion.tweenNormal(),
        label = "filterBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Ink,
        animationSpec = TogoMotion.tweenNormal(),
        label = "filterText"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun SearchFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        if (isSelected) TextPrimary else ChipBg,
        TogoMotion.tweenNormal(),
        label = "searchFilterBg"
    )
    val textColor by animateColorAsState(
        if (isSelected) Color.White else TextPrimary,
        TogoMotion.tweenNormal(),
        label = "searchFilterText"
    )

    Row(
        modifier
            .heightIn(min = 40.dp)
            .clip(RoundedCornerShape(Radius))
            .background(bgColor)
            .toggleable(
                value = isSelected,
                role = Role.Checkbox,
                onValueChange = { onClick() }
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1
        )
    }
}

@Composable
private fun SellerResultRow(
    seller: Seller,
    onClick: () -> Unit
) {
    val isOpen = seller.SellerOpenStatus == SellerOpenStatus.OPEN

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isOpen) OrangeTint else SurfaceBg),
            contentAlignment = Alignment.Center
        ) {
            Text("🏪", fontSize = 28.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = seller.name,
                    fontWeight = FontWeight.ExtraBold,
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
                        color = OrangeStrong,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            if (seller.description.isNotBlank()) {
                Text(
                    text = seller.description,
                    fontSize = 12.sp,
                    color = SoftMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

            Text(
                text = stringResource(
                    R.string.search_seller_location,
                    seller.zone,
                    seller.landmark,
                    seller.distanceMeters.formatDistance()
                ),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarGold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = stringResource(
                        R.string.seller_rating_reviews,
                        seller.rating,
                        seller.reviewCount
                    ),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SoftMuted
                )
            }

            SellerOpenBadge(status = seller.SellerOpenStatus)
        }
    }
}

@Composable
private fun SellerOpenBadge(status: SellerOpenStatus) {
    val (labelRes, color) = when (status) {
        SellerOpenStatus.OPEN -> R.string.seller_open to OpenGreen
        SellerOpenStatus.CLOSED -> R.string.seller_closed to ClosedRed
        SellerOpenStatus.OPENING_SOON -> R.string.seller_opening_soon to SoonOrange
    }
    check(labelRes != 0) { "Unhandled SellerOpenStatus: $status" }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(labelRes),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun EmptyResults(
    emoji: String,
    title: String,
    hasFilters: Boolean,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = 48.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (hasFilters) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onClear) {
                Text(
                    text = stringResource(R.string.search_reset_filters),
                    color = OrangeStrong
                )
            }
        }
    }
}

@Composable
private fun CategorySearchCard(
    category: Category,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceBg)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(
            text = category.label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = category.emoji,
            fontSize = 32.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
