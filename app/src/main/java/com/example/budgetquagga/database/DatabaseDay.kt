package com.example.budgetquagga.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//  Entity for storing days in database
@Entity(foreignKeys = [ForeignKey(entity = DatabaseBudget::class,
    parentColumns = arrayOf("name"),
    childColumns = arrayOf("budgetName"),
    onDelete = ForeignKey.CASCADE)],
    tableName = "days"
)
data class DatabaseDay (
    val date: String,
    val budgetName: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}