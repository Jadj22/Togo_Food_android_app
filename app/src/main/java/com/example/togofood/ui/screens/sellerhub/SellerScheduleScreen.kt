package com.example.togofood.ui.screens.sellerhub

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.togofood.domain.model.DaySchedule
import com.example.togofood.domain.model.TimeSlot
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.PrimaryButton
import com.example.togofood.ui.theme.togoEnter
import java.util.Calendar

private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)
private val PageBg = Color(0xFFFAFAFA)
private val CardBg = Color.White
private val SurfaceBg = Color(0xFFF5F5F5)

@Composable
fun SellerScheduleScreen(
    onBackClick: () -> Unit,
    viewModel: SellerScheduleViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(state.saved) {
        if (state.saved) {
            Toast.makeText(ctx, ctx.getString(R.string.seller_schedule_saved), Toast.LENGTH_SHORT).show()
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
                stringResource(R.string.seller_schedule_title),
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
            Text(
                stringResource(R.string.seller_schedule_hint),
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(16.dp))

            state.days.forEach { day ->
                DayScheduleCard(
                    day = day,
                    onOpenChange = { viewModel.setDayOpen(day.dayOfWeek, it) },
                    onOpenHourChange = { index, hour ->
                        viewModel.updateSlotOpenHour(day.dayOfWeek, index, hour)
                    },
                    onCloseHourChange = { index, hour ->
                        viewModel.updateSlotCloseHour(day.dayOfWeek, index, hour)
                    },
                    onAddSlot = { viewModel.addSlot(day.dayOfWeek) },
                    onRemoveSlot = { index -> viewModel.removeSlot(day.dayOfWeek, index) },
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                label = stringResource(R.string.seller_schedule_save),
                isLoading = state.isSaving,
                onClick = viewModel::save,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DayScheduleCard(
    day: DaySchedule,
    onOpenChange: (Boolean) -> Unit,
    onOpenHourChange: (Int, Int) -> Unit,
    onCloseHourChange: (Int, Int) -> Unit,
    onAddSlot: () -> Unit,
    onRemoveSlot: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    dayLabel(day.dayOfWeek),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    stringResource(
                        if (day.isOpen) R.string.seller_schedule_open_day
                        else R.string.seller_schedule_closed_day
                    ),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Switch(
                checked = day.isOpen,
                onCheckedChange = onOpenChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF2F9E44),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                )
            )
        }

        if (day.isOpen) {
            Spacer(Modifier.height(12.dp))
            day.slots.forEachIndexed { index, slot ->
                SlotEditor(
                    slot = slot,
                    canRemove = day.slots.size > 1,
                    onOpenHourChange = { onOpenHourChange(index, it) },
                    onCloseHourChange = { onCloseHourChange(index, it) },
                    onRemove = { onRemoveSlot(index) },
                )
                Spacer(Modifier.height(8.dp))
            }
            if (day.slots.size < 2) {
                TextButton(onClick = onAddSlot) {
                    Text(
                        stringResource(R.string.seller_schedule_add_slot),
                        color = BrandOrange,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotEditor(
    slot: TimeSlot,
    canRemove: Boolean,
    onOpenHourChange: (Int) -> Unit,
    onCloseHourChange: (Int) -> Unit,
    onRemove: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceBg)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HourStepper(
                label = stringResource(R.string.seller_schedule_open_hour),
                hour = slot.openHour,
                range = 0..22,
                onChange = onOpenHourChange,
            )
            Text("→", color = TextSecondary, fontWeight = FontWeight.Bold)
            HourStepper(
                label = stringResource(R.string.seller_schedule_close_hour),
                hour = slot.closeHour,
                range = 1..24,
                onChange = onCloseHourChange,
            )
        }
        if (canRemove) {
            TextButton(onClick = onRemove) {
                Text(
                    stringResource(R.string.seller_schedule_remove_slot),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun HourStepper(
    label: String,
    hour: Int,
    range: IntRange,
    onChange: (Int) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = { onChange((hour - 1).coerceIn(range)) },
                enabled = hour > range.first
            ) {
                Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
            }
            Text(
                "${hour}h",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.width(40.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            TextButton(
                onClick = { onChange((hour + 1).coerceIn(range)) },
                enabled = hour < range.last
            ) {
                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
            }
        }
    }
}

@Composable
private fun dayLabel(dayOfWeek: Int): String = stringResource(
    when (dayOfWeek) {
        Calendar.MONDAY -> R.string.seller_schedule_day_mon
        Calendar.TUESDAY -> R.string.seller_schedule_day_tue
        Calendar.WEDNESDAY -> R.string.seller_schedule_day_wed
        Calendar.THURSDAY -> R.string.seller_schedule_day_thu
        Calendar.FRIDAY -> R.string.seller_schedule_day_fri
        Calendar.SATURDAY -> R.string.seller_schedule_day_sat
        else -> R.string.seller_schedule_day_sun
    }
)
