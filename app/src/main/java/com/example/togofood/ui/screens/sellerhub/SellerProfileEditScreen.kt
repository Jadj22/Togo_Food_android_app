package com.example.togofood.ui.screens.sellerhub

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.ui.components.LabeledTextField
import com.example.togofood.ui.components.PhoneTextField
import com.example.togofood.ui.components.PrimaryButton
import com.example.togofood.ui.theme.togoEnter

private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)
private val PageBg = Color(0xFFFAFAFA)
private val CardBg = Color.White

@Composable
fun SellerProfileEditScreen(
    onBackClick: () -> Unit,
    viewModel: SellerProfileEditViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(state.saved) {
        if (state.saved) {
            Toast.makeText(ctx, ctx.getString(R.string.seller_profile_saved), Toast.LENGTH_SHORT).show()
            viewModel.consumeSaved()
            onBackClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .togoEnter(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = TextPrimary
                )
            }
            Text(
                stringResource(R.string.seller_profile_edit_title),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TextPrimary,
            )
        }

        if (state.missingSession) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.seller_hub_missing), color = TextSecondary)
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .togoEnter(delayMs = 40)
        ) {
            LabeledTextField(
                label = stringResource(R.string.shop_name_label),
                value = state.name,
                onValueChange = viewModel::updateName,
                placeholder = stringResource(R.string.shop_name_placeholder),
                isError = state.nameError != null,
                errorMessage = state.nameError?.let { stringResource(it) },
            )
            Spacer(Modifier.height(16.dp))
            LabeledTextField(
                label = stringResource(R.string.seller_profile_desc_label),
                value = state.description,
                onValueChange = viewModel::updateDescription,
                placeholder = stringResource(R.string.seller_profile_desc_placeholder),
            )
            Spacer(Modifier.height(16.dp))
            PhoneTextField(
                label = stringResource(R.string.phone_whatsapp_label),
                value = state.phoneDigits,
                onValueChange = viewModel::updatePhone,
                isError = state.phoneError != null,
                errorMessage = state.phoneError?.let { stringResource(it) },
            )
            Spacer(Modifier.height(16.dp))
            LabeledTextField(
                label = stringResource(R.string.seller_profile_zone_label),
                value = state.zone,
                onValueChange = viewModel::updateZone,
                placeholder = stringResource(R.string.seller_profile_zone_placeholder),
            )
            Spacer(Modifier.height(16.dp))
            LabeledTextField(
                label = stringResource(R.string.seller_profile_landmark_label),
                value = state.landmark,
                onValueChange = viewModel::updateLandmark,
                placeholder = stringResource(R.string.seller_profile_landmark_placeholder),
            )
            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                label = stringResource(R.string.seller_profile_save),
                isLoading = state.isSaving,
                onClick = viewModel::save,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
