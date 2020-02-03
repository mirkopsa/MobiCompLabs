package com.mobicomp.labs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test_button.setOnClickListener{
            toast("Oh hello")
        }

        fab.setOnClickListener{
            toast("Floating action button!")
        }

        openTimeActivity.setOnClickListener{
            startActivity(Intent(applicationContext, TimeActivity::class.java))
        }

        openMapActivity.setOnClickListener{
            startActivity(Intent(applicationContext, MapActivity::class.java))
        }

        val data = arrayOf("Oulu", "Helsinki", "Tampere")

        val reminderAdapter = ReminderAdapter(applicationContext, data)
        list.adapter = reminderAdapter

    }
}
