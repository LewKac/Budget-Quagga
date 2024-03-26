package com.example.budgetquagga

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Database
import com.example.budgetquagga.database.DatabaseRepository
import kotlinx.android.synthetic.main.activity_budget_overview.*
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.coroutines.*

class BudgetOverviewActivity : AppCompatActivity() {

    var defaultCategories = mutableListOf<Category>()

    private var activeBudget = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeBudget = intent.getStringExtra("activeBudget")!!

        setContentView(R.layout.activity_budget_overview)


        defaultCategories.add(Category("Uncategorised", "#808080", 0))
        defaultCategories.add(Category("Food", "#FF0000", 1))
        defaultCategories.add(Category("Games", "#0000FF", 2))

        var budget = Budget(Date(System.currentTimeMillis()/1000), 10, MoneyAmount(10000, 0), "bruh")

        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)

        runBlocking {
            val job = GlobalScope.launch {
                val db = DatabaseRepository(application)

                budget = db.getBudget(activeBudget)
            }

            job.join()
        }

        for (day in budget.days) {

            if (day.getTotal().whole == 0 && day.getTotal().decimal == 0){
                continue
            }

            //Create textView for day
            val newDayTextView = TextView(this)
            newDayTextView.text = day.toString()
            newDayTextView.setTextColor(getResources().getColor(R.color.white))

            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            newDayTextView.layoutParams = lp
            linearLayout.addView(newDayTextView)

            var iterator = 0
            for (category in day.categories) {
                val newCategoryTextView = TextView(this)    //New button
                val tempString = category.toString() + "Total: " + category.getTotal().toString()   //What is supposed to be displayed
                newCategoryTextView.text = tempString
                newCategoryTextView.setTextColor(getResources().getColor(R.color.white))
                newCategoryTextView.layoutParams = lp
                linearLayout.addView(newCategoryTextView)
                iterator += 1
            }
            val emptySpaceButton = TextView(this)
            linearLayout.addView(emptySpaceButton)
        }

        budget_overview_total_spent_display_textView.text = budget.getTotal().toString()
        budget_overview_total_display_textView.text = budget.amount.toString()
        budget_overview_total_unspent_display_textView.text = (budget.amount - budget.getTotal()).toString()
    }

    fun noExpenses(day: Day): Boolean{
        var totalCost = MoneyAmount(0, 0)

        for (i in day.categories){
            totalCost = totalCost + i.getTotal()
        }

        if (totalCost.whole != 0 && totalCost.decimal != 0){
            return true
        }

        return false
    }
}
