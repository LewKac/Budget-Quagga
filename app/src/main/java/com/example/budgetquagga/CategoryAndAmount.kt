package com.example.budgetquagga

import android.util.Log

class CategoryAndAmount(category: Category) {

    private val category: Category = category
    val expenses = ArrayList<SingleExpense>()

    fun compareCategory(category: Category): Boolean{

        if (this.category.name == category.name){
            return true
        }

        return false
    }

    fun getCategory(): Category{
        return this.category
    }

    fun addExtra(expense: SingleExpense){
        this.expenses.add(expense)
    }

    override fun toString(): String {
        var tempString = "Category: " + category.name
        tempString += '\n'

        tempString += "Expenses: \n"
        for (i in expenses){
            tempString += i.toString()
            tempString += "\n"
        }

        return tempString
    }

    fun getLocations(): List<Float>{

        var list = mutableListOf<Float>()

        for (i in expenses){
            list.add(i.lat)
            list.add(i.lon)
        }

        return list
    }

    fun getTotal(): MoneyAmount{
        var totalAmount = MoneyAmount(0, 0)

        for (i in this.expenses){
            totalAmount = totalAmount + i.amount
        }

        return totalAmount
    }

}