package com.intern.brewkeryapp.UX.Screen



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.car.ui.toolbar.MenuItem
import com.intern.brewkeryapp.Data.CartLine
import com.intern.brewkeryapp.Data.UiState
import com.intern.brewkeryapp.UX.Badge
import com.intern.brewkeryapp.UX.CircleIconBox
import com.intern.brewkeryapp.UX.Cream
import com.intern.brewkeryapp.UX.ErrorView
import com.intern.brewkeryapp.UX.Ink
import com.intern.brewkeryapp.UX.LoadingView
import com.intern.brewkeryapp.UX.Muted
import com.intern.brewkeryapp.UX.Orange
import com.intern.brewkeryapp.UX.Outline
import com.intern.brewkeryapp.UX.Peach
import com.intern.brewkeryapp.UX.PrimaryButton
import com.intern.brewkeryapp.UX.QuantityStepper
import com.intern.brewkeryapp.UX.RatingRow
import com.intern.brewkeryapp.UX.SectionTitle
import com.intern.brewkeryapp.UX.money
import com.intern.brewkeryapp.ViewModel.BrewViewModel


@Composable
fun ItemDetailScreen(
    vm: BrewViewModel,
    itemId: Int,
    onBack: () -> Unit,
    onAdded: () -> Unit
) {
    val state by vm.detail.collectAsState()

    LaunchedEffect(itemId) { vm.loadItem(itemId) }

    Box(Modifier.fillMaxSize().background(Cream)) {
        when (val s = state) {
            is UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(s.message, onRetry = { vm.loadItem(itemId) })
            is UiState.Success ->
                if (s.data.id == itemId) {
                    DetailContent(
                        item = s.data,
                        onAdd = { line -> vm.addToCart(line); onAdded() }
                    )
                } else LoadingView()
        }

        CircleIconBox(
            modifier = Modifier.statusBarsPadding().padding(16.dp),
            onClick = onBack
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(item: com.intern.brewkeryapp.Data.MenuItem, onAdd: (CartLine) -> Unit) {
    val c = item.customizations
    var size by remember(item.id) { mutableStateOf(c.sizes.firstOrNull()) }
    var milk by remember(item.id) { mutableStateOf(c.milkOptions.firstOrNull()) }
    var sugar by remember(item.id) { mutableStateOf(c.sugarLevels.firstOrNull()) }
    var qty by remember(item.id) { mutableIntStateOf(1) }

    val unit = item.basePrice + (size?.extraPrice ?: 0.0) + (milk?.extraPrice ?: 0.0)
    val total = unit * qty

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(280.dp).background(Peach)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                item.badge?.let { Badge(it) }
                Spacer(Modifier.height(6.dp))
                Text(item.name, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Spacer(Modifier.height(2.dp))
                Text(item.tagline, color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingRow(item.rating, item.reviewCount)
                    Spacer(Modifier.width(14.dp))
                    Text("⏱ ${item.prepTime}", color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.width(14.dp))
                    Text("🔥 ${item.calories} kcal", color = Muted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text(item.description, color = Ink, fontSize = 14.sp, lineHeight = 20.sp)

                if (item.ingredients.isNotEmpty()) {
                    SectionTitle("Ingredients")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.ingredients.forEach { ing ->
                            Text(
                                ing,
                                fontSize = 12.sp,
                                color = Ink,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Peach)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                if (c.sizes.isNotEmpty()) {
                    SectionTitle("Size")
                    c.sizes.forEach { opt ->
                        OptionRow(
                            label = opt.label,
                            extra = opt.extraPrice,
                            selected = size?.id == opt.id,
                            onSelect = { size = opt }
                        )
                    }
                }

                if (c.milkOptions.isNotEmpty()) {
                    SectionTitle("Milk / Add-ons")
                    c.milkOptions.forEach { opt ->
                        OptionRow(
                            label = opt.name,
                            extra = opt.extraPrice,
                            selected = milk?.id == opt.id,
                            onSelect = { milk = opt }
                        )
                    }
                }

                if (c.sugarLevels.isNotEmpty()) {
                    SectionTitle("Sugar level")
                    c.sugarLevels.forEach { level ->
                        OptionRow(
                            label = level,
                            extra = 0.0,
                            selected = sugar == level,
                            onSelect = { sugar = level }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }


        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Outline)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuantityStepper(
                quantity = qty,
                onMinus = { if (qty > 1) qty-- },
                onPlus = { if (qty < 20) qty++ }
            )
            Spacer(Modifier.width(12.dp))
            PrimaryButton(
                text = "Add to Cart · ${money(total)}",
                onClick = {
                    onAdd(
                        CartLine(
                            item = item,
                            size = size,
                            milk = milk,
                            sugar = sugar,
                            quantity = qty
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OptionRow(label: String, extra: Double, selected: Boolean, onSelect: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(shape)
            .background(if (selected) Peach else Color.White)
            .border(1.dp, if (selected) Orange else Outline, shape)
            .clickable(onClick = onSelect)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = Orange, unselectedColor = Muted),
            modifier = Modifier.size(40.dp)
        )
        Text(label, color = Ink, fontSize = 14.sp, modifier = Modifier.weight(1f))
        if (extra > 0) {
            Text("+${money(extra)}", color = Orange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.width(10.dp))
        }
    }
}
