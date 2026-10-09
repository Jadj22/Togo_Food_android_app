package com.example.togofood.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.R
import com.example.togofood.data.repository.SessionRepository
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.theme.togoEnter

private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF666666)

/**
 * Profil (PRD §33). Mode invité par défaut (PRD §34).
 * Si session vendeur → entrée « Ma boutique » (PRD §35).
 */
@Composable
fun ProfileScreen(
    onFavoritesClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSellerHubClick: () -> Unit = {},
) {
    val session by SessionRepository.session.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
                .togoEnter(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BrandOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (session.isVendor) Icons.Default.Home else Icons.Default.Person,
                    contentDescription = null,
                    tint = BrandOrange,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    when {
                        session.isLoggedIn -> session.displayName
                        else -> stringResource(R.string.profile_guest_name)
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                Text(
                    when {
                        session.isVendor -> stringResource(R.string.profile_vendor_subtitle)
                        session.isLoggedIn -> session.phone
                        else -> stringResource(R.string.profile_guest_subtitle)
                    },
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        if (!session.isLoggedIn) {
            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(46.dp)
                    .togoEnter(delayMs = 60),
                shape = RoundedCornerShape(23.dp)
            ) {
                Text(
                    stringResource(R.string.profile_login),
                    color = BrandOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        if (session.isVendor) {
            Spacer(Modifier.height(16.dp))
            Column(modifier = Modifier.togoEnter(delayMs = 80)) {
                SectionTitle(stringResource(R.string.profile_section_vendor))
                ProfileRow(
                    Icons.Default.Home,
                    stringResource(R.string.profile_my_shop),
                    onClick = onSellerHubClick
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.togoEnter(delayMs = 110)) {
            SectionTitle(stringResource(R.string.profile_section_discovery))
            ProfileRow(Icons.Default.Favorite, stringResource(R.string.profile_favorites), onClick = onFavoritesClick)
        }

        if (session.isLoggedIn) {
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = { SessionRepository.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(46.dp)
                    .togoEnter(delayMs = 140),
                shape = RoundedCornerShape(23.dp)
            ) {
                Text(
                    stringResource(R.string.profile_logout),
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))
        Text(
            stringResource(R.string.profile_version),
            fontSize = 12.sp,
            color = Color(0xFF999999),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 6.dp)
    )
}

@Composable
private fun ProfileRow(icon: ImageVector, label: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, label, tint = TextPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFBBBBBB))
    }
}
