package com.example.together

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101
    override fun onReceive(context: Context?, intent: Intent?) {
        var begin : Calendar = Calendar.getInstance()
        begin.set(2018, 7, 24)
        var present : Calendar = Calendar.getInstance()

        var dd : Int = ((present.timeInMillis - begin.timeInMillis) / (1000*60*60*24)).toInt()
        sendNotification(dd.toString(), context)
    }

    public fun sendNotification(time : String, context: Context?){
        var notificationLayout = RemoteViews(context!!.packageName, R.layout.layout_notification)
        notificationLayout.setTextViewText(R.id.time, time)

        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setLargeIcon(null)
            .setSmallIcon(R.drawable.ic_heart_small)
            .setDefaults(0)
            .setSound(null)
            .setCustomContentView(notificationLayout)
            .setAutoCancel(false)
            .setOngoing(true)
        with(NotificationManagerCompat.from(context)){
            notify(notificationId, builder.build())
        }
    }
}