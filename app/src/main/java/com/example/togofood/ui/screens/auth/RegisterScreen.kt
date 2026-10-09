package com.example.togofood.ui.screens.auth

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.domain.model.SellerScheduleResolver
import com.example.togofood.domain.model.UserRole
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.LabeledTextField
import com.example.togofood.ui.components.PhoneTextField
import com.example.togofood.ui.components.PrimaryButton
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter

private val BrandOrangeLight = Color(0xFFFBE9E7)
private val BorderGrey = Color(0xFFE0E0E0)

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val selectedRole by viewModel.selectedRole.collectAsState()
    val vendorStep by viewModel.vendorStep.collectAsState()
    val showOtpSheet by viewModel.showOtpSheet.collectAsState()

    val name by viewModel.name.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val phoneError by viewModel.phoneError.collectAsState()
    val nameError by viewModel.nameError.collectAsState()

    val neighborhood by viewModel.neighborhood.collectAsState()
    val acceptTerms by viewModel.acceptTerms.collectAsState()

    val shopName by viewModel.shopName.collectAsState()
    val shopDescription by viewModel.shopDescription.collectAsState()
    val shopCategoryId by viewModel.shopCategoryId.collectAsState()
    val shopNameError by viewModel.shopNameError.collectAsState()
    val shopDescError by viewModel.shopDescError.collectAsState()

    val shopZone by viewModel.shopZone.collectAsState()
    val shopLandmark by viewModel.shopLandmark.collectAsState()
    val shopFindHint by viewModel.shopFindHint.collectAsState()
    val zoneError by viewModel.zoneError.collectAsState()
    val landmarkError by viewModel.landmarkError.collectAsState()

    val scheduleDays by viewModel.scheduleDays.collectAsState()
    val openHour by viewModel.openHour.collectAsState()
    val closeHour by viewModel.closeHour.collectAsState()
    val scheduleError by viewModel.scheduleError.collectAsState()
    val termsError by viewModel.termsError.collectAsState()

    val isOtpLoading by viewModel.isOtpLoading.collectAsState()
    val otpError by viewModel.otpError.collectAsState()

    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimation = true }

    val isClient = selectedRole == UserRole.CLIENT
    val isVendor = selectedRole == UserRole.VENDEUR
    val showRoleSelector = isClient || (isVendor && vendorStep == VendorOnboarding.STEP_ACCOUNT)

    val stepLabel = when {
        isClient -> stringResource(R.string.step_1_1)
        else -> stringResource(
            R.string.vendor_onboarding_step_of,
            vendorStep,
            VendorOnboarding.TOTAL_STEPS
        )
    }

    val (titleRes, subRes, emoji) = when {
        isClient -> Triple(
            R.string.register_client_title,
            R.string.register_client_subtitle,
            "🍲"
        )
        vendorStep == VendorOnboarding.STEP_ACCOUNT -> Triple(
            R.string.register_vendor_title,
            R.string.register_vendor_subtitle,
            "👨‍🍳"
        )
        vendorStep == VendorOnboarding.STEP_SHOP -> Triple(
            R.string.vendor_onboarding_shop_title,
            R.string.vendor_onboarding_shop_subtitle,
            "🏪"
        )
        vendorStep == VendorOnboarding.STEP_LOCATION -> Triple(
            R.string.vendor_onboarding_location_title,
            R.string.vendor_onboarding_location_subtitle,
            "📍"
        )
        vendorStep == VendorOnboarding.STEP_SCHEDULE -> Triple(
            R.string.vendor_onboarding_schedule_title,
            R.string.vendor_onboarding_schedule_subtitle,
            "🕐"
        )
        else -> Triple(
            R.string.vendor_onboarding_review_title,
            R.string.vendor_onboarding_review_subtitle,
            "🚀"
        )
    }

    val buttonLabelRes = when {
        isClient -> R.string.create_account_button
        vendorStep == VendorOnboarding.STEP_ACCOUNT -> R.string.vendor_onboarding_continue_verify
        vendorStep == VendorOnboarding.STEP_REVIEW -> R.string.vendor_onboarding_launch
        else -> R.string.vendor_onboarding_continue
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
            .togoEnter()
            .animateContentSize(TogoMotion.tweenNormal())
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = startAnimation,
            enter = fadeIn(TogoMotion.tweenSlow()) + slideInVertically(TogoMotion.tweenSlow()) { -it / 2 }
        ) {
            RegisterTopBar(
                stepLabel = stepLabel,
                onBack = { if (!viewModel.navigateBack()) onNavigateToLogin() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isVendor) {
            VendorStepProgress(current = vendorStep)
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }

        Crossfade(
            targetState = Pair(selectedRole, vendorStep),
            animationSpec = TogoMotion.tweenNormal(),
            label = "Greeting"
        ) {
            FormGreeting(stringResource(titleRes), stringResource(subRes), emoji)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showRoleSelector) {
            RoleSelector(selectedRole = selectedRole, onRoleChange = viewModel::updateRole)
            Spacer(modifier = Modifier.height(24.dp))
        }

        AnimatedContent(
            targetState = if (isClient) 0 else vendorStep,
            transitionSpec = {
                (fadeIn(TogoMotion.tweenNormal()) + slideInVertically { it / 8 }) togetherWith
                    fadeOut(TogoMotion.tweenFast())
            },
            label = "stepContent"
        ) { step ->
            when {
                isClient -> {
                    Column {
                        LabeledTextField(
                            label = stringResource(R.string.full_name_label),
                            value = name,
                            onValueChange = viewModel::updateName,
                            placeholder = stringResource(R.string.full_name_placeholder),
                            isError = nameError != null,
                            errorMessage = nameError?.let { stringResource(it) },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PhoneTextField(
                            label = stringResource(R.string.phone_label),
                            value = phone,
                            onValueChange = viewModel::updatePhone,
                            isError = phoneError != null,
                            errorMessage = phoneError?.let { stringResource(it) },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LabeledTextField(
                            label = stringResource(R.string.neighborhood_label),
                            value = neighborhood,
                            onValueChange = viewModel::updateNeighborhood,
                            placeholder = stringResource(R.string.neighborhood_placeholder),
                            trailingIcon = { Text("📍") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TermsCheckbox(
                            checked = acceptTerms,
                            onCheckedChange = viewModel::updateAcceptTerms
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                step == VendorOnboarding.STEP_ACCOUNT -> {
                    VendorAccountStep(
                        name = name,
                        phone = phone,
                        nameError = nameError,
                        phoneError = phoneError,
                        onNameChange = viewModel::updateName,
                        onPhoneChange = viewModel::updatePhone,
                    )
                }
                step == VendorOnboarding.STEP_SHOP -> {
                    VendorShopStep(
                        shopName = shopName,
                        description = shopDescription,
                        categoryId = shopCategoryId,
                        categories = viewModel.categories,
                        shopNameError = shopNameError,
                        descError = shopDescError,
                        onShopNameChange = viewModel::updateShopName,
                        onDescriptionChange = viewModel::updateShopDescription,
                        onCategoryChange = viewModel::updateShopCategoryId,
                    )
                }
                step == VendorOnboarding.STEP_LOCATION -> {
                    VendorLocationStep(
                        zone = shopZone,
                        landmark = shopLandmark,
                        findHint = shopFindHint,
                        zoneError = zoneError,
                        landmarkError = landmarkError,
                        onZoneChange = viewModel::updateShopZone,
                        onLandmarkChange = viewModel::updateShopLandmark,
                        onFindHintChange = viewModel::updateShopFindHint,
                    )
                }
                step == VendorOnboarding.STEP_SCHEDULE -> {
                    VendorScheduleStep(
                        days = scheduleDays,
                        openHour = openHour,
                        closeHour = closeHour,
                        scheduleError = scheduleError,
                        onDayOpenChange = viewModel::setDayOpen,
                        onOpenHourChange = viewModel::updateOpenHour,
                        onCloseHourChange = viewModel::updateCloseHour,
                        onPreset = viewModel::applySchedulePreset,
                    )
                }
                else -> {
                    val categoryLabel = viewModel.categories
                        .find { it.id == shopCategoryId }
                        ?.let { "${it.emoji} ${it.label}" }
                        .orEmpty()
                    val scheduleSummary = SellerScheduleResolver
                        .summaryLines(scheduleDays)
                        .joinToString(" · ")
                    VendorReviewStep(
                        ownerName = name,
                        phone = phone,
                        shopName = shopName,
                        description = shopDescription,
                        categoryLabel = categoryLabel,
                        zone = shopZone,
                        landmark = shopLandmark,
                        findHint = shopFindHint,
                        scheduleSummary = scheduleSummary.ifBlank {
                            stringResource(R.string.seller_detail_hours_empty)
                        },
                        acceptTerms = acceptTerms,
                        termsError = termsError,
                        onAcceptTerms = viewModel::updateAcceptTerms,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryButton(
            label = stringResource(buttonLabelRes),
            enabled = true,
            onClick = { viewModel.submitStep(onSuccess = onNavigateToMain) }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showOtpSheet) {
        OtpVerificationBottomSheet(
            phoneNumber = "+228 $phone",
            isLoading = isOtpLoading,
            errorMessage = otpError,
            onConfirm = { otp -> viewModel.confirmOtp(otp) { onNavigateToMain() } },
            onDismiss = viewModel::dismissOtpSheet,
            onOtpChange = viewModel::clearOtpError
        )
    }
}

@Composable
private fun RoleSelector(selectedRole: UserRole, onRoleChange: (UserRole) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        RoleCard(
            emoji = "🛒",
            label = stringResource(R.string.role_client_label),
            description = stringResource(R.string.role_client_desc),
            selected = selectedRole == UserRole.CLIENT,
            onClick = { onRoleChange(UserRole.CLIENT) },
            modifier = Modifier.weight(1f)
        )
        RoleCard(
            emoji = "👨‍🍳",
            label = stringResource(R.string.role_vendor_label),
            description = stringResource(R.string.role_vendor_desc),
            selected = selectedRole == UserRole.VENDEUR,
            onClick = { onRoleChange(UserRole.VENDEUR) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RoleCard(
    emoji: String,
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        if (selected) BrandOrangeLight else Color.White,
        TogoMotion.tweenNormal(),
        label = "roleBg"
    )
    val borderColor by animateColorAsState(
        if (selected) BrandOrange else BorderGrey,
        TogoMotion.tweenNormal(),
        label = "roleBorder"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = description, fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
    }
}

@Composable
private fun RegisterTopBar(stepLabel: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.cd_back),
                modifier = Modifier.size(20.dp),
                tint = Color.Black
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(BrandOrangeLight)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = stepLabel,
                color = BrandOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun FormGreeting(title: String, subtitle: String, emoji: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$title ",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Text(text = emoji, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, fontSize = 15.sp, color = Color.Gray, lineHeight = 22.sp)
    }
}

@Composable
private fun TermsCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = BrandOrange)
        )
        Text(
            text = stringResource(R.string.terms_accept),
            fontSize = 13.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
    }
}
