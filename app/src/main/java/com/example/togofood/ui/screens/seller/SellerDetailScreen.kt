package com.example.togofood.ui.screens.seller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.data.repository.FavoritesRepository
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.domain.model.SellerScheduleResolver
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.FoodIcons
import com.example.togofood.ui.components.PreparationCard
import com.example.togofood.ui.components.PreparationCardSkeleton
import com.example.togofood.ui.components.SectionHeader
import com.example.togofood.ui.components.UnderlineTabs
import com.example.togofood.ui.components.formatDistance
import com.example.togofood.ui.components.shimmerEffect
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter

private const val TAB_MENU = 0
private const val TAB_POPULAR = 1

private val PageBg = Color.White
private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)
private val SurfaceBg = Color(0xFFF5F5F5)
private val OrangeStrong = Color(0xFFC2410C)
private val PageHPadding = 16.dp
private val MinTouch = 48.dp

@Composable
fun SellerDetailScreen(
    viewModel: SellerDetailViewModel = viewModel(),
    onBackClick: () -> Unit,
    onPreparationClick: (String) -> Unit
) {
    val seller by viewModel.seller.collectAsState()
    val menu by viewModel.menu.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favPrepIds by FavoritesRepository.ids.collectAsState()
    val favSellerIds by FavoritesRepository.sellerIds.collectAsState()
    val listState = rememberLazyListState()
    val ctx = LocalContext.current

    if (seller == null && isLoading) {
        SellerLoading(onBackClick)
        return
    }

    val s = seller ?: run {
        if (!isLoading) SellerNotFound(onBackClick)
        return
    }

    val showTitleByScroll by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 280
        }
    }
    val isSellerFav = s.uuid in favSellerIds
    val popular = remember(menu) { menu.filter { it.isPopular }.ifEmpty { menu.take(3) } }
    var selectedTab by remember { mutableIntStateOf(TAB_MENU) }

    Box(Modifier.fillMaxSize().background(PageBg)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                SellerHero(
                    seller = s,
                    isFavorite = isSellerFav,
                    listState = listState,
                    onBackClick = onBackClick,
                    onFavoriteClick = { FavoritesRepository.toggleSeller(s.uuid) },
                    onShareClick = { shareSeller(ctx, s) }
                )
            }

            item {
                Column(modifier = Modifier.togoEnter(delayMs = 40)) {
                    SellerIdentity(s)
                }
            }
            item {
                Column(modifier = Modifier.togoEnter(delayMs = 80)) {
                    LogisticsBlock(s, ctx)
                }
            }

            item {
                Column(modifier = Modifier.togoEnter(delayMs = 95)) {
                    SellerHoursBlock(s)
                }
            }

            if (s.description.isNotBlank()) {
                item {
                    Column(modifier = Modifier.togoEnter(delayMs = 110)) {
                        Spacer(Modifier.height(28.dp))
                        SectionHeader(title = stringResource(R.string.seller_about_title))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = s.description,
                            fontSize = 15.sp,
                            color = TextSecondary,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = PageHPadding)
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.togoEnter(delayMs = 140)) {
                    Spacer(Modifier.height(28.dp))
                    UnderlineTabs(
                        titles = listOf(
                            stringResource(R.string.seller_tab_menu),
                            stringResource(R.string.seller_tab_popular)
                        ),
                        counts = listOf(menu.size, popular.size),
                        selectedIndex = selectedTab,
                        onTabClick = { selectedTab = it }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (isLoading) {
                items(2) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = PageHPadding),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(Modifier.weight(1f)) { PreparationCardSkeleton() }
                        Box(Modifier.weight(1f)) { PreparationCardSkeleton() }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            } else {
                item {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { TogoMotion.contentSwitch() },
                        label = "sellerMenuTab"
                    ) { tab ->
                        val itemsForTab = if (tab == TAB_POPULAR) popular else menu
                        if (itemsForTab.isEmpty()) {
                            Text(
                                text = stringResource(R.string.seller_tab_empty),
                                color = TextSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = PageHPadding, vertical = 24.dp)
                            )
                        } else {
                            PrepGrid(
                                preparations = itemsForTab,
                                favPrepIds = favPrepIds,
                                sellerOpenStatus = s.SellerOpenStatus,
                                onPreparationClick = onPreparationClick
                            )
                        }
                    }
                }
            }
        }

        TopFloatingBar(
            title = s.name,
            showTitle = showTitleByScroll,
            onBackClick = onBackClick,
            onShareClick = { shareSeller(ctx, s) }
        )
    }
}

