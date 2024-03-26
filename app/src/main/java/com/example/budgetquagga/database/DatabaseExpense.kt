package com.example.budgetquagga.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//  Entity for storing expenses in database
@Entity(foreignKeys = [ForeignKey(entity = DatabaseCategory::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("categoryID"),
    onDelete = ForeignKey.CASCADE)],
    tableName = "expenses"
)
data class DatabaseExpense(
    val amountWhole: Int,
    val amountDecimal: Int,
    val lat: Float,
    val lon: Float,
    val categoryID: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}