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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.ui.components.MenuItemRow
import com.example.togofood.ui.components.PreparationCard
import com.example.togofood.ui.components.SearchResultSkeleton
import com.example.togofood.ui.components.UnderlineTabs
import com.example.togofood.ui.components.formatDistance
import com.example.togofood.ui.theme.Dimens
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter
import kotlinx.coroutines.launch

private const val TAB_PREPARATIONS = 0
private const val TAB_SELLERS = 1

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onItemClick: (Preparation) -> Unit = {},
    onSellerClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) { focusRequester.requestFocus() }

    val searchContent by remember(state) {
        derivedStateOf {
            state.isSearching
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SpacingXxxl, vertical = Dimens.SpacingMd)
                    .clip(RoundedCornerShape(Dimens.RadiusFull))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .padding(horizontal = Dimens.SpacingXxxl, vertical = Dimens.SpacingMd),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_placeholder),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimens.IconSizeMedium)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingMd))
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = Dimens.TextSizeLg,
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
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = Dimens.TextSizeLg,
                                fontWeight = FontWeight.Medium
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    }
                    if (state.query.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.search_clear),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(Dimens.IconSizeLarge)
                                .clip(CircleShape)
                                .clickable { viewModel.onQueryChange("") }
                        )
                    }
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = Dimens.SpacingXxxl),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
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

            Spacer(modifier = Modifier.height(Dimens.SpacingMd))

            AnimatedContent(
                targetState = searchContent,
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
                        onItemClick = { viewModel.onPreparationOpened(it); onItemClick(it.uuid) },
                        onSellerClick = onSellerClick,
                        onClear = viewModel::clearFilters
                    )
                }
            }
        }

        if (state.showFiltersSheet) {
            SearchFiltersSheet(
                initialMaxPrice = state.filterMaxPrice,
                initialMinRating = state.filterMinRating,
                previewCount = viewModel::previewResultCount,
                onDismiss = viewModel::dismissFiltersSheet,
                onApply = viewModel::applyAdvancedFilters
            )
        }
    }
}

@Composable
private fun AdvancedFiltersChip(
    active: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceContainerHighest,
        animationSpec = TogoMotion.tweenNormal(),
        label = "advBg"
    )
    val fg by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
        animationSpec = TogoMotion.tweenNormal(),
        label = "advFg"
    )
    val filtersCd = stringResource(R.string.search_filters_cd)
    Row(
        modifier = Modifier
            .heightIn(min = Dimens.ChipMinHeight)
            .clip(RoundedCornerShape(Dimens.RadiusLarge))
            .background(bg)
            .semantics { contentDescription = filtersCd }
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.SpacingXxl, vertical = Dimens.SpacingMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
    ) {
        Text(
            text = stringResource(R.string.search_filter_more),
            color = fg,
            fontSize = Dimens.TextSizeSm,
            fontWeight = FontWeight.Bold
        )
        if (active) {
            Box(
                modifier = Modifier
                    .size(Dimens.IconSizeSmall)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
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
            fontSize = Dimens.TextSizeTitle,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = Dimens.SpacingXxxl, vertical = Dimens.SpacingMd)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(Dimens.SpacingXxxl),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXl)
        ) {
            items(state.categories) { category ->
                CategorySearchCard(
                    category = category,
                    onClick = { viewModel.searchCategory(category.label) }
                )
            }

            if (state.recentPreparations.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.search_recent_title),
                        fontSize = Dimens.TextSizeTitle,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = Dimens.SpacingXl)
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXl)
                    ) {
                        items(state.recentPreparations, key = { it.uuid }) { preparation ->
                            PreparationCard(
                                preparation = preparation,
                                onClick = { onItemClick(preparation) },
                                showFavorite = false,
                                showSellerName = true
                            )
                        }
                    }
                }
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
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.padding(horizontal = Dimens.SpacingXxxl)
                            )
                        }
                    }
                }

                state.error != null -> {
                    ErrorResults(
                        message = state.error!!,
                        onRetry = { viewModel.onQueryChange(state.query) }
                    )
                }

                list.isEmpty() -> {
                    EmptyResults(
                        emoji = if (page == TAB_PREPARATIONS) stringResource(R.string.search_emoji_restaurant) else stringResource(R.string.search_emoji_shop),
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
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.padding(horizontal = Dimens.SpacingXxxl)
                                )
                            }
                        } else {
                            items(state.sellerResults) { seller ->
                                SellerResultRow(
                                    seller = seller,
                                    onClick = { onSellerClick(seller.uuid) }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.padding(horizontal = Dimens.SpacingXxxl)
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
fun SearchFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceContainerHighest,
        TogoMotion.tweenNormal(),
        label = "searchFilterBg"
    )
    val textColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
        TogoMotion.tweenNormal(),
        label = "searchFilterText"
    )

    Row(
        modifier
            .heightIn(min = Dimens.ChipMinHeight)
            .clip(RoundedCornerShape(Dimens.RadiusLarge))
            .background(bgColor)
            .toggleable(
                value = isSelected,
                role = Role.Checkbox,
                onValueChange = { onClick() }
            )
            .padding(horizontal = Dimens.SpacingXxl, vertical = Dimens.SpacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = Dimens.TextSizeSm,
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
            .padding(horizontal = Dimens.SpacingXxxl, vertical = Dimens.SpacingXl),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.AvatarSizeSmall)
                .clip(RoundedCornerShape(Dimens.RadiusMedium))
                .background(if (isOpen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.search_emoji_shop),
                fontSize = Dimens.TextSizeDisplay
            )
        }

        Spacer(Modifier.width(Dimens.SpacingXl))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = seller.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = Dimens.TextSizeXl,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (seller.isVerified) {
                    Spacer(Modifier.width(Dimens.SpacingSm))
                    Text(
                        text = stringResource(R.string.search_emoji_check),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.TextSizeMd,
                        contentDescription = stringResource(R.string.seller_verified)
                    )
                }
            }

            if (seller.description.isNotBlank()) {
                Text(
                    text = seller.description,
                    fontSize = Dimens.TextSizeXs,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = Dimens.TextSizeMd
                )
            }

            Text(
                text = stringResource(
                    R.string.search_seller_location,
                    seller.zone,
                    seller.landmark,
                    seller.distanceMeters.formatDistance()
                ),
                fontSize = Dimens.TextSizeSm,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(Dimens.IconSizeMedium)
                )
                Text(
                    text = stringResource(
                        R.string.seller_rating_reviews,
                        seller.rating,
                        seller.reviewCount
                    ),
                    fontSize = Dimens.TextSizeXs,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SellerOpenBadge(status = seller.SellerOpenStatus)
        }
    }
}

