package com.example.budgetquagga

import android.util.Log

//Represents one unit of money
class MoneyAmount(whole: Int, decimal: Int ) {

    val whole: Int = whole
    val decimal: Int = decimal

    override fun toString(): String {
        if (decimal < 10) {
            return "$whole,0" + decimal
        }
        return "$whole,$decimal"
    }

    operator fun plus(amount: MoneyAmount): MoneyAmount{

        var wholes = this.whole + amount.whole
        var decimals = this.decimal + amount.decimal
        if (decimals > 99){
            wholes += 1
            decimals -= 100
        }

        return MoneyAmount(wholes, decimals)
    }

    operator fun div(factor: Float): Float{
        var total: Float = this.whole.toFloat() + this.decimal/100

        return total.toFloat()/factor
    }

    operator fun div(factor: Int): Int{
        var total: Int = this.whole + this.decimal/100

        return total/factor
    }

    // Function that does subtraction for the MoneyAmount class
    operator fun minus(amount: MoneyAmount): MoneyAmount{
        var wholes = this.whole - amount.whole
        var decimals = this.decimal - amount.decimal

        if (decimals < 0){
            wholes -= 1
            decimals += 100
        }

        return MoneyAmount(wholes, decimals)
    }

    fun toFloat(): Float{
        return this.whole.toFloat() + this.decimal/100
    }

}