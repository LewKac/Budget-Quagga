package com.example.budgetquagga

import android.util.Log
import java.util.*

class Budget(startDate: Date?, length: Int?, amount: MoneyAmount, name: String) {

    val amount = amount
    var days: MutableList<Day> = mutableListOf<Day>()
    val name: String

    init {
        this.name = name

        if (startDate != null && length != null) {
            for (i in 0..length!!){
                var timestamp = ((startDate!!.time) + (i*86400)) * 1000

                var tempDay = Day(Date(timestamp), null)
                days.add(tempDay)
            }
        }
    }

    fun getLocations(): List<Float>{
        var list = mutableListOf<Float>()

        for (i in days){
            var tempList : List<Float> = i.getLocations()
            list.addAll(tempList)
        }

        return list
    }

    override fun toString(): String {
        var tempString = "Budget\nTotal amount: " + this.amount.toString()
        tempString += '\n'
        tempString += "Days: "

        for (i in days){
            tempString += i.toStringDEBUG()
            tempString += '\n'
        }

        return tempString
    }

    fun getSameDay(day: Day): Int{
        for (i in this.days){
            if (day.toString() == i.toString()){
                return this.days.indexOf(i)
            }

        }

        return -1
    }

    fun addToBudget(today: Day, expense: SingleExpense): Budget{
        var tempDayIndex = this.getSameDay(today)
        if (tempDayIndex == -1){
            throw Exception("Date is not in list!")
        }

        var tempDay = this.days.removeAt(tempDayIndex)
        tempDay.addToCategory(expense)

        this.days.add(tempDayIndex, tempDay)

        return this
    }

    fun isActive(date: Date): Boolean{
        if ((this.days[days.size-1].date.time / 86400000).toInt() <= (date.time / 86400000).toInt()){
            return false
        }

        return true
    }

    fun getTotal(): MoneyAmount{
        var totalAmount = MoneyAmount(0,0)

        for (i in this.days){
            totalAmount += i.getTotal()
        }

        return totalAmount
    }
}