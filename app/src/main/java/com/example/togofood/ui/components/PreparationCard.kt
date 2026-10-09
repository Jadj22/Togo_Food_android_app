package com.example.togofood.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.R
import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.ui.theme.TogoMotion

private val TextPrimary = Color(0xFF111111)
private val SoftMuted = Color(0xFF8A8A8A)
private val PriceGreen = Color(0xFF2E7D32)
private val StarGold = Color(0xFFF08C00)
private val photoPlaceholderBg = Color(0xFFF0F0F0)
private val CardRadius = 14.dp
private val MediaHeight = 160.dp

@Composable
fun PreparationCard(
    preparation: Preparation,
    isFavorite: Boolean = false,
    sellerOpenStatus: SellerOpenStatus = SellerOpenStatus.OPEN,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    showFavorite: Boolean = true,
    showSellerOpenStatus: Boolean = true,
    showSellerName: Boolean = true,
    modifier: Modifier = Modifier.width(188.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = TogoMotion.PressSpring,
        label = "cardScale"
    )

    val heartColor by animateColorAsState(
        if (isFavorite) BrandOrange else TextPrimary,
        TogoMotion.tweenNormal(),
        label = "heartColor"
    )
    val heartScale by animateFloatAsState(
        if (isFavorite) 1.15f else 1.0f,
        TogoMotion.HeartSpring,
        label = "heartScale"
    )

    val unavailable = preparation.availabilityStatus == AvailabilityStatus.UNAVAILABLE
    val showClosedSignal = showSellerOpenStatus && sellerOpenStatus != SellerOpenStatus.OPEN

    Column(
        modifier = modifier
            .scale(animatedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MediaHeight)
                    .clip(RoundedCornerShape(CardRadius))
                    .background(
                        FoodIcons.pastelBackgrounds[preparation.categoryId]
                            ?: photoPlaceholderBg
                    )
                    .alpha(if (unavailable) 0.55f else 1f),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder calme tant qu'il n'y a pas de vraie photo (photoUrl).
                Text(
                    text = FoodIcons.emojiFor(preparation.categoryId),
                    fontSize = 40.sp
                )
            }

            // Signaux décisionnels (Badges) positionnés en bas à gauche sur le média.
            if (unavailable || showClosedSignal) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (unavailable) {
                        AvailabilityBadge(status = AvailabilityStatus.UNAVAILABLE)
                    }
                    if (showClosedSignal) {
                        SellerOpenChip(status = sellerOpenStatus)
                    }
                }
            }

            if (showFavorite) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { onFavoriteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) {
                                stringResource(R.string.cd_remove_favorite)
                            } else {
                                stringResource(R.string.cd_add_favorite)
                            },
                            tint = heartColor,
                            modifier = Modifier.size(16.dp).scale(heartScale)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                preparation.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )



            if (showSellerName) {
                Text(
                    preparation.sellerName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SoftMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (preparation.rating > 0f) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = StarGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = stringResource(
                            R.string.card_rating_summary,
                            "%.1f".format(preparation.rating),
                            preparation.reviewCount.toString()
                        ),
                        fontSize = 11.sp,
                        color = SoftMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(" · ", fontSize = 11.sp, color = SoftMuted)
                }
                Text(
                    preparation.distanceMeters.formatDistance(),
                    fontSize = 11.sp,
                    color = SoftMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Prix propriétaire de sa ligne — ancre décisionnelle.
            Text(
                text = if (preparation.variants.size > 1) {
                    stringResource(
                        R.string.card_price_from_short,
                        preparation.basePrice.formatFcfa()
                    )
                } else {
                    stringResource(
                        R.string.search_price_value,
                        preparation.basePrice.formatFcfa()
                    )
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PriceGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SellerOpenChip(status: SellerOpenStatus) {
    val (labelRes, color) = when (status) {
        SellerOpenStatus.OPEN -> R.string.seller_open to Color(0xFF2F9E44)
        SellerOpenStatus.CLOSED -> R.string.seller_closed to Color(0xFF6C757D)
        SellerOpenStatus.OPENING_SOON -> R.string.seller_opening_soon to Color(0xFFF08C00)
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            stringResource(labelRes),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun PreparationCardSkeleton() {
    Column(modifier = Modifier.width(188.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MediaHeight)
                .clip(RoundedCornerShape(CardRadius))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth(0.85f).height(16.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth(0.55f).height(12.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth(0.4f).height(14.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
    }
}

@Composable
fun WidePromoCarousel(
    preparations: List<Preparation>,
    promoLabel: String,
    onPreparationClick: (Preparation) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (preparations.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = preparations, key = { it.uuid }) { preparation ->
            WidePromoCard(
                preparation = preparation,
                promoLabel = promoLabel,
                onClick = { onPreparationClick(preparation) },
                modifier = Modifier.width(300.dp)
            )
        }
    }
}

@Composable
fun WidePromoCard(
    preparation: Preparation,
    promoLabel: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (isPressed) 0.98f else 1f,
        TogoMotion.PressSpring,
        label = "promoScale"
    )

    val bgColor = FoodIcons.pastelBackgrounds[preparation.categoryId] ?: FoodIcons.defaultPastel
    Card(
        modifier = modifier
            .height(156.dp)
            .scale(scale)
            .clickable(interactionSource, null, onClick = onClick),
        shape = RoundedCornerShape(CardRadius),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    preparation.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    preparation.sellerName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SoftMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    promoLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    stringResource(R.string.card_price_from, preparation.basePrice.formatFcfa()),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PriceGreen
                )
            }
            Box(
                Modifier
                    .width(96.dp)
                    .fillMaxHeight()
                    .alpha(0.85f),
                contentAlignment = Alignment.Center
            ) {
                Text(FoodIcons.emojiFor(preparation.categoryId), fontSize = 48.sp)
            }
        }
    }
}
