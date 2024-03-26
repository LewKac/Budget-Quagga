package com.example.budgetquagga.database

import android.app.Application
import android.util.Log
import com.example.budgetquagga.*
import java.util.*
import kotlin.collections.ArrayList

/** DatabaseRepository is a higher level abstraction
 *  to interact with the room database
 */
class DatabaseRepository(application: Application) {
    private val dbDao: Dao      //  Declaring dao instance

    //  Initializing dao instance
    init {
        val db: BudgetDatabase = BudgetDatabase.getInstance(
            application.applicationContext
        )!!
        dbDao = db.dao()
    }

    /** Utility functions*/

    //  Deletes ALL data stored in database
    fun deleteAllDataInDatabase() {
        dbDao.deleteAllBudgets()
    }

    /** Budget functions */

    //  addBudget adds a budget to the database
    fun addBudget(budget: Budget) {
        dbDao.insertBudget(DatabaseBudget(budget.amount.whole,    //  Inserting budget in database
                                          budget.amount.decimal,
                                          budget.name))

        for (i in budget.days){         //  Inserting all days in budget to database
            addDayToBudget(i, budget.name)
            var tempDay = getLastDayAdded()
        }
    }


    //  Fetches a single budget from database
    fun getBudget(budgetName: String): Budget {
        val temp = dbDao.getBudget(budgetName)     //  Getting budget from database
        val budget = Budget(null,     // Converting form DatabaseBudget to Budget
                            null,
                            MoneyAmount(temp.wholeAmount, temp.decimalAmount),
                            temp.name
        )

        //  Filling the budget with corresponding days from database
        for (day in getAllDaysInBudget(budget.name)) {
            budget.days.add(Day(day.date, day.ID))
        }


        var allDays = budget.days

        //  Filling the days with corresponding categories and amounts from database
        for(day in allDays) {
            var foo = ArrayList<CategoryAndAmount>()

            for (cat in getAllCategoriesInDay(day.ID!!)) {      // getting categories for a day
                val bar = CategoryAndAmount(cat)

                for (expense in getAllExpensesInCategory(cat.ID!!)) {
                    bar.expenses.add(expense)
                }

                foo.add(bar)
            }

            day.categories = foo
            Log.d("DEB", "DAYDAYDAY: " + day.categories.toString())
        }

        budget.days = allDays
        Log.d("DEB", "FOOFOOFOO: " + budget.toString())

        return budget
    }


    //  Fetches all of the budgets from the database
    fun getAllBudgets(): ArrayList<Budget> {
        val dbBudgets = dbDao.getAllBudgets()//  Getting budgets from database
        var returnBudgets = ArrayList<Budget>()     //  List to be filled and returned

        //  Adding all budget entities from database to the return list
        for (budget in dbBudgets) {
            returnBudgets.add(Budget(null,
                                     null,
                                     MoneyAmount(budget.wholeAmount, budget.decimalAmount),
                                     budget.name))
        }

        if (returnBudgets.size == 0){   //  Exits if there's no budgets
            return ArrayList<Budget>()
        }

        //  Adding day entities from database to budgets
        for (budget in returnBudgets) {
           for (day in getAllDaysInBudget(budget.name)) {
               budget.days.add(Day(day.date, day.ID))
           }
        }

        //  Adding categories and expense entities from database to the budgets
        for (budget in returnBudgets) {
            var allDays = budget.days
            //  Filling the days with corresponding categories and amounts from database
            for(day in allDays) {
                var foo = ArrayList<CategoryAndAmount>()

                for (cat in getAllCategoriesInDay(day.ID!!)) {      // getting categories for a day
                    val bar = CategoryAndAmount(cat)

                    for (expense in getAllExpensesInCategory(cat.ID!!)) {
                        bar.expenses.add(expense)
                    }

                    foo.add(bar)
                }

                day.categories = foo
                Log.d("DEB", "DAYDAYDAY: " + day.categories.toString())
            }

            budget.days = allDays
            Log.d("DEB", "FOOFOOFOO: " + budget.toString())
        }

        return returnBudgets
    }

