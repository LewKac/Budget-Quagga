package com.example.budgetquagga.database

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {     //  dao interface to interact with database

    /** Budget functions */
    @Query("SELECT * FROM budgets")     //  gets all the budgets from database
    fun getAllBudgets(): List<DatabaseBudget>

    //  Gets a single budget from database
    @Query("SELECT * FROM budgets WHERE name LIKE :budgetName")
    fun getBudget(budgetName: String): DatabaseBudget

    @Insert(onConflict = OnConflictStrategy.REPLACE)    //  Inserts a budget to database
    fun insertBudget(budget: DatabaseBudget)

    @Delete                                             //  Deletes a budget in database
    fun deleteBudget(budget: DatabaseBudget)

    @Query("DELETE FROM budgets")                 //  Deletes all budgets in database
    fun deleteAllBudgets()


    /** Day functions */

    //  gets all days in a specified budget from database
    @Query("SELECT * FROM days WHERE budgetName LIKE :budgetName")
    fun getDaysInBudget(budgetName: String): List<DatabaseDay>

    @Query("SELECT * FROM days ORDER BY id DESC LIMIT 1")   // gets last day added from DB
    fun getLastDayAdded(): DatabaseDay

    @Insert(onConflict = OnConflictStrategy.REPLACE)    //  Inserts a day to database
    fun insertDay(day: DatabaseDay)

    @Delete                                             //  Deletes a specified day from database
    fun deleteDay(day: DatabaseDay)




    /** Category functions */
    @Query("SELECT * FROM categories")        //  Gets all the categories from the database
    fun getAllCategories(): List<DatabaseCategory>

    //  Gets a specified category from database
    @Query("SELECT * FROM categories WHERE id LIKE :id")
    fun getCategory(id: Int): DatabaseCategory

    //  Gets all categories connected to a specified day from database
    @Query("SELECT * FROM categories WHERE dayId LIKE :id")
    fun getAllCategoriesInDay(id: Int): List<DatabaseCategory>

    //  Gets the last category added to database
    @Query("SELECT * FROM categories ORDER BY id DESC LIMIT 1")
    fun getLastCategoryAdded(): DatabaseCategory

    //  Inserts a cetgory to database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: DatabaseCategory)

    //  Deletes a specified category from database
    @Delete
    fun deleteCategory(category: DatabaseCategory)


    /** Expense functions */

    //  Gets all the expenses connected to specified category from database
    @Query("SELECT * FROM expenses WHERE categoryID LIKE :id")
    fun getAllExpensesInCategory(id: Int): List<DatabaseExpense>

    //  Adds an expense to database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleExpense(expense: DatabaseExpense)

    //  Deletes an expense from database
    @Delete
    fun deleteSingleExpense(expense: DatabaseExpense)
}