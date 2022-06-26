package com.example.together


import android.Manifest
import android.R.attr.action
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101
    private lateinit var alarmManager : AlarmManager
    private lateinit var pendingIntent : PendingIntent

    private var click = 1
    private var data1 : String? = null
    private var data2 : String? = null
    private var data3 : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE)

        data1 = sharedPreferences.getString("background", null)
        data2 = sharedPreferences.getString("imageOne", null)
        data3 = sharedPreferences.getString("imageTwo", null)
        tvbegin.text = sharedPreferences.getString("begin", tvbegin.text.toString())
        tvnext.text = sharedPreferences.getString("next", tvnext.text.toString())

        if(data1 == null){
            background.setImageResource(R.drawable.background)
        } else {
            background.setImageURI(Uri.parse(data1))
        }

        if(data2 == null){
            image_one.setImageResource(R.drawable.background)
        } else {
            image_one.setImageURI(Uri.parse(data2))
        }

        if(data3 == null){
            image_two.setImageResource(R.drawable.background)
        } else {
            image_two.setImageURI(Uri.parse(data3))
        }


        sbPercent.isEnabled = false

        var animScale : Animation = AnimationUtils.loadAnimation(this, R.anim.heart_scale)
        image_heart.startAnimation(animScale)

        val animDrable = pbPersent.progressDrawable as AnimationDrawable
        animDrable.setEnterFadeDuration(10)
        animDrable.setExitFadeDuration(500)
        animDrable.start()


        var begin : Calendar = Calendar.getInstance()
        begin.set(2018, 7, 24)
        var present : Calendar = Calendar.getInstance()

        var dd : Int = ((present.timeInMillis - begin.timeInMillis) / (1000*60*60*24)).toInt()

        totalDays.text = dd.toString()

        if(totalDays.text.toString().toInt() >= tvnext.text.toString().toInt()){
            var editor: SharedPreferences.Editor = sharedPreferences.edit()
            tvbegin.text = tvnext.text
            tvnext.text = (tvnext.text.toString().toInt() + 100).toString()
            editor.putString("begin", tvbegin.text.toString())
            editor.putString("next", tvnext.text.toString())
            editor.commit()
        }

        sbPercent.progress = totalDays.text.toString().toInt() - tvbegin.text.toString().toInt()
        pbPersent.progress = totalDays.text.toString().toInt() - tvbegin.text.toString().toInt()


        creatNotificationChannel()
        sendNotification(dd.toString())

        background.setOnClickListener {
            choseImage()
            click = 1
        }

        image_one.setOnClickListener{
            choseImage()
            click = 2
        }

        image_two.setOnClickListener{
            choseImage()
            click = 3
        }

        simpleWork()

//        var intent : Intent = Intent(this, AlarmReceiver::class.java)
//        intent.setAction("MyAction")
//        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        pendingIntent = PendingIntent.getBroadcast(
//            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or
//                    PendingIntent.FLAG_IMMUTABLE
//        )
//        alarmManager.set(AlarmManager.RTC, 1655294421986, pendingIntent)


    }

    private fun simpleWork() {
        val mRequest = PeriodicWorkRequest.Builder(
            MyWork::class.java,
             60,
            TimeUnit.MINUTES
        ).build()


        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "my_id",
                ExistingPeriodicWorkPolicy.KEEP,
                mRequest
            )
    }

    private fun creatNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel : NotificationChannel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    public fun sendNotification(time : String){
        var notificationLayout = RemoteViews(packageName, R.layout.layout_notification)
        notificationLayout.setTextViewText(R.id.time, time)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setLargeIcon(null)
            .setSmallIcon(R.drawable.ic_heart_small)
            .setDefaults(0)
            .setSound(null)
            .setCustomContentView(notificationLayout)
            .setAutoCancel(false)
            .setOngoing(true)
        with(NotificationManagerCompat.from(this@MainActivity)){
            notify(notificationId, builder.build())
        }
    }

    companion object{
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }

    private fun choseImage(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                var permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else{
                pickImageFromGallery()
            }
        } else{
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE ->{
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                } else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            var editor: SharedPreferences.Editor = sharedPreferences.edit()
            if(click == 1){
                this.data1 = data?.data!!.toString()
                background.setImageURI(data?.data)
                editor.putString("background", data?.data.toString())
                editor.commit()
            } else if(click == 2){
                this.data2 = data?.data!!.toString()
                image_one.setImageURI(data?.data)
                editor.putString("imageOne", data?.data.toString())
                editor.commit()
            } else if(click == 3){
                this.data3 = data?.data!!.toString()
                image_two.setImageURI(data?.data)
                editor.putString("imageTwo", data?.data.toString())
                editor.commit()
            }

        }
    }





}