    /** Day functions */

    //  getAllDaysInBudget fetches all day entities to
    //  the corresponding budget from database
    fun getAllDaysInBudget(budgetName: String): ArrayList<Day> {
        val dbDays = dbDao.getDaysInBudget(budgetName)
        var returnDays = ArrayList<Day>()


        for (item in dbDays) {//  Gets a specified category from DB
            returnDays.add(Day(Date(item.date), item.id))
        }

        return returnDays       //  Returning all days
    }

    fun getLastDayAdded(): Day {
        val day = dbDao.getLastDayAdded()
        val returnDay = Day(Date(day.date), day.id)
        return returnDay
    }

    //  Abstracted function for adding a day and all it's data to database
    fun addDayToBudget(day: Day, budgetName: String) {
        dbDao.insertDay(DatabaseDay(day.date.toString(), budgetName))

        var tempDay = getLastDayAdded()

        for (item in day.categories) {
            addCategoryToDay(item.getCategory(), tempDay.ID!!)
            var tempCategory = getLastCategoryAdded()
            addMultipleExpensesToCategory(item.expenses, tempCategory.ID!!)
        }
    }

    //  Abstracted function for adding multiple days and all their data to database
    fun addMultipleDaysToBudget(days: ArrayList<Day>, budgetName: String) {
        for (item in days) {
            addDayToBudget(item, budgetName)
        }
    }


    /** Category functions */

    //  Abstracted function for adding a category to a day in database
    fun addCategoryToDay(category: Category, dayID: Int) {
        dbDao.insertCategory(DatabaseCategory(category.name, category.color, dayID))
    }

    //  fetches the last category added to database
    fun getLastCategoryAdded(): Category {
        val cat = dbDao.getLastCategoryAdded()
        val returnCat = Category(cat.name, cat.color, cat.id)
        return returnCat
    }

    //  Abstracted function for getting all categories to a specified day from database
    fun getAllCategoriesInDay(dayID: Int): ArrayList<Category> {
        val dbCategories = dbDao.getAllCategoriesInDay(dayID)

        var returnCategories = ArrayList<Category>()
        for (item in dbCategories) {
            returnCategories.add(Category(item.name, item.color, item.id))
        }

        return returnCategories
    }


    /** Expense functions */

    //  getAllExpensesInCategory fetches all expenses to
    //  corresponding category from database
    fun getAllExpensesInCategory(categoryID: Int): ArrayList<SingleExpense> {
        val expenses = dbDao.getAllExpensesInCategory(categoryID)   //  Fetching expenses from DB

        var returnExpenses = ArrayList<SingleExpense>()       //  List to be returned
        val foo = dbDao.getCategory(categoryID)             //  Fetching category from database
        val bar = Category(foo.name, foo.color, foo.id)  //  Creating category using fetched data

        //  Adding all expenses to return list
        for (item in expenses) {
            returnExpenses.add(
                SingleExpense(bar, MoneyAmount(item.amountWhole, item.amountDecimal),
                    item.lat, item.lon))
        }

        return returnExpenses       //  Returning list of expenses
    }

    //  addSingleExpense() adds a expense to category in database
    fun addSingleExpense(expense: SingleExpense, categoryID: Int) {
    //    Log.d("DEB", "categoryID: " + categoryID.toString())
        dbDao.insertSingleExpense(DatabaseExpense(expense.amount.whole,
                                                  expense.amount.decimal,
                                                  expense.lat,
                                                  expense.lon,
                                                  categoryID))
    }

    //  Abstracted function for adding multiple expenses to a specified category in database
    fun addMultipleExpensesToCategory(expenses: ArrayList<SingleExpense>, categoryID: Int) {
        for (item in expenses) {    //  Loops through list of expenses and adds them to database
            addSingleExpense(item, categoryID)
        }
    }
}