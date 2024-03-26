package com.example.budgetquagga

class SingleExpense(category: Category, amount: MoneyAmount, lat: Float, lon:Float) {

    val category: Category = category
    val amount = amount
    val lat = lat
    val lon = lon


    override fun toString(): String {
        return amount.toString()
    }
}