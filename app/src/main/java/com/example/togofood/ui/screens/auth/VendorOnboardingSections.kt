package com.example.togofood.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.R
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.DaySchedule
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.LabeledTextField
import com.example.togofood.ui.components.PhoneTextField
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter
import kotlinx.coroutines.delay
import java.util.Calendar

private val SurfaceBg = Color(0xFFF5F5F5)
private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)
private val BrandOrangeLight = Color(0xFFFBE9E7)
private val HeroPastel = Color(0xFFFFF3E8)
private val SoftGreen = Color(0xFFE8F5E9)
private val SoftGreenText = Color(0xFF2E7D32)

data class MotivSlide(
    val emoji: String,
    val titleRes: Int,
    val bodyRes: Int,
    val tint: Color = BrandOrangeLight,
)

@Composable
fun VendorStepProgress(current: Int, total: Int = VendorOnboarding.TOTAL_STEPS) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(total) { index ->
                val step = index + 1
                val filled = step <= current
                val progress by animateFloatAsState(
                    targetValue = if (filled) 1f else 0f,
                    animationSpec = TogoMotion.tweenNormal(),
                    label = "stepFill$step"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE8E8E8))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceAtLeast(0.001f))
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(BrandOrange, Color(0xFFFF8A50))
                                )
                            )
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        AnimatedContent(
            targetState = current,
            transitionSpec = {
                (fadeIn(TogoMotion.tweenFast()) + slideInVertically { it / 3 }) togetherWith
                    fadeOut(TogoMotion.tweenFast())
            },
            label = "stepLabel"
        ) { step ->
            Text(
                stringResource(R.string.vendor_onboarding_step_of, step, total),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandOrange
            )
        }
    }
}

