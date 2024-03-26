package com.example.budgetquagga

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetquagga.database.DatabaseRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_budget.*
import java.util.*

class NewBudgetActivity : AppCompatActivity() {

    lateinit var budget: Budget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_budget)

        new_budget_submit_button.isEnabled = true

        new_budget_submit_button.setOnClickListener(){
            val temp = findViewById<EditText>(R.id.new_budget_amount_editText)
            val moneyString = temp.text

            if (check()){
                var tempStrings = moneyString.split(".", ",").toMutableList()

                if (tempStrings.size < 2) {
                    tempStrings.add("0")
                }

                val tempMoneyAmount = MoneyAmount(tempStrings[0].toInt(),tempStrings[1].toInt())

                Thread {
                    var db = DatabaseRepository(application)

                    budget = Budget(Date(System.currentTimeMillis()/1000), new_budget_length.text.toString().toInt(), tempMoneyAmount, (System.currentTimeMillis() / 1000).toString())
                    db.addBudget(budget)

                    Snackbar.make(new_budget_constraint_view, "Budget added!", Snackbar.LENGTH_LONG).show()

                    finish()
                }.start()
            }
        }
    }

    fun check(): Boolean{
        var moneyString: String = new_budget_amount_editText.text.toString()
        var tempStrings = moneyString.split(".", ",")

        if (tempStrings.size > 2){
            return false
        }

        return true
    }
}
