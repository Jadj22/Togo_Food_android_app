package com.example.togofood.ui.screens.preparation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.data.repository.FavoritesRepository
import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.Variant
import com.example.togofood.domain.model.VendorStatus
import com.example.togofood.ui.components.*
import java.util.Locale
import kotlin.math.roundToInt

private val PageBg        = Color.White
private val TextPrimary   = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)
private val SurfaceBg     = Color(0xFFF5F5F5)
private val Outline       = Color(0xFFE0E0E0)
private val PriceGreen    = Color(0xFF2E7D32)
private val OrangeStrong  = Color(0xFFC2410C)
private val WhatsAppGreen = Color(0xFF0E7C66)
private val PageHPadding = 16.dp
private val SectionGap   = 32.dp
private val MinTouch     = 48.dp

private class RequestActions(
    val onModeChange: (RequestMode) -> Unit,
    val onToggleOption: (groupId: String, optionId: String) -> Unit,
    val onMessageChange: (String) -> Unit,
    val onResetMessage: () -> Unit,
    val messageToSend: () -> String
)

@Composable
fun PreparationDetailScreen(
    viewModel: PreparationDetailViewModel = viewModel(),
    onBackClick: () -> Unit,
    onSellerClick: (String) -> Unit = {},
    onPreparationClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    when (val s = state) {
        is PreparationDetailUiState.Loading -> DetailSkeleton(onBackClick)
        is PreparationDetailUiState.NotFound -> DetailMessage("Plat introuvable", null, {}, onBackClick)
        is PreparationDetailUiState.Error -> DetailMessage(s.message, "Réessayer", viewModel::retry, onBackClick)
        is PreparationDetailUiState.Success -> {
            val selected by viewModel.selectedVariant.collectAsState()
            val request by viewModel.request.collectAsState()
            val requestActions = remember(viewModel) {
                RequestActions(viewModel::setRequestMode, viewModel::toggleOption, viewModel::onMessageChange, viewModel::resetMessage, viewModel::messageToSend)
            }
            DetailContent(s.prep, s.seller, s.related, selected, viewModel::selectVariant, request, requestActions, onBackClick, onSellerClick, onPreparationClick)
        }
    }
}

