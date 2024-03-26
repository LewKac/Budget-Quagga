package com.example.budgetquagga.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [DatabaseDay::class,
                      DatabaseBudget::class,
                      DatabaseCategory::class,
                      DatabaseExpense::class], version = 9)
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        /* For Singleton instantiation */
        @Volatile private var dbInstance: BudgetDatabase? = null

        fun getInstance(context: Context): BudgetDatabase? {
            if (dbInstance == null) {
                synchronized(BudgetDatabase::class) {
                    dbInstance = databaseBuilder(
                        context.applicationContext,
                        BudgetDatabase::class.java,
                        "app_database"
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return dbInstance
        }
        /* Used to create database for first time use */
        private fun buildDatabase(context: Context): BudgetDatabase {
            return Room.databaseBuilder(context, BudgetDatabase::class.java, "app_database").build()
        }
    }
}