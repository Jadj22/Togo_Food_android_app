package com.example.togofood.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togofood.R
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.components.PasswordField
import com.example.togofood.ui.components.PhoneTextField
import com.example.togofood.ui.components.PrimaryButton
import com.example.togofood.ui.theme.TogoFoodTheme
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoEnter

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val phone by viewModel.phone.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val phoneError by viewModel.phoneError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        LoginHeader()
        LoginForm(
            phone = phone,
            onPhoneChange = viewModel::updatePhone,
            phoneError = phoneError,
            password = password,
            onPasswordChange = viewModel::updatePassword,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
            passwordError = passwordError,
            isLoading = isLoading,
            onLoginClick = { viewModel.login { onNavigateToMain() } },
            onNavigateToRegister = onNavigateToRegister
        )
    }
}

@Composable
private fun LoginHeader() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(colors = listOf(Color(0xFFBF360C), Color(0xFFFF9800)))
            )
        )
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(TogoMotion.tweenSlow()) + slideInVertically(TogoMotion.tweenSlow()) { it / 2 },
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.login_greeting_subtitle),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    phone: String,
    onPhoneChange: (String) -> Unit,
    phoneError: Int?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    passwordError: Int?,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .togoEnter(delayMs = 80)
            .animateContentSize(TogoMotion.tweenNormal())
    ) {
        LoginGreeting()
        Spacer(modifier = Modifier.height(28.dp))
        
        PhoneTextField(
            label = stringResource(R.string.phone_label),
            value = phone,
            onValueChange = onPhoneChange,
            isError = phoneError != null,
            errorMessage = phoneError?.let { stringResource(it) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PasswordField(
            label = stringResource(R.string.password_label),
            value = password,
            onValueChange = onPasswordChange,
            passwordVisible = passwordVisible,
            onToggleVisibility = onTogglePasswordVisibility,
            isError = passwordError != null,
            errorMessage = passwordError?.let { stringResource(it) }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PrimaryButton(
            label = stringResource(R.string.login_button),
            isLoading = isLoading,
            onClick = onLoginClick
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        RegisterPrompt(onNavigateToRegister = onNavigateToRegister)
    }
}

@Composable
private fun LoginGreeting() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.login_greeting_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Spacer(Modifier.width(8.dp))
        Text(text = "👋", fontSize = 24.sp)
    }
}

@Composable
private fun RegisterPrompt(onNavigateToRegister: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = stringResource(R.string.register_prompt), color = Color.Gray, fontSize = 15.sp)
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.register_link),
            color = BrandOrange,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.clickable { onNavigateToRegister() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    TogoFoodTheme { LoginScreen(onNavigateToRegister = {}) }
}
