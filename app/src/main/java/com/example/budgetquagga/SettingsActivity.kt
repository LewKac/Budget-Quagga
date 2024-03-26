package com.example.budgetquagga

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.budgetquagga.database.DatabaseRepository
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val context = applicationContext

        settings_reset_all_button.setOnClickListener {

            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@SettingsActivity)
            alertDialog.setTitle("Warning!!")
            alertDialog.setMessage("This will completely and irrevertably delete all your budgets! Are you sure?")

            alertDialog.setPositiveButton(
                "No, cancel"
            ) { dialog, which ->

                dialog.cancel()

            }
            alertDialog.setNegativeButton(
                "Yes, proceed"
            ) { dialog, which ->

                dialog.cancel()

                Thread {
                    val db =
                        DatabaseRepository(
                            application
                        )
                    db.deleteAllDataInDatabase()
                    finish()
                }.start()
                Toast.makeText(context, "Database removed successfully!", Toast.LENGTH_SHORT)
                    .show()


            }

            alertDialog.show()


        }
    }
}
