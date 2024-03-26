package com.example.budgetquagga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.budgetquagga.database.DatabaseRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var budgetName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {
            getActiveBudgetName()

            if (budgetName == ""){
                Snackbar.make(main_constraint_layout, "No active budgets!", Snackbar.LENGTH_LONG).show()
            }

        }.start()

        main_new_budget_button.setOnClickListener {
            val intent = Intent(this, NewBudgetActivity :: class.java)
            startActivity(intent)
        }

        main_new_expense_button.setOnClickListener {
            if (budgetName == ""){
                Toast.makeText(this, "No budgets found. Are you sure you added at least one?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, NewExpenseActivity :: class.java)
            intent.putExtra("activeBudget", budgetName)
            startActivity(intent)
        }

        main_budget_overview_button.setOnClickListener {
            if (budgetName == ""){
                Toast.makeText(this, "No budgets found. Are you sure you added at least one?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, BudgetOverviewActivity :: class.java)
            intent.putExtra("activeBudget", budgetName)
            startActivity(intent)
        }

        main_budget_history_button.setOnClickListener {
            val intent = Intent(this, AllBudgetHistory :: class.java)
            startActivity(intent)
        }

        main_spendometer_button.setOnClickListener {
            if (budgetName == ""){
                Toast.makeText(this, "No budgets found. Are you sure you added at least one?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, Spendometer :: class.java)
            intent.putExtra("activeBudget", budgetName)
            startActivity(intent)
        }

        main_map_button.setOnClickListener {
            if (budgetName == ""){
                Toast.makeText(this, "No budgets found. Are you sure you added at least one?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, MapsActivity :: class.java)
            intent.putExtra("activeBudget", budgetName)
            startActivity(intent)
        }

        main_settings_button.setOnClickListener {
            val intent = Intent(this, SettingsActivity:: class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()

        budgetName = ""

        getActiveBudgetName()
    }


    fun getActiveBudgetName() {
        var db = DatabaseRepository(application)
        var budgets = ArrayList<Budget>()

        runBlocking {
            val job = GlobalScope.launch {
                val db = DatabaseRepository(application)
                budgets = db.getAllBudgets()
            }

            job.join()
        }



        if (budgets.size != 0){
            if (budgets[budgets.size-1].isActive(Date(System.currentTimeMillis()))) {
                budgetName = budgets[budgets.size-1].name
            }
        }

        if (budgetName == ""){
            Snackbar.make(main_constraint_layout, "No active budgets!", Snackbar.LENGTH_LONG).show()
        }
    }
}
