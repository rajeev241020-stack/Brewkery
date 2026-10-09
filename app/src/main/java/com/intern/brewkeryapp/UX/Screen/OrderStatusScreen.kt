package com.intern.brewkeryapp.UX.Screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intern.brewkeryapp.UX.Cream
import com.intern.brewkeryapp.UX.Green
import com.intern.brewkeryapp.UX.Ink
import com.intern.brewkeryapp.UX.Muted
import com.intern.brewkeryapp.UX.Orange
import com.intern.brewkeryapp.UX.Outline
import com.intern.brewkeryapp.UX.Peach
import com.intern.brewkeryapp.UX.PrimaryButton
import com.intern.brewkeryapp.UX.SectionTitle
import com.intern.brewkeryapp.UX.SummaryRow
import com.intern.brewkeryapp.UX.money
import com.intern.brewkeryapp.ViewModel.BrewViewModel


@Composable
fun OrderStatusScreen(vm: BrewViewModel, onBackToMenu: () -> Unit) {
    val order by vm.order.collectAsState()
    val current = order

    Column(Modifier.fillMaxSize().background(Cream).statusBarsPadding()) {
        if (current == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No active order", color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton("Back to menu", onBackToMenu, Modifier.width(180.dp))
                }
            }
            return@Column
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Box(
                Modifier.size(84.dp).clip(CircleShape).background(Peach),
                contentAlignment = Alignment.Center
            ) { Text("☕", fontSize = 40.sp) }
            Spacer(Modifier.height(16.dp))
            Text("Order Placed!", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                "We're getting started on your order.",
                color = Muted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Outline, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TICKET ID", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    current.ticketId,
                    color = Orange,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE3F5EA))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Green))
                    Spacer(Modifier.width(8.dp))
                    Text(current.status, color = Green, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }

            Column(Modifier.fillMaxWidth()) {
                SectionTitle("Order summary")
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Outline, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    current.lines.forEach { line ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text("${line.quantity}× ${line.item.name}", color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                if (line.summary.isNotBlank()) {
                                    Text(line.summary, color = Muted, fontSize = 11.sp)
                                }
                            }
                            Text(money(line.lineTotal), color = Ink, fontSize = 14.sp)
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Outline))
                    SummaryRow("Subtotal", money(current.subtotal))
                    SummaryRow("Delivery fee", money(current.deliveryFee))
                    SummaryRow("Tax", money(current.tax))
                    SummaryRow("Total", money(current.total), bold = true)
                }
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Outline)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            PrimaryButton("Back to Menu", onBackToMenu, Modifier.fillMaxWidth())
        }
    }
}