@Composable
private fun DetailContent(
    prep: Preparation,
    seller: Seller?,
    related: List<Preparation>,
    selectedVariant: Int,
    onVariantSelect: (Int) -> Unit,
    request: CustomRequestState,
    requestActions: RequestActions,
    onBackClick: () -> Unit,
    onSellerClick: (String) -> Unit,
    onPreparationClick: (String) -> Unit
) {
    val ctx = LocalContext.current
    val favIds by FavoritesRepository.ids.collectAsState()
    val scrollState = rememberScrollState()
    val chosen = prep.variants.getOrNull(selectedVariant) ?: prep.variants.firstOrNull()
    val hasPhone = !seller?.phone.isNullOrBlank()
    
    val showTitleByScroll by remember { derivedStateOf { scrollState.value > 450 } }

    Box(Modifier.fillMaxSize().background(PageBg).imePadding()) {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1f).verticalScroll(scrollState)) {
                PrepHero(prep, seller, prep.uuid in favIds, scrollState, onBackClick, { FavoritesRepository.toggle(prep.uuid) }, { sharePrep(ctx, prep, chosen, seller) })

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PageHPadding)
                        .togoEnter(delayMs = 40)
                ) {
                    Spacer(Modifier.height(20.dp))
                    Text(prep.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, lineHeight = 31.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Chez ${prep.sellerName} · ${prep.sellerZone}", fontSize = 15.sp, color = TextSecondary)
                    Spacer(Modifier.height(12.dp))
                    PriceHeader(prep.variants, chosen)
                    Spacer(Modifier.height(12.dp))
                    InfoChips(prep, prep.distanceMeters.formatDistance())

                    if (prep.variants.size > 1) {
                        Spacer(Modifier.height(SectionGap))
                        VariantPicker(prep.variants, selectedVariant, onVariantSelect)
                    }

                    if (hasPhone && request.groups.isNotEmpty()) {
                        Spacer(Modifier.height(SectionGap))
                        CustomRequestSection(request, requestActions)
                    }

                    Spacer(Modifier.height(SectionGap))
                    AboutBlock(prep.description)
                    Spacer(Modifier.height(SectionGap))
                    SectionTitle("Vendeur")
                    Spacer(Modifier.height(12.dp))
                    SellerCard(prep, seller, onSellerClick)
                    Spacer(Modifier.height(SectionGap))
                    WhereToFindBlock(prep, seller) { openMaps(ctx, seller, prep.sellerZone) }
                    Spacer(Modifier.height(SectionGap))
                    ReviewsBlock(prep)
                    Spacer(Modifier.height(SectionGap))
                }

                if (related.isNotEmpty()) {
                    Column(Modifier.padding(horizontal = PageHPadding)) {
                        SectionTitle("Dans la même catégorie")
                        Spacer(Modifier.height(12.dp))
                    }
                    LazyRow(contentPadding = PaddingValues(horizontal = PageHPadding), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(related, key = { it.uuid }) { other ->
                            PreparationCard(preparation = other, isFavorite = other.uuid in favIds, onClick = { onPreparationClick(other.uuid) }, onFavoriteClick = { FavoritesRepository.toggle(other.uuid) })
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
            ActionBar(hasPhone, chosen, if (request.isPersonalized) "💬 Envoyer au vendeur" else "💬 WhatsApp", { onSellerClick(prep.sellerId) }, { dialPhone(ctx, seller?.phone) }, { openWhatsApp(ctx, seller?.phone, requestActions.messageToSend()) })
        }

        // Barre de titre collante
        TopDetailBar(prep.name, showTitleByScroll, onBackClick, { sharePrep(ctx, prep, chosen, seller) })
    }
}

@Composable
private fun TopDetailBar(title: String, showTitle: Boolean, onBackClick: () -> Unit, onShareClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = PageHPadding, vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            CircleBtn(Icons.AutoMirrored.Filled.ArrowBack, "Retour", onBackClick)
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(TogoMotion.tweenNormal()),
                exit = fadeOut(TogoMotion.tweenFast())
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 12.dp))
            }
            CircleBtn(Icons.Default.Share, "Partager", onShareClick)
        }
    }
}

@Composable
private fun PrepHero(prep: Preparation, seller: Seller?, isFavorite: Boolean, scrollState: ScrollState, onBackClick: () -> Unit, onFavoriteClick: () -> Unit, onShareClick: () -> Unit) {
    val vendorClosed = seller?.vendorStatus == VendorStatus.CLOSED
    val badgeStatus = if (vendorClosed) AvailabilityStatus.UNAVAILABLE else prep.availabilityStatus
    val badgeLabel = if (vendorClosed) "Vendeur fermé" else badgeStatus.displayLabel()

    val heartScale by animateFloatAsState(
        if (isFavorite) 1.2f else 1.0f,
        TogoMotion.HeartSpring,
        label = "prepHeart"
    )
    val heartColor by animateColorAsState(
        if (isFavorite) Color(0xFFD32F2F) else TextPrimary,
        TogoMotion.tweenNormal(),
        label = "prepHeartColor"
    )

    Box(Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))) {
        // Effet Parallaxe
        Box(Modifier.fillMaxSize().graphicsLayer { translationY = scrollState.value * 0.45f }) {
            PrepPhoto(prep)
        }

        Row(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = PageHPadding, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            CircleBtn(Icons.AutoMirrored.Filled.ArrowBack, "Retour", onBackClick, background = Color.Transparent)
            Row {
                CircleBtn(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favori", onFavoriteClick, tint = heartColor, modifier = Modifier.scale(heartScale))
                Spacer(Modifier.width(8.dp))
                CircleBtn(Icons.Default.Share, "Partager", onShareClick, background = Color.Transparent)
            }
        }

        AvailabilityBadge(status = badgeStatus, label = badgeLabel, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }
}

@Composable
private fun VariantPicker(variants: List<Variant>, selected: Int, onSelect: (Int) -> Unit) {
    SectionTitle("Choisir une portion")
    Spacer(Modifier.height(12.dp))
    Row(Modifier.horizontalScroll(rememberScrollState()).selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        variants.forEachIndexed { i, v ->
            val isSelected = i == selected
            val bgColor by animateColorAsState(
                if (isSelected) OrangeStrong else Color.White,
                TogoMotion.tweenNormal(),
                label = "variantBg"
            )
            val textColor by animateColorAsState(
                if (isSelected) Color.White else PriceGreen,
                TogoMotion.tweenNormal(),
                label = "variantFg"
            )
            
            Column(
                Modifier.heightIn(min = 56.dp).clip(RoundedCornerShape(16.dp)).background(bgColor).border(1.dp, if (isSelected) OrangeStrong else Outline, RoundedCornerShape(16.dp))
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(i) }).padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
                Text(v.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextSecondary)
                Text("${v.priceFcfa.formatFcfa()} F", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
            }
        }
    }
}

