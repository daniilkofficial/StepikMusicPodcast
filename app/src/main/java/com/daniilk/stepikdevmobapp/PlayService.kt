package com.daniilk.stepikdevmobapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.*

class PlayService : Service() {
    var player: MediaPlayer? = null
    var notification: NotificationCompat.Builder? = null

    // Запуск
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "stop") {
            player?.stop()
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(333)
            stopSelf()
            return START_NOT_STICKY
        }
        player?.stop()

        val url = intent!!.extras?.getString("mp3")
        player = MediaPlayer()
        player?.setDataSource(this, Uri.parse(url))
        player?.setOnPreparedListener { p ->
            if (p != player)
                return@setOnPreparedListener
            p.start()

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (!p.isPlaying) {
                        timer.cancel()
                        return
                    }
                    notification?.setContentText("${p.currentPosition / 100} sec / ${p.duration}")
                    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
                        333,
                        notification?.build()
                    )
                }

            }, 1000, 1000)
        }
        player?.prepareAsync()

        val notificationIntent = Intent(
            this,
            MainActivity::class.java
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val iStop = Intent(this, PlayService::class.java).setAction("stop")
        val piStop = PendingIntent.getService(this, 0, iStop, PendingIntent.FLAG_CANCEL_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel: NotificationChannel =
                NotificationChannel(
                    "1",
                    "mynewchannel",
                    NotificationManager.IMPORTANCE_LOW
                )

            (this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                notificationChannel
            )
        }
        notification = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("MP3")
            .setContentText("")
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            )
            .addAction(R.mipmap.ic_launcher, "Stop", piStop)
            .setAutoCancel(true)
            .setOngoing(false)

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            333,
            notification?.build()
        )

        // START_NOT_STICKY // убился не будет восстановлен
        // START_REDELIVER_INTENT // будет повторен при убийстве
        // START_STICKY // не будет пренаправлен (повторен) при убийстве
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player?.stop()
        super.onDestroy()
    }

    // Без подключения к запущеному
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}