package com.example.budgetquagga.database

import androidx.room.Entity
import androidx.room.PrimaryKey

//  Entity for storing the budget data
@Entity(tableName = "budgets")
data class DatabaseBudget(
    val wholeAmount: Int,
    val decimalAmount: Int,

    @PrimaryKey(autoGenerate = false)
    val name: String
)