@Composable
private fun SellerOpenBadge(status: SellerOpenStatus) {
    val (labelRes, color) = when (status) {
        SellerOpenStatus.OPEN -> R.string.seller_open to MaterialTheme.colorScheme.tertiary
        SellerOpenStatus.CLOSED -> R.string.seller_closed to MaterialTheme.colorScheme.error
        SellerOpenStatus.OPENING_SOON -> R.string.seller_opening_soon to MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.RadiusXXLarge))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = Dimens.SpacingMd, vertical = Dimens.SpacingXs)
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.IconSizeSmall)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(Dimens.SpacingSm))
        Text(
            text = stringResource(labelRes),
            fontSize = Dimens.TextSizeXs,
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
            .padding(Dimens.SpacingXXHuge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = Dimens.TextSizeEmoji)
        Spacer(Modifier.height(Dimens.SpacingXl))
        Text(
            text = title,
            fontSize = Dimens.TextSizeXxl,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (hasFilters) {
            Spacer(Modifier.height(Dimens.SpacingMd))
            TextButton(onClick = onClear) {
                Text(
                    text = stringResource(R.string.search_reset_filters),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ErrorResults(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SpacingXXHuge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(Dimens.SpacingXl))
        Text(
            text = stringResource(R.string.search_error_title),
            fontSize = Dimens.TextSizeXxl,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Dimens.SpacingSm))
        Text(
            text = message,
            fontSize = Dimens.TextSizeMd,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimens.SpacingXxxl)
        )
        Spacer(Modifier.height(Dimens.SpacingXl))
        Button(onClick = onClick = onRetry) {
            Text(text = stringResource(R.string.search_error_retry))
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
            .height(Dimens.CategoryCardHeight)
            .clip(RoundedCornerShape(Dimens.RadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable(onClick = onClick)
            .padding(Dimens.SpacingXl)
    ) {
        Text(
            text = category.label,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = category.emoji,
            fontSize = Dimens.TextSizeDisplay,
            modifier = Modifier.align(Alignment.BottomEnd),
            contentDescription = null
        )
    }
}