@Composable
private fun PrepGrid(
    preparations: List<Preparation>,
    favPrepIds: Set<String>,
    sellerOpenStatus: SellerOpenStatus,
    onPreparationClick: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        preparations.chunked(2).forEach { rowItems ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PageHPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (item in rowItems) {
                    PreparationCard(
                        preparation = item,
                        isFavorite = item.uuid in favPrepIds,
                        sellerOpenStatus = sellerOpenStatus,
                        onClick = { onPreparationClick(item.uuid) },
                        onFavoriteClick = { FavoritesRepository.toggle(item.uuid) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SellerLoading(onBackClick: () -> Unit) {
    Box(Modifier.fillMaxSize().background(PageBg)) {
        Column(Modifier.fillMaxSize().padding(PageHPadding)) {
            Spacer(Modifier.height(48.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .shimmerEffect()
            )
            Spacer(Modifier.height(24.dp))
            Box(Modifier.fillMaxWidth(0.6f).height(24.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth(0.4f).height(16.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
            Spacer(Modifier.height(20.dp))
            Box(Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
        }
        Box(Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(PageHPadding)) {
            CircleBtn(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), onBackClick)
        }
    }
}

@Composable
private fun SellerNotFound(onBackClick: () -> Unit) {
    Box(Modifier.fillMaxSize().background(PageBg), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(R.string.seller_not_found),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TextPrimary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.cd_back),
                color = BrandOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onBackClick)
            )
        }
        Box(Modifier.align(Alignment.TopStart).windowInsetsPadding(WindowInsets.statusBars).padding(PageHPadding)) {
            CircleBtn(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), onBackClick)
        }
    }
}

@Composable
private fun TopFloatingBar(
    title: String,
    showTitle: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = PageHPadding, vertical = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleBtn(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), onBackClick)
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(TogoMotion.tweenNormal()),
                exit = fadeOut(TogoMotion.tweenFast())
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            CircleBtn(Icons.Default.Share, stringResource(R.string.cd_share), onShareClick)
        }
    }
}

@Composable
private fun SellerHero(
    seller: Seller,
    isFavorite: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val scrollOffset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
    val heartScale by animateFloatAsState(
        if (isFavorite) 1.2f else 1f,
        TogoMotion.HeartSpring,
        label = "sellerHeart"
    )
    val heartColor by animateColorAsState(
        if (isFavorite) Color(0xFFD32F2F) else TextPrimary,
        TogoMotion.tweenNormal(),
        label = "sellerHeartColor"
    )
    val pastel = FoodIcons.defaultPastel

    Box(
        Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = scrollOffset.value * 0.45f }
                .background(pastel),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.35f),
                modifier = Modifier.size(88.dp)
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = PageHPadding, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleBtn(
                Icons.AutoMirrored.Filled.ArrowBack,
                stringResource(R.string.cd_back),
                onBackClick,
                background = Color.Transparent
            )
            Row {
                CircleBtn(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    if (isFavorite) {
                        stringResource(R.string.cd_unfavorite_seller)
                    } else {
                        stringResource(R.string.cd_favorite_seller)
                    },
                    onFavoriteClick,
                    tint = heartColor,
                    modifier = Modifier.scale(heartScale)
                )
                Spacer(Modifier.width(8.dp))
                CircleBtn(
                    Icons.Default.Share,
                    stringResource(R.string.cd_share),
                    onShareClick,
                    background = Color.Transparent
                )
            }
        }
    }
}

