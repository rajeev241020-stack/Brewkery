package com.intern.brewkeryapp.UX.Screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.intern.brewkeryapp.Data.CartCalculator
import com.intern.brewkeryapp.Data.CartLine
import com.intern.brewkeryapp.UX.CircleIconBox
import com.intern.brewkeryapp.UX.Cream
import com.intern.brewkeryapp.UX.Ink
import com.intern.brewkeryapp.UX.Muted
import com.intern.brewkeryapp.UX.Orange
import com.intern.brewkeryapp.UX.Outline
import com.intern.brewkeryapp.UX.Peach
import com.intern.brewkeryapp.UX.PrimaryButton
import com.intern.brewkeryapp.UX.QuantityStepper
import com.intern.brewkeryapp.UX.SummaryRow
import com.intern.brewkeryapp.UX.money
import com.intern.brewkeryapp.ViewModel.BrewViewModel


@Composable
fun CartScreen(
    vm: BrewViewModel,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val lines by vm.cart.collectAsState()
    val meta = vm.meta

    val subtotal = CartCalculator.subtotal(lines)
    val tax = CartCalculator.tax(subtotal, meta.taxRatePercent)
    val total = CartCalculator.total(subtotal, meta.deliveryFee, tax)

    Column(Modifier.fillMaxSize().background(Cream).statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconBox(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink)
            }
            Spacer(Modifier.width(12.dp))
            Text("Your Cart", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        }

        if (lines.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛍️", fontSize = 44.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Your cart is empty", color = Ink, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Add something delicious from the menu.", color = Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton("Browse menu", onBack, Modifier.width(180.dp))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(lines, key = { it.lineId }) { line ->
                    CartRow(
                        line = line,
                        onMinus = { vm.changeQuantity(line.lineId, -1) },
                        onPlus = { vm.changeQuantity(line.lineId, +1) },
                        onRemove = { vm.removeLine(line.lineId) }
                    )
                }
                item {
                    Spacer(Modifier.height(6.dp))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Outline, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        SummaryRow("Subtotal", money(subtotal))
                        SummaryRow("Delivery fee", money(meta.deliveryFee))
                        SummaryRow("Tax (${meta.taxRatePercent.toInt()}%)", money(tax))
                        Spacer(Modifier.height(6.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(Outline))
                        Spacer(Modifier.height(6.dp))
                        SummaryRow("Total", money(total), bold = true)
                    }
                    Spacer(Modifier.height(8.dp))
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
                PrimaryButton(
                    text = "Place Order · ${money(total)}",
                    onClick = {
                        vm.placeOrder()
                        onOrderPlaced()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CartRow(
    line: CartLine,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Outline, shape)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = line.item.imageUrl,
            contentDescription = line.item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Peach)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(line.item.name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
            if (line.summary.isNotBlank()) {
                Text(line.summary, color = Muted, fontSize = 11.sp, maxLines = 2)
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantityStepper(line.quantity, onMinus, onPlus)
                Spacer(Modifier.weight(1f))
                Text(money(line.lineTotal), color = Orange, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        }
        Box(
            Modifier.size(36.dp).clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.DeleteOutline, contentDescription = "Remove", tint = Muted, modifier = Modifier.size(20.dp))
        }
    }
}