@Composable
fun MotivAutoCarousel(
    slides: List<MotivSlide>,
    modifier: Modifier = Modifier,
    autoMs: Long = 3200L,
) {
    if (slides.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { slides.size })

    LaunchedEffect(slides.size) {
        while (true) {
            delay(autoMs)
            val next = (pagerState.currentPage + 1) % slides.size
            pagerState.animateScrollToPage(
                page = next,
                animationSpec = tween(TogoMotion.SlowMs, easing = FastOutSlowInEasing)
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 2.dp),
            pageSpacing = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val slide = slides[page]
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = 1f - (kotlin.math.abs(pageOffset) * 0.06f).coerceIn(0f, 0.12f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clip(RoundedCornerShape(16.dp))
                    .background(slide.tint)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(slide.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(slide.titleRes),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        stringResource(slide.bodyRes),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(slides.size) { i ->
                val selected = pagerState.currentPage == i
                val w by animateFloatAsState(
                    if (selected) 16f else 6f,
                    TogoMotion.tweenFast(),
                    label = "dotW$i"
                )
                val c by animateColorAsState(
                    if (selected) BrandOrange else Color(0xFFD0D0D0),
                    TogoMotion.tweenFast(),
                    label = "dotC$i"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(w.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(c)
                )
            }
        }
    }
}

@Composable
fun VendorTipCard(text: String, emoji: String = "💡") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BrandOrangeLight)
            .padding(12.dp)
            .togoEnter(),
        verticalAlignment = Alignment.Top
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = BrandOrange,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun VendorAccountStep(
    name: String,
    phone: String,
    nameError: Int?,
    phoneError: Int?,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
) {
    Column {
        MotivAutoCarousel(
            slides = listOf(
                MotivSlide("💬", R.string.vendor_onboarding_account_slide1_title, R.string.vendor_onboarding_account_slide1_body),
                MotivSlide("📱", R.string.vendor_onboarding_account_slide2_title, R.string.vendor_onboarding_account_slide2_body, SoftGreen),
                MotivSlide("🤝", R.string.vendor_onboarding_account_slide3_title, R.string.vendor_onboarding_account_slide3_body, Color(0xFFE3F2FD)),
            )
        )
        Spacer(Modifier.height(18.dp))
        LabeledTextField(
            label = stringResource(R.string.full_name_label),
            value = name,
            onValueChange = onNameChange,
            placeholder = stringResource(R.string.full_name_placeholder),
            isError = nameError != null,
            errorMessage = nameError?.let { stringResource(it) },
        )
        Spacer(Modifier.height(16.dp))
        PhoneTextField(
            label = stringResource(R.string.phone_whatsapp_label),
            value = phone,
            onValueChange = onPhoneChange,
            isError = phoneError != null,
            errorMessage = phoneError?.let { stringResource(it) },
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.vendor_onboarding_whatsapp_hint),
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 17.sp
        )
    }
}

@Composable
fun VendorShopStep(
    shopName: String,
    description: String,
    categoryId: String,
    categories: List<Category>,
    shopNameError: Int?,
    descError: Int?,
    onShopNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
) {
    Column {
        MotivAutoCarousel(
            slides = listOf(
                MotivSlide("✨", R.string.vendor_onboarding_shop_slide1_title, R.string.vendor_onboarding_shop_slide1_body),
                MotivSlide("😋", R.string.vendor_onboarding_shop_slide2_title, R.string.vendor_onboarding_shop_slide2_body, Color(0xFFFFF8E1)),
                MotivSlide("🏷️", R.string.vendor_onboarding_shop_slide3_title, R.string.vendor_onboarding_shop_slide3_body, SoftGreen),
            )
        )
        Spacer(Modifier.height(18.dp))
        LabeledTextField(
            label = stringResource(R.string.shop_name_label),
            value = shopName,
            onValueChange = onShopNameChange,
            placeholder = stringResource(R.string.shop_name_placeholder),
            isError = shopNameError != null,
            errorMessage = shopNameError?.let { stringResource(it) },
        )
        Spacer(Modifier.height(16.dp))
        LabeledTextField(
            label = stringResource(R.string.seller_profile_desc_label),
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = stringResource(R.string.vendor_onboarding_desc_placeholder),
            isError = descError != null,
            errorMessage = descError?.let { stringResource(it) },
        )
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.vendor_onboarding_specialty_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = TextPrimary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.vendor_onboarding_specialty_hint),
            fontSize = 12.sp,
            color = TextSecondary
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val selected = cat.id == categoryId
                val bg by animateColorAsState(
                    if (selected) BrandOrange.copy(alpha = 0.14f) else SurfaceBg,
                    TogoMotion.tweenFast(),
                    label = "catBg${cat.id}"
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(bg)
                        .then(
                            if (selected) Modifier.border(1.5.dp, BrandOrange, RoundedCornerShape(20.dp))
                            else Modifier
                        )
                        .clickable { onCategoryChange(cat.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .scale(if (selected) 1.02f else 1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(cat.emoji, fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        cat.label,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) BrandOrange else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun VendorLocationStep(
    zone: String,
    landmark: String,
    findHint: String,
    zoneError: Int?,
    landmarkError: Int?,
    onZoneChange: (String) -> Unit,
    onLandmarkChange: (String) -> Unit,
    onFindHintChange: (String) -> Unit,
) {
    Column {
        MotivAutoCarousel(
            slides = listOf(
                MotivSlide("📍", R.string.vendor_onboarding_location_slide1_title, R.string.vendor_onboarding_location_slide1_body),
                MotivSlide("🛵", R.string.vendor_onboarding_location_slide2_title, R.string.vendor_onboarding_location_slide2_body, Color(0xFFE3F2FD)),
                MotivSlide("🗺️", R.string.vendor_onboarding_location_slide3_title, R.string.vendor_onboarding_location_slide3_body, SoftGreen),
            )
        )
        Spacer(Modifier.height(18.dp))
        LabeledTextField(
            label = stringResource(R.string.seller_profile_zone_label),
            value = zone,
            onValueChange = onZoneChange,
            placeholder = stringResource(R.string.seller_profile_zone_placeholder),
            isError = zoneError != null,
            errorMessage = zoneError?.let { stringResource(it) },
            trailingIcon = { Text("📍") },
        )
        Spacer(Modifier.height(16.dp))
        LabeledTextField(
            label = stringResource(R.string.seller_profile_landmark_label),
            value = landmark,
            onValueChange = onLandmarkChange,
            placeholder = stringResource(R.string.seller_profile_landmark_placeholder),
            isError = landmarkError != null,
            errorMessage = landmarkError?.let { stringResource(it) },
        )
        Spacer(Modifier.height(16.dp))
        LabeledTextField(
            label = stringResource(R.string.vendor_onboarding_find_hint_label),
            value = findHint,
            onValueChange = onFindHintChange,
            placeholder = stringResource(R.string.vendor_onboarding_find_hint_placeholder),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.vendor_onboarding_location_note),
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 17.sp
        )
    }
}

@Composable
fun VendorScheduleStep(
    days: List<DaySchedule>,
    openHour: Int,
    closeHour: Int,
    scheduleError: Int? = null,
    onDayOpenChange: (Int, Boolean) -> Unit,
    onOpenHourChange: (Int) -> Unit,
    onCloseHourChange: (Int) -> Unit,
    onPreset: (SchedulePreset) -> Unit,
) {
    Column {
        MotivAutoCarousel(
            slides = listOf(
                MotivSlide("🟢", R.string.vendor_onboarding_schedule_slide1_title, R.string.vendor_onboarding_schedule_slide1_body, SoftGreen),
                MotivSlide("⚡", R.string.vendor_onboarding_schedule_slide2_title, R.string.vendor_onboarding_schedule_slide2_body),
                MotivSlide("🎛️", R.string.vendor_onboarding_schedule_slide3_title, R.string.vendor_onboarding_schedule_slide3_body, Color(0xFFFFF8E1)),
            )
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.vendor_onboarding_preset_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = TextPrimary
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetChip(stringResource(R.string.vendor_onboarding_preset_mon_sat)) {
                onPreset(SchedulePreset.MON_SAT)
            }
            PresetChip(stringResource(R.string.vendor_onboarding_preset_everyday)) {
                onPreset(SchedulePreset.EVERY_DAY)
            }
            PresetChip(stringResource(R.string.vendor_onboarding_preset_lunch)) {
                onPreset(SchedulePreset.LUNCH_DINNER)
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.vendor_onboarding_hours_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceBg)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HourControl(
                label = stringResource(R.string.seller_schedule_open_hour),
                hour = openHour,
                onMinus = { onOpenHourChange(openHour - 1) },
                onPlus = { onOpenHourChange(openHour + 1) },
            )
            Text("→", fontWeight = FontWeight.Bold, color = TextSecondary)
            HourControl(
                label = stringResource(R.string.seller_schedule_close_hour),
                hour = closeHour,
                onMinus = { onCloseHourChange(closeHour - 1) },
                onPlus = { onCloseHourChange(closeHour + 1) },
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.vendor_onboarding_days_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(10.dp))
        val ordered = days.sortedBy {
            when (it.dayOfWeek) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                else -> 6
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ordered.forEach { day ->
                DayToggle(
                    label = shortDay(day.dayOfWeek),
                    open = day.isOpen,
                    onClick = { onDayOpenChange(day.dayOfWeek, !day.isOpen) },
                )
            }
        }
        if (scheduleError != null) {
            Spacer(Modifier.height(10.dp))
            Text(stringResource(scheduleError), color = Color(0xFFC62828), fontSize = 12.sp)
        }
    }
}

@Composable
fun VendorReviewStep(
    ownerName: String,
    phone: String,
    shopName: String,
    description: String,
    categoryLabel: String,
    zone: String,
    landmark: String,
    findHint: String = "",
    scheduleSummary: String,
    acceptTerms: Boolean,
    termsError: Int?,
    onAcceptTerms: (Boolean) -> Unit,
) {
    val pulse = rememberInfiniteTransition(label = "launchPulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, TogoMotion.tweenSlow()) }

    Column {
        // Hero aperçu boutique
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = appear.value; translationY = (1f - appear.value) * 18f }
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(listOf(HeroPastel, Color.White))
                )
                .border(1.dp, BrandOrange.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "🚀",
                        fontSize = 28.sp,
                        modifier = Modifier.scale(pulseScale)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandOrange.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                stringResource(R.string.vendor_onboarding_review_preview_badge),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandOrange
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            shopName.ifBlank { "—" },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    description.ifBlank { "—" },
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PreviewChip(categoryLabel.ifBlank { "—" })
                    PreviewChip("📍 $zone")
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "$landmark · $scheduleSummary",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            stringResource(R.string.vendor_onboarding_review_next_title),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.togoEnter(delayMs = 80)
        )
        Spacer(Modifier.height(10.dp))
        MotivAutoCarousel(
            slides = listOf(
                MotivSlide("🍲", R.string.vendor_onboarding_review_slide1_title, R.string.vendor_onboarding_review_slide1_body),
                MotivSlide("🟢", R.string.vendor_onboarding_review_slide2_title, R.string.vendor_onboarding_review_slide2_body, SoftGreen),
                MotivSlide("💬", R.string.vendor_onboarding_review_slide3_title, R.string.vendor_onboarding_review_slide3_body, Color(0xFFE3F2FD)),
                MotivSlide("🏙️", R.string.vendor_onboarding_review_slide4_title, R.string.vendor_onboarding_review_slide4_body, Color(0xFFFFF8E1)),
            ),
            autoMs = 2800L,
            modifier = Modifier.togoEnter(delayMs = 120)
        )

        Spacer(Modifier.height(18.dp))

        Text(
            stringResource(R.string.vendor_onboarding_review_summary),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.togoEnter(delayMs = 160)
        )
        Spacer(Modifier.height(10.dp))

        SummaryCard(
            emoji = "👤",
            title = stringResource(R.string.vendor_onboarding_review_account),
            lines = listOf(ownerName, "+228 $phone"),
            delayMs = 180,
        )
        Spacer(Modifier.height(8.dp))
        SummaryCard(
            emoji = "🏪",
            title = stringResource(R.string.vendor_onboarding_review_shop),
            lines = listOf(shopName, categoryLabel, description),
            delayMs = 220,
        )
        Spacer(Modifier.height(8.dp))
        SummaryCard(
            emoji = "📍",
            title = stringResource(R.string.vendor_onboarding_review_location),
            lines = buildList {
                add(zone)
                add(landmark)
                if (findHint.isNotBlank()) add(findHint)
            },
            delayMs = 260,
        )
        Spacer(Modifier.height(8.dp))
        SummaryCard(
            emoji = "🕐",
            title = stringResource(R.string.seller_schedule_title),
            lines = listOf(scheduleSummary),
            delayMs = 300,
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SoftGreen)
                .padding(12.dp)
                .togoEnter(delayMs = 320),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("✅", fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                stringResource(R.string.vendor_onboarding_review_ready),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftGreenText,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(14.dp))

        val termsBg by animateColorAsState(
            if (acceptTerms) BrandOrangeLight else SurfaceBg,
            TogoMotion.tweenNormal(),
            label = "termsBg"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(termsBg)
                .border(
                    width = if (acceptTerms) 1.5.dp else 0.dp,
                    color = if (acceptTerms) BrandOrange else Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable { onAcceptTerms(!acceptTerms) }
                .padding(14.dp)
                .togoEnter(delayMs = 340),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (acceptTerms) BrandOrange else Color.White)
                    .border(
                        1.5.dp,
                        if (acceptTerms) BrandOrange else Color(0xFFBDBDBD),
                        RoundedCornerShape(7.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = acceptTerms,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.6f)) togetherWith fadeOut()
                    },
                    label = "checkAnim"
                ) { checked ->
                    if (checked) {
                        Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Spacer(Modifier.size(8.dp))
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                stringResource(R.string.vendor_onboarding_terms),
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
        if (termsError != null) {
            Spacer(Modifier.height(6.dp))
            Text(stringResource(termsError), color = Color(0xFFC62828), fontSize = 12.sp)
        }
    }
}

@Composable
private fun PreviewChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceBg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SummaryCard(
    emoji: String,
    title: String,
    lines: List<String>,
    delayMs: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceBg)
            .padding(14.dp)
            .togoEnter(delayMs = delayMs),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BrandOrangeLight),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandOrange)
            Spacer(Modifier.height(4.dp))
            lines.filter { it.isNotBlank() }.take(3).forEach { line ->
                Text(
                    line,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrandOrangeLight)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BrandOrange)
    }
}

@Composable
private fun HourControl(
    label: String,
    hour: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onMinus) {
                Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
            }
            AnimatedContent(
                targetState = hour,
                transitionSpec = {
                    (fadeIn(TogoMotion.tweenFast()) + slideInVertically { it / 2 }) togetherWith
                        fadeOut(TogoMotion.tweenFast())
                },
                label = "hourAnim"
            ) { h ->
                Text(
                    "${h}h",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.width(44.dp),
                    color = TextPrimary
                )
            }
            TextButton(onClick = onPlus) {
                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
            }
        }
    }
}

@Composable
private fun DayToggle(label: String, open: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(
        if (open) BrandOrange else SurfaceBg,
        TogoMotion.tweenFast(),
        label = "dayBg$label"
    )
    val fg by animateColorAsState(
        if (open) Color.White else TextSecondary,
        TogoMotion.tweenFast(),
        label = "dayFg$label"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
        }
    }
}

@Composable
private fun shortDay(dayOfWeek: Int): String = stringResource(
    when (dayOfWeek) {
        Calendar.MONDAY -> R.string.vendor_day_mon_short
        Calendar.TUESDAY -> R.string.vendor_day_tue_short
        Calendar.WEDNESDAY -> R.string.vendor_day_wed_short
        Calendar.THURSDAY -> R.string.vendor_day_thu_short
        Calendar.FRIDAY -> R.string.vendor_day_fri_short
        Calendar.SATURDAY -> R.string.vendor_day_sat_short
        else -> R.string.vendor_day_sun_short
    }
)
