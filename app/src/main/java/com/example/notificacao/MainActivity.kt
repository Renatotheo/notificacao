package com.example.notificacao

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val channelId = "default_channel"
    private val notificationPermissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        // Botão para acionar a notificação
        findViewById<View>(R.id.buttonShowNotification).setOnClickListener {
            checkNotificationPermissionAndShow()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel description"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermissionAndShow() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_FULL_SCREEN_INTENT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // A permissão já foi concedida, mostre a notificação
            showNotification()
        } else {
            // Solicitar permissão
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.USE_FULL_SCREEN_INTENT),
                notificationPermissionCode
            )
        }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // ícone da notificação
            .setContentTitle("Título da Notificação")
            .setContentText("Conteúdo da notificação")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val context: Context = this

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.USE_FULL_SCREEN_INTENT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Solicitar permissão, se necessário
                return
            }
            notify(1, builder.build())
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == notificationPermissionCode && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissão concedida, mostre a notificação
            showNotification()
        } else {
            // Permissão negada, trate conforme necessário
            Toast.makeText(
                this,
                "Permissão de notificação negada. A notificação não será exibida.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
