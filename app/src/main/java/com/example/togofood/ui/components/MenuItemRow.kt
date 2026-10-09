package com.example.togofood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.R
import com.example.togofood.domain.model.Preparation

private val Ink = Color(0xFF111111)
private val Muted = Color(0xFF6B6B6B)
private val SoftMuted = Color(0xFF8A8A8A)
private val PriceGreen = Color(0xFF2E7D32)
private val StarGold = Color(0xFFF08C00)
private val ImageBg = Color(0xFFF5F5F5)

/**
 * Ligne résultat préparation (Search) — hiérarchie :
 * titre → vendeur/zone → description → note → prix vert + dispo.
 */
@Composable
fun MenuItemRow(
    preparation: Preparation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = preparation.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Text(
                text = stringResource(
                    R.string.search_prep_seller_meta,
                    preparation.sellerName,
                    preparation.sellerZone
                ),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (preparation.description.isNotBlank()) {
                Text(
                    text = preparation.description,
                    fontSize = 12.sp,
                    color = SoftMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

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
                        preparation.rating,
                        preparation.reviewCount
                    ),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SoftMuted
                )
                Text("·", fontSize = 12.sp, color = SoftMuted)
                Text(
                    text = preparation.distanceMeters.formatDistance(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SoftMuted
                )
            }

            Spacer(Modifier.height(2.dp))

            val pricePrefix = stringResource(R.string.search_price_from_prefix)
            val priceValue = stringResource(
                R.string.search_price_value,
                preparation.basePrice.formatFcfa()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        if (preparation.variants.size > 1) {
                            withStyle(
                                SpanStyle(
                                    color = SoftMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append(pricePrefix)
                                append(' ')
                            }
                        }
                        withStyle(
                            SpanStyle(
                                color = PriceGreen,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append(priceValue)
                        }
                    },
                    fontSize = 15.sp,
                    maxLines = 1
                )
                AvailabilityBadge(status = preparation.availabilityStatus)
            }
        }

        Spacer(Modifier.width(14.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    FoodIcons.pastelBackgrounds[preparation.categoryId]
                        ?: ImageBg
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = FoodIcons.emojiFor(preparation.categoryId),
                fontSize = 42.sp
            )
        }
    }
}
