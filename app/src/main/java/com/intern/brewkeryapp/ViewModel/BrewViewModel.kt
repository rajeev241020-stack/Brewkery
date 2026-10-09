package com.intern.brewkeryapp.ViewModel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern.brewkeryapp.Data.BrewRepository
import com.intern.brewkeryapp.Data.CartCalculator
import com.intern.brewkeryapp.Data.CartLine
import com.intern.brewkeryapp.Data.MenuItem
import com.intern.brewkeryapp.Data.MenuResponse
import com.intern.brewkeryapp.Data.Meta
import com.intern.brewkeryapp.Data.Order
import com.intern.brewkeryapp.Data.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrewViewModel(private val repo: BrewRepository = BrewRepository()) : ViewModel() {

    private val _menu = MutableStateFlow<UiState<MenuResponse>>(UiState.Loading)
    val menu: StateFlow<UiState<MenuResponse>> = _menu.asStateFlow()

    private val _detail = MutableStateFlow<UiState<MenuItem>>(UiState.Loading)
    val detail: StateFlow<UiState<MenuItem>> = _detail.asStateFlow()

    private val _cart = MutableStateFlow<List<CartLine>>(emptyList())
    val cart: StateFlow<List<CartLine>> = _cart.asStateFlow()

    private val _order = MutableStateFlow<Order?>(null)
    val order: StateFlow<Order?> = _order.asStateFlow()

    init {
        loadMenu()
    }

    fun loadMenu() {
        _menu.value = UiState.Loading
        viewModelScope.launch {
            repo.menu()
                .onSuccess { _menu.value = UiState.Success(it) }
                .onFailure {
                    _menu.value = UiState.Error(
                        "Couldn't load the menu. Check your internet connection and try again."
                    )
                }
        }
    }

    fun loadItem(id: Int) {
        _detail.value = UiState.Loading
        viewModelScope.launch {
            repo.item(id)
                .onSuccess { _detail.value = UiState.Success(it) }
                .onFailure {
                    _detail.value = UiState.Error(
                        "Couldn't load this item. Check your internet connection and try again."
                    )
                }
        }
    }

    val meta: Meta
        get() = (menu.value as? UiState.Success)?.data?.meta ?: Meta()

    fun addToCart(line: CartLine) {
        _cart.update { current ->
            val existing = current.firstOrNull { it.sameAs(line) }
            if (existing == null) current + line
            else current.map {
                if (it.lineId == existing.lineId) it.copy(quantity = it.quantity + line.quantity) else it
            }
        }
    }

    fun changeQuantity(lineId: String, delta: Int) {
        _cart.update { current ->
            current.mapNotNull {
                if (it.lineId != lineId) it
                else {
                    val q = it.quantity + delta
                    if (q <= 0) null else it.copy(quantity = q)
                }
            }
        }
    }

    fun removeLine(lineId: String) {
        _cart.update { current -> current.filterNot { it.lineId == lineId } }
    }

    fun placeOrder() {
        val lines = _cart.value
        if (lines.isEmpty()) return
        val m = meta
        val subtotal = CartCalculator.subtotal(lines)
        val tax = CartCalculator.tax(subtotal, m.taxRatePercent)
        val total = CartCalculator.total(subtotal, m.deliveryFee, tax)
        _order.value = Order(
            ticketId = "BRK-" + (1000..9999).random(),
            lines = lines,
            subtotal = subtotal,
            deliveryFee = m.deliveryFee,
            tax = tax,
            total = total
        )
        _cart.value = emptyList()
    }
}