@Composable
private fun CustomRequestSection(state: CustomRequestState, actions: RequestActions) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        if (expanded) 180f else 0f,
        TogoMotion.tweenNormal(),
        label = "aboutRotation"
    )

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceBg)
            .animateContentSize(TogoMotion.tweenNormal())
    ) {
        Row(Modifier.fillMaxWidth().heightIn(min = MinTouch).clickable { expanded = !expanded }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                SectionTitle("Personnaliser ma demande")
                Text(if (state.choiceCount > 0) "Demande prête" else "Options disponibles", fontSize = 13.sp, color = TextSecondary)
            }
            Icon(Icons.Default.KeyboardArrowDown, null, tint = BrandOrange, modifier = Modifier.graphicsLayer { rotationZ = rotation })
        }

        if (expanded) {
            Column(Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {
                RequestModeSwitch(state.mode, actions.onModeChange)
                Spacer(Modifier.height(16.dp))
                if (state.mode == RequestMode.CHOOSE) {
                    state.groups.forEach { group ->
                        OptionGroupRow(group, state.selections[group.id].orEmpty(), { actions.onToggleOption(group.id, it) })
                        Spacer(Modifier.height(16.dp))
                    }
                }
                RequestMessageEditor(state, actions.onMessageChange, actions.onResetMessage)
            }
        }
    }
}

// ───────────────────────────── Rest of UI (stubs for missing components) ─────────────────────────────

@Composable
private fun ChoiceChip(label: String, isSelected: Boolean, multi: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        if (isSelected) OrangeStrong else Color.White,
        TogoMotion.tweenNormal(),
        label = "chipBg"
    )
    val shape = RoundedCornerShape(50)
    Box(Modifier.heightIn(min = MinTouch).clip(shape).background(bgColor).border(1.dp, if (isSelected) OrangeStrong else Outline, shape).clickable { onClick() }.padding(horizontal = 16.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
        Text((if (isSelected) "✓ " else "") + label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextPrimary)
    }
}

@Composable
private fun SectionTitle(text: String) = Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

@Composable
private fun PriceHeader(variants: List<Variant>, chosen: Variant?) {
    if (chosen == null) return
    Text("${chosen.priceFcfa.formatFcfa()} FCFA", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PriceGreen)
}

@Composable
private fun PrepPhoto(prep: Preparation) {
    val bgColor = FoodIcons.pastelBackgrounds[prep.categoryId] ?: FoodIcons.defaultPastel
    Box(Modifier.fillMaxSize().background(bgColor), contentAlignment = Alignment.Center) {
        Text(FoodIcons.emojiFor(prep.categoryId), fontSize = 110.sp)
    }
}

@Composable
private fun AvailabilityBadge(status: AvailabilityStatus, label: String, modifier: Modifier) {
    Row(modifier.clip(RoundedCornerShape(50)).background(Color.White).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(status.color))
        Spacer(Modifier.width(7.dp))
        Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
    }
}

