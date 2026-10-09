package com.example.togofood.ui.screens.sellerhub

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.FoodIcons
import com.example.togofood.ui.components.LabeledTextField
import com.example.togofood.ui.components.PrimaryButton
import com.example.togofood.ui.theme.togoEnter

private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)
private val PageBg = Color(0xFFFAFAFA)
private val CardBg = Color.White
private val SurfaceBg = Color(0xFFF5F5F5)

@Composable
fun SellerPrepEditScreen(
    onBackClick: () -> Unit,
    viewModel: SellerPrepEditViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(state.saved) {
        if (state.saved) {
            Toast.makeText(ctx, ctx.getString(R.string.seller_prep_saved), Toast.LENGTH_SHORT).show()
            viewModel.consumeSaved()
            onBackClick()
        }
    }
    LaunchedEffect(state.deleted) {
        if (state.deleted) {
            Toast.makeText(ctx, ctx.getString(R.string.seller_prep_deleted), Toast.LENGTH_SHORT).show()
            viewModel.consumeDeleted()
            onBackClick()
        }
    }

    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDelete,
            title = { Text(stringResource(R.string.seller_prep_delete_confirm_title)) },
            text = { Text(stringResource(R.string.seller_prep_delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) {
                    Text(stringResource(R.string.seller_prep_delete_confirm), color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDelete) {
                    Text(stringResource(R.string.seller_prep_delete_cancel))
                }
            },
        )
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
                stringResource(
                    if (state.isEdit) R.string.seller_prep_edit_title_edit
                    else R.string.seller_prep_edit_title_new
                ),
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
            // Photo placeholder
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            FoodIcons.pastelBackgrounds[state.categoryId] ?: FoodIcons.defaultPastel
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(FoodIcons.emojiFor(state.categoryId), fontSize = 32.sp)
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    stringResource(R.string.seller_prep_photo_hint),
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))
            LabeledTextField(
                label = stringResource(R.string.seller_prep_name_label),
                value = state.name,
                onValueChange = viewModel::updateName,
                placeholder = stringResource(R.string.seller_prep_name_placeholder),
                isError = state.nameError != null,
                errorMessage = state.nameError?.let { stringResource(it) },
            )
            Spacer(Modifier.height(16.dp))
            LabeledTextField(
                label = stringResource(R.string.seller_prep_desc_label),
                value = state.description,
                onValueChange = viewModel::updateDescription,
                placeholder = stringResource(R.string.seller_prep_desc_placeholder),
            )

            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.seller_prep_category_label),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categories.forEach { cat ->
                    val selected = cat.id == state.categoryId
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) BrandOrange.copy(alpha = 0.12f) else SurfaceBg)
                            .then(
                                if (selected) Modifier.border(1.dp, BrandOrange, RoundedCornerShape(20.dp))
                                else Modifier
                            )
                            .clickable { viewModel.updateCategory(cat.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
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

            Spacer(Modifier.height(16.dp))
            ToggleRow(
                label = stringResource(R.string.seller_prep_popular_label),
                checked = state.isPopular,
                onCheckedChange = viewModel::updatePopular,
            )
            Spacer(Modifier.height(8.dp))
            ToggleRow(
                label = stringResource(R.string.seller_prep_available_label),
                checked = state.isAvailable,
                onCheckedChange = viewModel::updateAvailable,
            )

            Spacer(Modifier.height(24.dp))
            Text(
                stringResource(R.string.seller_prep_variants_title),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            if (state.variantsError != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(state.variantsError!!),
                    color = Color(0xFFC62828),
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(12.dp))

            state.variants.forEach { variant ->
                VariantEditorRow(
                    variant = variant,
                    canRemove = state.variants.size > 1,
                    onLabelChange = { viewModel.updateVariantLabel(variant.id, it) },
                    onPriceChange = { viewModel.updateVariantPrice(variant.id, it) },
                    onRemove = { viewModel.removeVariant(variant.id) },
                )
                Spacer(Modifier.height(10.dp))
            }

            TextButton(onClick = viewModel::addVariant) {
                Icon(Icons.Default.Add, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    stringResource(R.string.seller_prep_add_variant),
                    color = BrandOrange,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                label = stringResource(R.string.seller_prep_save),
                isLoading = state.isSaving,
                onClick = viewModel::save,
            )

            if (state.isEdit) {
                Spacer(Modifier.height(12.dp))
                TextButton(
                    onClick = viewModel::requestDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.seller_prep_delete),
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp, color = TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandOrange,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBDBDBD),
            )
        )
    }
}

@Composable
private fun VariantEditorRow(
    variant: VariantDraft,
    canRemove: Boolean,
    onLabelChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onRemove: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                LabeledTextField(
                    label = stringResource(R.string.seller_prep_variant_label),
                    value = variant.label,
                    onValueChange = onLabelChange,
                    placeholder = stringResource(R.string.seller_prep_variant_label_ph),
                )
            }
            if (canRemove) {
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.seller_prep_remove_variant),
                        tint = TextSecondary,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        LabeledTextField(
            label = stringResource(R.string.seller_prep_variant_price),
            value = variant.priceText,
            onValueChange = onPriceChange,
            placeholder = stringResource(R.string.seller_prep_variant_price_ph),
        )
    }
}