@Composable
private fun SellerHoursBlock(s: Seller) {
    val lines = remember(s.schedule) { SellerScheduleResolver.summaryLines(s.schedule) }
    Spacer(Modifier.height(20.dp))
    SectionHeader(title = stringResource(R.string.seller_detail_hours_title))
    Spacer(Modifier.height(8.dp))
    Column(Modifier.padding(horizontal = PageHPadding)) {
        if (lines.isEmpty()) {
            Text(
                stringResource(R.string.seller_detail_hours_empty),
                fontSize = 14.sp,
                color = TextSecondary
            )
        } else {
            lines.forEach { line ->
                Text(
                    line,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun SellerIdentity(s: Seller) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = PageHPadding)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            s.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            lineHeight = 28.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.seller_zone_landmark, s.zone, s.landmark),
            fontSize = 14.sp,
            color = TextSecondary
        )
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (s.isVerified) {
                StatusChip(
                    icon = Icons.Default.Star,
                    label = stringResource(R.string.seller_pro_badge),
                    tint = BrandOrange,
                    bg = Color(0xFFFFEDE3)
                )
            }
            StatusChip(
                icon = Icons.Default.Star,
                label = stringResource(
                    R.string.seller_rating_reviews,
                    s.rating,
                    s.reviewCount
                ),
                tint = Color(0xFFFFC107),
                bg = SurfaceBg
            )
            OpenStatusChip(s.SellerOpenStatus)
            StatusChip(
                icon = Icons.Default.LocationOn,
                label = s.distanceMeters.formatDistance(),
                tint = TextSecondary,
                bg = SurfaceBg
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun OpenStatusChip(status: SellerOpenStatus) {
    val (labelRes, color) = when (status) {
        SellerOpenStatus.OPEN -> R.string.seller_open to Color(0xFF2F9E44)
        SellerOpenStatus.CLOSED -> R.string.seller_closed to Color(0xFF6C757D)
        SellerOpenStatus.OPENING_SOON -> R.string.seller_opening_soon to Color(0xFFF08C00)
    }
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(SurfaceBg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(
            stringResource(labelRes),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun StatusChip(
    icon: ImageVector,
    label: String,
    tint: Color,
    bg: Color
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
private fun LogisticsBlock(s: Seller, ctx: Context) {
    val hasPhone = s.phone.isNotBlank()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = PageHPadding)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceBg)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, null, tint = BrandOrange, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.seller_zone_landmark, s.zone, s.landmark),
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LogisticsAction(
                label = stringResource(R.string.seller_itinerary),
                modifier = Modifier.weight(1f),
                onClick = { openSellerMaps(ctx, s) }
            )
            if (hasPhone) {
                LogisticsAction(
                    label = stringResource(R.string.seller_call),
                    icon = Icons.Default.Call,
                    modifier = Modifier.weight(1f),
                    onClick = { dialPhone(ctx, s.phone) }
                )
            }
        }
    }
}

@Composable
private fun LogisticsAction(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier
            .heightIn(min = MinTouch)
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(icon, null, tint = OrangeStrong, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(label, color = OrangeStrong, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun CircleBtn(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = TextPrimary,
    modifier: Modifier = Modifier,
    background: Color = Color.White.copy(alpha = 0.9f)
) {
    Box(
        modifier
            .size(MinTouch)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(22.dp))
    }
}

private fun shareSeller(ctx: Context, s: Seller) {
    val text = ctx.getString(R.string.seller_share_text, s.name)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.seller_share_via)))
}

private fun openSellerMaps(ctx: Context, s: Seller) {
    val query = listOfNotNull(s.name, s.landmark, s.zone, "Lomé")
        .filter { it.isNotBlank() }
        .joinToString(", ")
    val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    runCatching { ctx.startActivity(intent) }
        .onFailure {
            Toast.makeText(ctx, ctx.getString(R.string.seller_maps_failed), Toast.LENGTH_SHORT).show()
        }
}

private fun dialPhone(ctx: Context, phone: String) {
    if (phone.isBlank()) {
        Toast.makeText(ctx, ctx.getString(R.string.seller_phone_unavailable), Toast.LENGTH_SHORT).show()
        return
    }
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    runCatching { ctx.startActivity(intent) }
        .onFailure {
            Toast.makeText(ctx, ctx.getString(R.string.seller_call_failed), Toast.LENGTH_SHORT).show()
        }
}
