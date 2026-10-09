package com.intern.brewkeryapp.Data



import com.google.gson.annotations.SerializedName
import java.util.UUID

data class MenuResponse(
    val meta: Meta,
    val categories: List<Category>,
    val items: List<MenuItem>
)

data class Meta(
    val app: String = "Brewkery",
    val tagline: String = "",
    @SerializedName("currency_symbol") val currencySymbol: String = "$",
    @SerializedName("delivery_fee") val deliveryFee: Double = 2.50,
    @SerializedName("tax_rate_percent") val taxRatePercent: Double = 8.0,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: String = "20 - 30 mins"
)

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    @SerializedName("item_count") val itemCount: Int = 0
)

data class MenuItem(
    val id: Int,
    @SerializedName("category_id") val categoryId: String,
    val name: String,
    val tagline: String,
    val description: String,
    @SerializedName("base_price") val basePrice: Double,
    val rating: Double,
    @SerializedName("review_count") val reviewCount: Int,
    @SerializedName("prep_time") val prepTime: String,
    val calories: Int,
    @SerializedName("image_url") val imageUrl: String,
    val badge: String? = null,
    val ingredients: List<String> = emptyList(),
    val customizations: Customizations
)

data class Customizations(
    val sizes: List<SizeOption> = emptyList(),
    @SerializedName("sugar_levels") val sugarLevels: List<String> = emptyList(),
    @SerializedName("milk_options") val milkOptions: List<MilkOption> = emptyList()
)

data class SizeOption(
    val id: String,
    val label: String,
    @SerializedName("extra_price") val extraPrice: Double = 0.0
)

data class MilkOption(
    val id: String,
    val name: String,
    @SerializedName("extra_price") val extraPrice: Double = 0.0
)

data class CartLine(
    val lineId: String = UUID.randomUUID().toString(),
    val item: MenuItem,
    val size: SizeOption?,
    val milk: MilkOption?,
    val sugar: String?,
    val quantity: Int
) {
    val unitPrice: Double
        get() = item.basePrice + (size?.extraPrice ?: 0.0) + (milk?.extraPrice ?: 0.0)
    val lineTotal: Double
        get() = unitPrice * quantity

    val summary: String
        get() = listOfNotNull(size?.label, milk?.name, sugar).joinToString(" • ")

    fun sameAs(other: CartLine) =
        item.id == other.item.id && size?.id == other.size?.id &&
                milk?.id == other.milk?.id && sugar == other.sugar
}

data class Order(
    val ticketId: String,
    val lines: List<CartLine>,
    val subtotal: Double,
    val deliveryFee: Double,
    val tax: Double,
    val total: Double,
    val status: String = "PREPARING"
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
}
