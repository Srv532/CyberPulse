package com.cyberpulse.core.notification

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notification Manager for handling FCM topic subscriptions
 */
@Singleton
class NotificationManager @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) {
    
    /**
     * Subscribe to critical alerts (high priority security issues)
     */
    suspend fun subscribeToCriticalAlerts(): Result<Unit> {
        return try {
            firebaseMessaging.subscribeToTopic(CyberPulseMessagingService.TOPIC_CRITICAL).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Subscribe to all news updates
     */
    suspend fun subscribeToAllNews(): Result<Unit> {
        return try {
            firebaseMessaging.subscribeToTopic(CyberPulseMessagingService.TOPIC_ALL_NEWS).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Subscribe to breach alerts
     */
    suspend fun subscribeToBreaches(): Result<Unit> {
        return try {
            firebaseMessaging.subscribeToTopic(CyberPulseMessagingService.TOPIC_BREACHES).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Subscribe to hackathon/CTF alerts
     */
    suspend fun subscribeToHackathons(): Result<Unit> {
        return try {
            firebaseMessaging.subscribeToTopic(CyberPulseMessagingService.TOPIC_HACKATHONS).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Subscribe to daily tips
     */
    suspend fun subscribeToDailyTips(): Result<Unit> {
        return try {
            firebaseMessaging.subscribeToTopic(CyberPulseMessagingService.TOPIC_DAILY_TIPS).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Unsubscribe from a topic
     */
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return try {
            firebaseMessaging.unsubscribeFromTopic(topic).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Unsubscribe from all topics
     */
    suspend fun unsubscribeFromAll() {
        listOf(
            CyberPulseMessagingService.TOPIC_CRITICAL,
            CyberPulseMessagingService.TOPIC_ALL_NEWS,
            CyberPulseMessagingService.TOPIC_BREACHES,
            CyberPulseMessagingService.TOPIC_HACKATHONS,
            CyberPulseMessagingService.TOPIC_DAILY_TIPS
        ).forEach { topic ->
            try {
                firebaseMessaging.unsubscribeFromTopic(topic).await()
            } catch (e: Exception) {
                // Log but don't fail
            }
        }
    }
    
    /**
     * Get current FCM token
     */
    suspend fun getToken(): String? {
        return try {
            firebaseMessaging.token.await()
        } catch (e: Exception) {
            null
        }
    }
}
