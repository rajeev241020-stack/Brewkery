package com.intern.brewkeryapp.Data


import kotlin.math.round

object CartCalculator {

    fun subtotal(lines: List<CartLine>): Double = round2(lines.sumOf { it.lineTotal })

    fun tax(subtotal: Double, taxRatePercent: Double): Double =
        round2(subtotal * taxRatePercent / 100.0)

    fun total(subtotal: Double, deliveryFee: Double, tax: Double): Double =
        round2(subtotal + deliveryFee + tax)

    private fun round2(value: Double): Double = round(value * 100.0) / 100.0
}
