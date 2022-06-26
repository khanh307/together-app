package com.example.together

import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class MyWork(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101

    override fun doWork(): Result {
        var begin : Calendar = Calendar.getInstance()
        begin.set(2018, 7, 24)
        var present : Calendar = Calendar.getInstance()

        var dd : Int = ((present.timeInMillis - begin.timeInMillis) / (1000*60*60*24)).toInt()

        sendNotification(dd.toString())
        return Result.success()
    }

    public fun sendNotification(time : String){
        var notificationLayout = RemoteViews(this.applicationContext.packageName, R.layout.layout_notification)
        notificationLayout.setTextViewText(R.id.time, time)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setLargeIcon(null)
            .setSmallIcon(R.drawable.ic_heart_small)
            .setDefaults(0)
            .setSound(null)
            .setCustomContentView(notificationLayout)
            .setAutoCancel(false)
            .setOngoing(true)
        with(NotificationManagerCompat.from(applicationContext)){
            notify(notificationId, builder.build())
        }
    }

}