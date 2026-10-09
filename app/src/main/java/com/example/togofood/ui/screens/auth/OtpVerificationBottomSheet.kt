package com.example.togofood.ui.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.R
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.PrimaryButton
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val OTP_LENGTH = 4
private const val RESEND_SECONDS = 42

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationBottomSheet(
    phoneNumber: String,
    isLoading: Boolean,
    errorMessage: String?,
    onConfirm: (otp: String) -> Unit,
    onDismiss: () -> Unit,
    onOtpChange: () -> Unit // Called when user types to clear error
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var otpValue by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableIntStateOf(RESEND_SECONDS) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1_000L)
            secondsLeft--
        }
    }

    // Shake animation state
    val shakeOffset = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // Heavy haptic for error
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            // Shake pattern
            for (i in 0..2) {
                shakeOffset.animateTo(15f, animationSpec = tween(50))
                shakeOffset.animateTo(-15f, animationSpec = tween(50))
            }
            shakeOffset.animateTo(0f, animationSpec = tween(50))
            
            // Clear the OTP so the user can retype immediately
            otpValue = ""
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OtpHeaderIcon()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.otp_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.otp_subtitle),
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                text = phoneNumber,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BrandOrange
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // Error Message (if any)
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // OTP Input with Shake Modifier
            Box(
                modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            ) {
                OtpInputField(
                    value = otpValue,
                    hasError = errorMessage != null,
                    onValueChange = { newValue ->
                        if (errorMessage != null) onOtpChange()
                        otpValue = newValue
                        if (newValue.length == OTP_LENGTH) {
                            // Light haptic when complete
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            ResendCodeRow(secondsLeft = secondsLeft, onResend = { secondsLeft = RESEND_SECONDS })
            Spacer(modifier = Modifier.height(24.dp))
            
            PrimaryButton(
                label = stringResource(R.string.confirm_button),
                enabled = otpValue.length == OTP_LENGTH,
                isLoading = isLoading,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onConfirm(otpValue)
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OtpHeaderIcon() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(color = Color(0xFFFBE9E7), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_otp_phone),
            contentDescription = null,
            tint = BrandOrange,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun OtpInputField(
    value: String,
    hasError: Boolean,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = value,
        onValueChange = { input ->
            if (input.length <= OTP_LENGTH && input.all(Char::isDigit)) {
                onValueChange(input)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.focusRequester(focusRequester),
        textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
        singleLine = true,
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(OTP_LENGTH) { index ->
                    OtpDigitBox(
                        digit = value.getOrNull(index)?.toString() ?: "",
                        isActive = index == value.length && value.length < OTP_LENGTH,
                        isFilled = index < value.length,
                        hasError = hasError,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isActive: Boolean,
    isFilled: Boolean,
    hasError: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        hasError -> Color.Red
        isActive -> BrandOrange
        isFilled -> BrandOrange.copy(alpha = 0.5f)
        else     -> Color(0xFFE0E0E0)
    }

    Box(
        modifier = modifier
            .height(60.dp)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ResendCodeRow(secondsLeft: Int, onResend: () -> Unit) {
    if (secondsLeft > 0) {
        val formatted = "%02d:%02d".format(secondsLeft / 60, secondsLeft % 60)
        Row {
            Text(text = stringResource(R.string.resend_code_in), fontSize = 13.sp, color = Color.Gray)
            Text(
                text = formatted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BrandOrange
            )
        }
    } else {
        TextButton(onClick = onResend) {
            Text(
                text = stringResource(R.string.resend_code_button),
                color = BrandOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
