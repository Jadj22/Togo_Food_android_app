package com.example.togofood.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togofood.ui.theme.TogoMotion
import kotlin.math.roundToInt

private val TabInk = Color(0xFF111111)
private val TabMuted = Color(0xFF6B6B6B)
private val TabOutline = Color(0xFFE6E6E6)
private val TabOrange = Color(0xFFC2410C)
private val TabOrangeTint = Color(0xFFFFEDE3)

/**
 * Onglets soulignés style Search (indicateur orange animé + badges compteur).
 * Compatible pager (offsetFraction) ou sélection simple.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun UnderlineTabs(
    titles: List<String>,
    counts: List<Int>,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedOffsetFraction: Float = 0f
) {
    val density = LocalDensity.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .selectableGroup()
        ) {
            titles.forEachIndexed { index, title ->
                UnderlineTab(
                    title = title,
                    count = counts.getOrElse(index) { 0 },
                    selected = selectedIndex == index,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabClick(index) }
                )
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val tabWidth = maxWidth / titles.size
            val tabWidthPx = with(density) { tabWidth.toPx() }

            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                thickness = 1.dp,
                color = TabOutline
            )

            Box(
                modifier = Modifier
                    .width(tabWidth)
                    .height(3.dp)
                    .offset {
                        IntOffset(
                            x = (tabWidthPx * (selectedIndex + selectedOffsetFraction)).roundToInt(),
                            y = 0
                        )
                    }
                    .padding(horizontal = 28.dp)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(TabOrange)
            )
        }
    }
}

@Composable
private fun UnderlineTab(
    title: String,
    count: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (selected) TabInk else TabMuted,
        animationSpec = TogoMotion.tweenNormal(),
        label = "tabText"
    )

    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
        )
        Spacer(Modifier.width(6.dp))
        TabCountBadge(count = count, selected = selected)
    }
}

@Composable
private fun TabCountBadge(
    count: Int,
    selected: Boolean
) {
    val bg by animateColorAsState(
        targetValue = if (selected) TabOrangeTint else Color(0xFFF0F0F0),
        animationSpec = TogoMotion.tweenNormal(),
        label = "badgeBg"
    )
    val fg by animateColorAsState(
        targetValue = if (selected) TabOrange else TabMuted,
        animationSpec = TogoMotion.tweenNormal(),
        label = "badgeFg"
    )

    Box(
        modifier = Modifier
            .widthIn(min = 22.dp)
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = fg,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
