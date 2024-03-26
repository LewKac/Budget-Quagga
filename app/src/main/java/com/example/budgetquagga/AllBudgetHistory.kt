package com.example.budgetquagga

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetquagga.database.DatabaseRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

class AllBudgetHistory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_budget_history)

        var budgets = ArrayList<Budget>()
        runBlocking {
            val job = GlobalScope.launch {

                val db = DatabaseRepository(application)

                budgets = db.getAllBudgets()

                budgets.reverse()
            }

            job.join()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.allHistoryRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val adapter = CustomAdapter(budgets)

        recyclerView.adapter = adapter
    }
}