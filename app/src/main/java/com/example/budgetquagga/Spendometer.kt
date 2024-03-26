package com.example.budgetquagga

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.budgetquagga.database.DatabaseRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.Date
import java.util.*

class Spendometer : AppCompatActivity() {

    private lateinit var mView: ImageView
    private var activeBudget = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeBudget = intent.getStringExtra("activeBudget")!!

        if (activeBudget == ""){
            Toast.makeText(this@Spendometer, "No budget found! Have you forgotten to add one?", Toast.LENGTH_SHORT).show()
            finish()
        }

        setContentView(R.layout.activity_spendometer)

        mView = findViewById(R.id.spendometerArrow)

        var budget = Budget( Date(System.currentTimeMillis()/1000) , 10, MoneyAmount(1000, 0), "Some String" )

        runBlocking {
            val job = GlobalScope.launch {
                val db = DatabaseRepository(application)
                budget = db.getBudget(activeBudget)
            }

            job.join()
        }

        if (budget != null){
            rotateMeasurer(budget)
        }
    }


    private fun rotateMeasurer(budget: Budget)
    {
        var rotation: Float

        if (budget.getTotal().whole != 0)
        {
            val totalPercentage = budget.amount.toFloat() / 100
            val percentageRotation = budget.getTotal() / totalPercentage.toFloat()

            rotation = ((180 / 100.0f) * percentageRotation) + 3.0f
            if (rotation > 183.0f)
            {
                rotation = 183.0f
            }
        }
        else{
            rotation = 3.0f
        }

        mView.rotation = rotation

        //mView.translationX += 20
       // mView.translationY += 120 - percentageRotation
    }
}
