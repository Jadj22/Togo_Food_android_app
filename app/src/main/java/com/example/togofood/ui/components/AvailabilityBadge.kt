package com.example.togofood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.domain.model.AvailabilityStatus

@Composable
fun AvailabilityBadge(
    status: AvailabilityStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(status.color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circle dot — color NOT the only indicator (text follows)
        androidx.compose.foundation.Canvas(modifier = Modifier) {
            drawCircle(color = status.color, radius = 4.dp.toPx())
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = status.label,
            color = status.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
