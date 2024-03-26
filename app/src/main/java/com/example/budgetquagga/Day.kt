package com.example.budgetquagga

import android.util.Log
import java.util.*

//Represents one day
class Day(date: Date, id: Int?) {
    val date : Date = date
    var categories = mutableListOf<CategoryAndAmount>()
    val ID = id

    fun toStringDEBUG(): String{
        var tempString = "Day: $this"
        tempString += '\n'
        tempString += "CaA size: " + categories.size

        tempString += "\nCategoriesAndAmounts: "
        for (i in categories){
            tempString += i.toString()
            tempString += '\n'
        }

        return tempString
    }

    fun getLocations(): List<Float>{
        var list = mutableListOf<Float>()

        for (i in categories){
            var tempList : List<Float> = i.getLocations()
            list.addAll(tempList)
        }

        return list
    }

    fun addToCategory(expense: SingleExpense){
        for (i in categories){
            if (i.compareCategory(expense.category)){
                i.addExtra(expense)
                return
            }
        }

        var tempCategory = CategoryAndAmount(expense.category)
        tempCategory.addExtra(expense)
        categories.add(tempCategory)
    }

    override fun toString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val tempDate = Date(this.date.time)

        return sdf.format(tempDate).split("T")[0]
    }

    fun getTotal(): MoneyAmount{
        var totalAmount = MoneyAmount(0,0)

        for (i in this.categories){
            totalAmount = totalAmount + i.getTotal()
        }

        return totalAmount
    }

}