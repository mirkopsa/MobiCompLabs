package com.mobicomp.labs

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.AdapterView
import androidx.core.app.NotificationCompat
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Flag for the FAB's state
        var fabOpened = false

        // Subactions
        fab.setOnClickListener{

            if (!fabOpened) {

                fabOpened = true
                // Show subactions
                fab_map.animate().translationY(-resources.getDimension(R.dimen.standard_66))
                fab_time.animate().translationY(-resources.getDimension(R.dimen.standard_116))

            } else {

                fabOpened = false
                // Hide subactions
                fab_map.animate().translationY(0f)
                fab_time.animate().translationY(0f)

            }
        }

        // Open the time-based reminder creation activity
        fab_time.setOnClickListener{
            startActivity(Intent(applicationContext, TimeActivity::class.java))
        }

        // Open the location-based reminder creation activity
        fab_map.setOnClickListener {
            startActivity(Intent(applicationContext, MapActivity::class.java))
        }

        // Listener that performs an action on row element click LAB 6 3:38
        list.onItemClickListener = AdapterView.OnItemClickListener { _ , _ , position, _ ->

            // Retrieve Reminder corresponding to the clicked item
            val selected = list.adapter.getItem(position) as Reminder

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete reminder?")
                .setMessage(selected.message)
                .setPositiveButton("Delete") { _ , _ ->

                    // Cancel scheduled reminder with AlarmManager
                    if (selected.time != null) {
                        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(this@MainActivity, ReminderReceiver::class.java)
                        val pending = PendingIntent.getBroadcast(this@MainActivity,
                            selected.uid!!, intent, PendingIntent.FLAG_ONE_SHOT)
                        manager.cancel(pending)
                    }

                    // Remove reminder from db
                    doAsync {
                        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders")
                            .build()
                        db.reminderDao().delete(selected.uid!!)
                        db.close()

                        // Refresh the UI
                        refreshList()
                    }
                }

                .setNegativeButton("Cancel") { dialog, _ ->
                    // Do nothing
                    dialog.dismiss()
                }

                .show()

        }

    }

    override fun onResume() {

        super.onResume()

        // Refresh the UI
        refreshList()

    }

    // Retrieve latest reminders and refresh the UI
    private fun refreshList () {

        doAsync {

            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders").build()
            val reminders = db.reminderDao().getReminders()

            db.close()

            uiThread {

                if (reminders.isNotEmpty()) {

                    val adapter = ReminderAdapter(applicationContext, reminders)
                    list.adapter = adapter

                } else {
                    toast("No reminders yet")
                }

            }

        }

    }

    companion object {

        fun showNotification(context: Context, message: String) {

            val CHANNEL_ID = "REMINDER_NOTIFICATION_CHANNEL"
            var NotificationID = 1589

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_24dp)
                .setContentTitle(context?.getString(R.string.app_name))
                .setContentText(message)
                .setStyle( NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context?.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context?.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationID + Random(NotificationID).nextInt(1, 30)
            notificationManager.notify(notification, notificationBuilder.build())
        }

    }

}
