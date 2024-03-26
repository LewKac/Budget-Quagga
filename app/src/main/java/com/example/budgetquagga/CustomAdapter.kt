package com.example.budgetquagga

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


// The adapter to make the recycler view work
class CustomAdapter (val allBudgetsList: ArrayList<Budget>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.activity_all_budget_linear_layout_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return allBudgetsList.size
    }

    // The Function that sets the values for the different parts of each list item
    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val budget: Budget = allBudgetsList[position]

        val totalSpent = MoneyAmount(0, 0)

        // For each textView get the correct info from the respective budget
        holder?.dateFrom?.text = "Starting date: " + budget.days[0].toString()
        holder?.dateTo?.text = "End date: " + budget.days[budget.days.size-1].toString()
        holder?.totalSum?.text = "Total: " + budget.amount.toString()
        holder?.totalUsed?.text = "Total used: " + budget.getTotal().toString()
        holder?.afterCompletion?.text = "Remainder: " + budget.amount.minus(budget.getTotal()).toString()
    }

    // Connect the different parts of the list item with values
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateFrom = itemView.findViewById(R.id.allHistoryDateFrom) as TextView
        val dateTo = itemView.findViewById(R.id.allHistoryDateTo) as TextView
        val totalSum = itemView.findViewById(R.id.allHistoryTotalSum) as TextView
        val totalUsed = itemView.findViewById(R.id.allHistoryUsedSum) as TextView
        val afterCompletion = itemView.findViewById(R.id.allHistoryDifferenceAfter) as TextView
    }
}