package com.example.budgetquagga.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//  Entity for storing categories in database
@Entity(foreignKeys = [ForeignKey(entity = DatabaseDay::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("dayId"),
    onDelete = ForeignKey.CASCADE)],
    tableName = "categories"
)
data class DatabaseCategory(
    val name: String,
    val color: String,
    val dayId: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}