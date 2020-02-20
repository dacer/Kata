package im.dacer.kata.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import im.dacer.kata.R

/**
 * Created by Dacer on 09/02/2018.
 */
class NotificationUtil {

    companion object {
        const val NOTIFICATION_ID = 111
        private const val CHANNEL_ID = "nothing_channel"

        fun getNotification(context: Context) : Notification {
            createNotiChannel(context)
            return NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_gesture)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setShowWhen(false)
                    .build()
        }

        @SuppressLint("NewApi")
        private fun createNotiChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val importance = NotificationManager.IMPORTANCE_MIN
                val notificationChannel = NotificationChannel(CHANNEL_ID,
                        context.getString(R.string.enhanced_mode),
                        importance)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

    }
}