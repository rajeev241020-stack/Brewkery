package com.intern.brewkeryapp.UX.Screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.intern.brewkeryapp.Data.Category
import com.intern.brewkeryapp.Data.MenuItem
import com.intern.brewkeryapp.Data.MenuResponse
import com.intern.brewkeryapp.Data.Order
import com.intern.brewkeryapp.Data.UiState
import com.intern.brewkeryapp.UX.Badge
import com.intern.brewkeryapp.UX.Cream
import com.intern.brewkeryapp.UX.ErrorView
import com.intern.brewkeryapp.UX.Green
import com.intern.brewkeryapp.UX.Ink
import com.intern.brewkeryapp.UX.LoadingView
import com.intern.brewkeryapp.UX.Muted
import com.intern.brewkeryapp.UX.Orange
import com.intern.brewkeryapp.UX.Outline
import com.intern.brewkeryapp.UX.Peach
import com.intern.brewkeryapp.UX.RatingRow
import com.intern.brewkeryapp.UX.money
import com.intern.brewkeryapp.ViewModel.BrewViewModel


@Composable
fun MenuScreen(
    vm: BrewViewModel,
    onItemClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onTrackOrder: () -> Unit
) {
    val state by vm.menu.collectAsState()
    val cart by vm.cart.collectAsState()
    val order by vm.order.collectAsState()

    Box(Modifier.fillMaxSize().background(Cream)) {
        when (val s = state) {
            is UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(s.message, onRetry = vm::loadMenu)
            is UiState.Success -> MenuContent(
                data = s.data,
                cartCount = cart.sumOf { it.quantity },
                order = order,
                onItemClick = onItemClick,
                onCartClick = onCartClick,
                onTrackOrder = onTrackOrder
            )
        }
    }
}

@Composable
private fun MenuContent(
    data: MenuResponse,
    cartCount: Int,
    order: Order?,
    onItemClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onTrackOrder: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filtered = data.items.filter { item ->
        (selectedCategory == null || item.categoryId == selectedCategory) &&
                (query.isBlank() ||
                        item.name.contains(query, ignoreCase = true) ||
                        item.tagline.contains(query, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Header(data.meta.app, cartCount, onCartClick) }

        if (order != null) {
            item { OrderBanner(order, onTrackOrder) }
        }

        item {
            StoreInfoCard(
                eta = data.meta.estimatedDeliveryTime,
                fee = data.meta.deliveryFee
            )
        }
        item { SearchField(query) { query = it } }
        item {
            CategoryRow(
                categories = data.categories,
                selected = selectedCategory,
                onSelect = { selectedCategory = it }
            )
        }

        if (filtered.isEmpty()) {
            item {
                Text(
                    "No items match your search.",
                    color = Muted,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    fontSize = 14.sp
                )
            }
        } else {
            items(filtered, key = { it.id }) { item ->
                MenuItemCard(item) { onItemClick(item.id) }
            }
        }
    }
}

@Composable
private fun Header(storeName: String, cartCount: Int, onCartClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(Ink),
            contentAlignment = Alignment.Center
        ) {
            Text("BK", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Fresh Roast & Bakes", color = Muted, fontSize = 10.sp)
            Text("$storeName Artisans", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        }

        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Outline, CircleShape)
                    .clickable(onClick = onCartClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ShoppingBag, contentDescription = "Cart", tint = Ink, modifier = Modifier.size(20.dp))
            }
            if (cartCount > 0) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Orange)
                        .border(2.dp, Cream, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$cartCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OrderBanner(order: Order, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Ink)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(Green))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Order ${order.ticketId} · ${order.status}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Tap to track your order", color = Color(0xFFCDBFB4), fontSize = 11.sp)
        }
        Text("Track →", color = Orange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun StoreInfoCard(eta: String, fee: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Peach)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.DeliveryDining, contentDescription = null, tint = Orange)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("STORE INFO", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(5.dp).clip(CircleShape).background(Orange))
            }
            Text("Delivery in $eta", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("${money(fee)} flat fee", color = Muted, fontSize = 10.sp)
        }
        Text(
            "Open",
            color = Ink,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Outline, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Search, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text("Search roast, cold brew, pastry…", color = Muted, fontSize = 13.sp)
            }
            BasicTextField(
                value = query,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(color = Ink, fontSize = 13.sp, fontFamily = FontFamily.SansSerif),
                cursorBrush = SolidColor(Orange),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<Category>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item { CategoryChip("All Items", selected == null) { onSelect(null) } }
        items(categories, key = { it.id }) { cat ->
            CategoryChip("${cat.icon} ${cat.name}", selected == cat.id) { onSelect(cat.id) }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Text(
        label,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (selected) Color.White else Ink,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) Ink else Color.White)
            .border(1.dp, if (selected) Ink else Outline, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

@Composable
private fun MenuItemCard(item: MenuItem, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Outline, shape)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Peach)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            item.badge?.let { Badge(it) }
            Spacer(Modifier.height(4.dp))
            Text(item.name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
            Spacer(Modifier.height(2.dp))
            RatingRow(item.rating, item.reviewCount)
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    money(item.basePrice),
                    color = Orange,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    "+ Customize",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Orange)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}