@Composable
private fun InfoChips(prep: Preparation, dist: String) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.clip(RoundedCornerShape(50)).background(SurfaceBg).padding(horizontal = 14.dp, vertical = 8.dp)) {
            Text("📍 $dist", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Box(Modifier.clip(RoundedCornerShape(50)).background(SurfaceBg).padding(horizontal = 14.dp, vertical = 8.dp)) {
            Text("⭐ ${String.format("%.1f", prep.rating)}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SellerCard(prep: Preparation, seller: Seller?, onSellerClick: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceBg)
            .clickable { onSellerClick(prep.sellerId) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(52.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = BrandOrange,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(prep.sellerName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            if (seller?.isVerified == true) {
                Text(
                    stringResource(R.string.seller_verified),
                    fontSize = 13.sp,
                    color = OrangeStrong
                )
            } else {
                Text(
                    "${prep.sellerZone} · ${prep.distanceMeters.formatDistance()}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = BrandOrange)
    }
}

@Composable
private fun AboutBlock(desc: String) {
    SectionTitle("À propos")
    Spacer(Modifier.height(8.dp))
    Text(desc, fontSize = 15.sp, color = TextSecondary, lineHeight = 22.sp)
}

@Composable
private fun WhereToFindBlock(prep: Preparation, seller: Seller?, onItinerary: () -> Unit) {
    SectionTitle("Localisation")
    Spacer(Modifier.height(8.dp))
    Text("📍 ${prep.sellerZone}", fontSize = 15.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(12.dp))
    OutlinedButton(onClick = onItinerary, modifier = Modifier.fillMaxWidth().height(MinTouch), shape = RoundedCornerShape(50), border = BorderStroke(1.dp, OrangeStrong)) {
        Text("Voir l'itinéraire", color = OrangeStrong, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ReviewsBlock(prep: Preparation) {
    SectionTitle("Avis")
    Spacer(Modifier.height(12.dp))
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceBg).padding(16.dp)) {
        Text("⭐ ${String.format("%.1f", prep.rating)} (${prep.reviewCount} avis)", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ActionBar(hasPhone: Boolean, chosen: Variant?, label: String, onSeller: () -> Unit, onCall: () -> Unit, onWhatsApp: () -> Unit) {
    Row(Modifier.fillMaxWidth().navigationBarsPadding().background(Color.White).padding(horizontal = PageHPadding, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (hasPhone) {
            OutlinedButton(onClick = onCall, Modifier.weight(1f).heightIn(min = 56.dp), shape = RoundedCornerShape(50), border = BorderStroke(1.dp, OrangeStrong)) {
                Icon(Icons.Default.Call, null, tint = OrangeStrong); Spacer(Modifier.width(8.dp)); Text("Appeler", color = OrangeStrong)
            }
            Button(onClick = onWhatsApp, Modifier.weight(1.5f).heightIn(min = 56.dp), shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)) {
                Text(label, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(onClick = onSeller, Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = OrangeStrong)) {
                Text("Voir le vendeur", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CircleBtn(icon: ImageVector, desc: String, onClick: () -> Unit, tint: Color = TextPrimary, modifier: Modifier = Modifier, background: Color = Color.White.copy(alpha = 0.9f)) {
    Box(modifier.size(MinTouch).clip(CircleShape).background(background).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Icon(icon, desc, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun DetailSkeleton(onBackClick: () -> Unit) {
    Box(Modifier.fillMaxSize().background(PageBg)) {
        Column(Modifier.fillMaxSize().padding(PageHPadding)) {
            Spacer(Modifier.height(48.dp))
            Box(Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
            Spacer(Modifier.height(24.dp))
            Box(Modifier.fillMaxWidth(0.7f).height(28.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth(0.5f).height(18.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
            Spacer(Modifier.height(24.dp))
            Box(Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
        }
        Box(Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(PageHPadding)) {
            CircleBtn(Icons.AutoMirrored.Filled.ArrowBack, "Retour", onBackClick)
        }
    }
}

@Composable
private fun DetailMessage(
    title: String,
    actionLabel: String?,
    onAction: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(PageBg)) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
            if (actionLabel != null) {
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeStrong),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(actionLabel, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onBackClick) {
                Text("Retour", color = TextSecondary)
            }
        }
        Box(Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(PageHPadding)) {
            CircleBtn(Icons.AutoMirrored.Filled.ArrowBack, "Retour", onBackClick)
        }
    }
}

@Composable
private fun RequestModeSwitch(mode: RequestMode, onChange: (RequestMode) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(RequestMode.CHOOSE to "Choisir", RequestMode.WRITE to "Écrire").forEach { (m, label) ->
            val selected = mode == m
            val bg by animateColorAsState(
                if (selected) OrangeStrong else Color.Transparent,
                TogoMotion.tweenNormal(),
                label = "modeBg"
            )
            Box(
                Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bg)
                    .clickable { onChange(m) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (selected) Color.White else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun OptionGroupRow(
    group: RequestOptionGroup,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(group.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            group.options.forEach { option ->
                ChoiceChip(
                    label = listOfNotNull(option.emoji, option.label).joinToString(" "),
                    isSelected = option.id in selected,
                    multi = group.selection == OptionSelection.MULTIPLE,
                    onClick = { onToggle(option.id) }
                )
            }
        }
    }
}

@Composable
private fun RequestMessageEditor(
    state: CustomRequestState,
    onChange: (String) -> Unit,
    onReset: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Message", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            if (state.isMessageEdited) {
                TextButton(onClick = onReset, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
                    Text("Réinitialiser", color = OrangeStrong, fontSize = 13.sp)
                }
            }
        }
        OutlinedTextField(
            value = state.message,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangeStrong,
                unfocusedBorderColor = Outline,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
    }
}

private fun dialPhone(ctx: Context, phone: String?) {
    if (phone.isNullOrBlank()) {
        Toast.makeText(ctx, "Numéro indisponible", Toast.LENGTH_SHORT).show()
        return
    }
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    runCatching { ctx.startActivity(intent) }
        .onFailure { Toast.makeText(ctx, "Impossible d'ouvrir l'appel", Toast.LENGTH_SHORT).show() }
}

private fun openMaps(ctx: Context, seller: Seller?, zone: String) {
    val query = listOfNotNull(seller?.name, seller?.landmark, zone, "Lomé")
        .filter { it.isNotBlank() }
        .joinToString(", ")
    val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    runCatching { ctx.startActivity(intent) }
        .onFailure { Toast.makeText(ctx, "Aucune app carte trouvée", Toast.LENGTH_SHORT).show() }
}

private fun sharePrep(ctx: Context, prep: Preparation, variant: Variant?, seller: Seller?) {
    val price = variant?.let { "${it.priceFcfa.formatFcfa()} FCFA" } ?: "${prep.basePrice.formatFcfa()} FCFA"
    val text = buildString {
        append(prep.name)
        append(" — ").append(price)
        append(" · Chez ").append(seller?.name ?: prep.sellerName)
        append(" · ").append(prep.sellerZone)
        append(" (via TogoFood)")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    ctx.startActivity(Intent.createChooser(intent, "Partager via"))
}

private fun openWhatsApp(ctx: Context, phone: String?, message: String) {
    if (phone.isNullOrBlank()) {
        Toast.makeText(ctx, "Numéro WhatsApp indisponible", Toast.LENGTH_SHORT).show()
        return
    }
    val digits = phone.filter { it.isDigit() }
    val encoded = Uri.encode(message)
    val waUri = Uri.parse("https://wa.me/$digits?text=$encoded")
    val intent = Intent(Intent.ACTION_VIEW, waUri)
    runCatching { ctx.startActivity(intent) }
        .onFailure { dialPhone(ctx, phone) }
}

private fun AvailabilityStatus.displayLabel(): String = label
