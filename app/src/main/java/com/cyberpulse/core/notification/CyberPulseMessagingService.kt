package com.cyberpulse.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.cyberpulse.MainActivity
import com.cyberpulse.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Firebase Cloud Messaging Service
 * 
 * Handles push notifications for:
 * - Critical security alerts
 * - New breach notifications
 * - Hackathon/CTF reminders
 * - Daily security tips
 */
@AndroidEntryPoint
class CyberPulseMessagingService : FirebaseMessagingService() {
    
    companion object {
        // Notification Channels
        const val CHANNEL_CRITICAL = "critical_alerts"
        const val CHANNEL_BREACH = "breach_alerts"
        const val CHANNEL_EVENTS = "event_alerts"
        const val CHANNEL_TIPS = "daily_tips"
        const val CHANNEL_GENERAL = "general"
        
        // Topics for FCM subscriptions
        const val TOPIC_CRITICAL = "critical_alerts"
        const val TOPIC_ALL_NEWS = "all_news"
        const val TOPIC_BREACHES = "breaches"
        const val TOPIC_HACKATHONS = "hackathons"
        const val TOPIC_DAILY_TIPS = "daily_tips"
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to server for push targeting
        sendTokenToServer(token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Check if message contains data payload
        val data = remoteMessage.data
        
        when (data["type"]) {
            "critical" -> showCriticalNotification(data)
            "breach" -> showBreachNotification(data)
            "event" -> showEventNotification(data)
            "tip" -> showTipNotification(data)
            else -> showGeneralNotification(remoteMessage.notification, data)
        }
    }
    
    private fun showCriticalNotification(data: Map<String, String>) {
        val title = data["title"] ?: "Critical Security Alert"
        val body = data["body"] ?: "A critical security issue requires your attention"
        val cveId = data["cve_id"]
        
        createNotificationChannel(
            CHANNEL_CRITICAL,
            "Critical Alerts",
            "High-priority security notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_CRITICAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🚨 $title")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setColor(0xFFFF3366.toInt()) // CyberRed
            .setContentIntent(createPendingIntent(cveId?.let { "cve/$it" }))
            .build()
        
        showNotification(CHANNEL_CRITICAL.hashCode(), notification)
    }
    
    private fun showBreachNotification(data: Map<String, String>) {
        val breachName = data["breach_name"] ?: "New Breach"
        val recordCount = data["record_count"] ?: "unknown"
        val body = data["body"] ?: "$breachName data breach reported"
        
        createNotificationChannel(
            CHANNEL_BREACH,
            "Breach Alerts",
            "Data breach notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_BREACH)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🔴 Data Breach: $breachName")
            .setContentText("$recordCount records exposed")
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setColor(0xFFFF3366.toInt())
            .setContentIntent(createPendingIntent("breach/${data["breach_id"]}"))
            .build()
        
        showNotification(System.currentTimeMillis().toInt(), notification)
    }
    
    private fun showEventNotification(data: Map<String, String>) {
        val eventName = data["event_name"] ?: "Upcoming Event"
        val eventType = data["event_type"] ?: "Event"
        val startsIn = data["starts_in"] ?: ""
        
        createNotificationChannel(
            CHANNEL_EVENTS,
            "Event Alerts",
            "CTF, hackathon, and webinar reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        
        val icon = when (eventType.lowercase()) {
            "ctf" -> "🏴"
            "hackathon" -> "💻"
            "webinar" -> "📺"
            else -> "📅"
        }
        
        val notification = NotificationCompat.Builder(this, CHANNEL_EVENTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("$icon $eventName")
            .setContentText("Starts $startsIn")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setColor(0xFFFFE033.toInt()) // CyberYellow
            .setContentIntent(createPendingIntent("event/${data["event_id"]}"))
            .build()
        
        showNotification(System.currentTimeMillis().toInt(), notification)
    }
    
    private fun showTipNotification(data: Map<String, String>) {
        val tip = data["tip"] ?: "Check today's security tip"
        val category = data["category"] ?: ""
        
        createNotificationChannel(
            CHANNEL_TIPS,
            "Daily Tips",
            "Daily security tips and best practices",
            NotificationManager.IMPORTANCE_LOW
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_TIPS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💡 Daily Security Tip")
            .setContentText(tip)
            .setStyle(NotificationCompat.BigTextStyle().bigText(tip))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setColor(0xFF00FF7F.toInt()) // CyberGreen
            .setContentIntent(createPendingIntent("home"))
            .build()
        
        showNotification(CHANNEL_TIPS.hashCode(), notification)
    }
    
    private fun showGeneralNotification(
        notification: RemoteMessage.Notification?,
        data: Map<String, String>
    ) {
        createNotificationChannel(
            CHANNEL_GENERAL,
            "General",
            "General notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        
        val builder = NotificationCompat.Builder(this, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notification?.title ?: data["title"] ?: "CyberPulse")
            .setContentText(notification?.body ?: data["body"] ?: "")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setColor(0xFF00F5FF.toInt()) // CyberCyan
            .setContentIntent(createPendingIntent(null))
        
        showNotification(System.currentTimeMillis().toInt(), builder.build())
    }
    
    private fun createNotificationChannel(
        channelId: String,
        name: String,
        description: String,
        importance: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = description
                enableLights(true)
                lightColor = 0xFF00F5FF.toInt()
                enableVibration(importance >= NotificationManager.IMPORTANCE_HIGH)
            }
            
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createPendingIntent(deepLink: String?): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            deepLink?.let { putExtra("deep_link", it) }
        }
        
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun showNotification(id: Int, notification: android.app.Notification) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }
    
    private fun sendTokenToServer(token: String) {
        // TODO: Send to your backend for push targeting
        // In a privacy-focused app, you might only do this if user consents
    }
}
