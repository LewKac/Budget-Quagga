package com.example.budgetquagga

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.budgetquagga.database.DatabaseRepository
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_new_expense.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class NewExpenseActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //List which contains all options for spinner
    private val spinnerList = ArrayList<String>()

    private val defaultCategories = ArrayList<Category>()
    private lateinit var chosenOption: Category

    private var activeBudget = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var location: Location? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest().apply {

            interval = 5000

            fastestInterval = 500

            maxWaitTime = 10000

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                if (locationResult?.lastLocation != null) {

                    location = locationResult.lastLocation
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        activeBudget = intent.getStringExtra("activeBudget")!!

        if (activeBudget == ""){
            Toast.makeText(this@NewExpenseActivity, "No budget found! Have you forgotten to add one?", Toast.LENGTH_SHORT).show()
            finish()
        }

        defaultCategories.add(Category("Uncategorised", "#808080", 0))
        defaultCategories.add(Category("Food", "#FF0000", 1))
        defaultCategories.add(Category("Games", "#0000FF", 2))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_expense)


        checkLocationPermission()

        //Spinner object
        val spinner = findViewById<Spinner>(R.id.new_expense_category_spinner)
        spinner.onItemSelectedListener = this

        //Add stuff to list
        for (i in defaultCategories){
            spinnerList.add(i.name)
        }



        //Create adapter for spinner
        val dataAdapter = ArrayAdapter(this, R.layout.spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Apply adapter to spinner
        spinner.adapter = dataAdapter

        val budget: Budget = Budget(Date(System.currentTimeMillis()/1000), 10, MoneyAmount(1000, 0), "Test")

        new_expense_amount_editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val df = DecimalFormat("#.##")
                    val stuff = s.toString().toDouble()
                    val stuff1 = df.format(stuff).toDouble()
                    new_expense_submit_button.isEnabled = (stuff == stuff1)
                } catch(e: Exception){
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })


        new_expense_new_category_button.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@NewExpenseActivity)
            alertDialog.setTitle("New Category")
            alertDialog.setMessage("Add a new category you can assign spending to")

            val context: Context = this@NewExpenseActivity
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            val name = EditText(this@NewExpenseActivity)
            name.layoutParams = lp
            name.hint = "Category name"

            val color = EditText(this@NewExpenseActivity)
            color.layoutParams = lp
            color.hint = "Category color"

            layout.addView(name)
            layout.addView(color)

            alertDialog.setView(layout)

            alertDialog.setPositiveButton(
                "Add"
            ) { dialog, which ->

                defaultCategories.add(Category(name.text.toString(), color.text.toString(), 0))
                spinnerList.add(name.text.toString())

                dialog.cancel()
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }
            alertDialog.show()
        }


        new_expense_submit_button.setOnClickListener(){
            val amountEdit = findViewById<EditText>(R.id.new_expense_amount_editText);
            val strs = amountEdit.text
            var temp = strs.split(".").toMutableList()

            if (temp.size < 2 ) {
                temp.add("0")
            }

            try {
                var budget = Budget(Date(System.currentTimeMillis()/1000), 10, MoneyAmount(10000, 0), "bruh")

                runBlocking {
                    val job = GlobalScope.launch {
                        val db = DatabaseRepository(application)
                        budget = db.getBudget(activeBudget)
                    }

                    job.join()
                }

                if (location != null) {
                    val DEBUG_EXPENSE = SingleExpense(
                        chosenOption,
                        MoneyAmount(temp[0].toInt(), temp[1].toInt()),
                        location!!.latitude.toFloat(),
                        location!!.longitude.toFloat()
                    )
                    budget.addToBudget(
                        Day(Date(System.currentTimeMillis()), null),
                        DEBUG_EXPENSE
                    )
                    // get latitude , longitude and other info from this
                } else {
                    val DEBUG_EXPENSE =
                        SingleExpense(chosenOption, MoneyAmount(temp[0].toInt(), temp[1].toInt()), 0.0F, 0.0F)
                    budget.addToBudget(
                        Day(Date(System.currentTimeMillis()), null),
                        DEBUG_EXPENSE
                    )
                }

                runBlocking {
                    val job = GlobalScope.launch {
                        val db = DatabaseRepository(application)
                        db.addBudget(budget)
                    }

                    job.join()
                }

                Snackbar.make(constraintLayout, "Expense added!", Snackbar.LENGTH_LONG)
                    .show()
                finish()


            } catch (e: Exception){
                Snackbar.make(constraintLayout, "Error occured!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    //When an item is selected from spinner list, currently set to debug
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        chosenOption = defaultCategories[position]
    }

    //When nothing is selected from the spinner
    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun checkLocationPermission() {
        val result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (result1 != PackageManager.PERMISSION_GRANTED || result2 != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 101
            )
        }
    }
}

