package com.cyberpulse.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.cyberpulse.domain.model.DataPreferences
import com.cyberpulse.domain.model.NotificationPreferences
import com.cyberpulse.domain.model.UserProfile
import com.cyberpulse.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
    
    private object PreferenceKeys {
        val STORE_LOGIN = booleanPreferencesKey("store_login_session")
        val ALLOW_ANALYTICS = booleanPreferencesKey("allow_usage_analytics")
        val PERSONALIZE_FEEDS = booleanPreferencesKey("personalize_feeds")
        val SAVE_READ_HISTORY = booleanPreferencesKey("save_read_history")
        val CACHE_OFFLINE = booleanPreferencesKey("cache_feeds_offline")
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey("enable_notifications")
        val STATELESS_MODE = booleanPreferencesKey("stateless_mode")
        
        val CRITICAL_ALERTS_ONLY = booleanPreferencesKey("critical_alerts_only")
        val ALL_NEWS = booleanPreferencesKey("all_news")
        val HACKATHON_ALERTS = booleanPreferencesKey("hackathon_alerts")
        val DAILY_TIPS = booleanPreferencesKey("daily_tips")
        val BREACH_ALERTS = booleanPreferencesKey("breach_alerts")
        val CVE_ALERTS = booleanPreferencesKey("cve_alerts")
    }
    
    override fun getCurrentUser(): Flow<UserProfile?> = flow {
        val user = firebaseAuth.currentUser
        if (user != null) {
            emit(
                UserProfile(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString(),
                    isAnonymous = user.isAnonymous
                )
            )
        } else {
            emit(null)
        }
    }
    
    override suspend fun signInWithGoogle(idToken: String): Result<UserProfile> {
        return withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val user = authResult.user!!
                
                Result.success(
                    UserProfile(
                        uid = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName,
                        photoUrl = user.photoUrl?.toString(),
                        isAnonymous = false
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            firebaseAuth.signOut()
        }
    }
    
    override fun isSignedIn(): Flow<Boolean> = flow {
        emit(firebaseAuth.currentUser != null)
    }
    
    override fun getDataPreferences(): Flow<DataPreferences> {
        return context.dataStore.data.map { prefs ->
            DataPreferences(
                storeLoginSession = prefs[PreferenceKeys.STORE_LOGIN] ?: false,
                allowUsageAnalytics = prefs[PreferenceKeys.ALLOW_ANALYTICS] ?: false,
                personalizeFeeds = prefs[PreferenceKeys.PERSONALIZE_FEEDS] ?: false,
                saveReadHistory = prefs[PreferenceKeys.SAVE_READ_HISTORY] ?: false,
                cacheFeedsOffline = prefs[PreferenceKeys.CACHE_OFFLINE] ?: true,
                enableNotifications = prefs[PreferenceKeys.ENABLE_NOTIFICATIONS] ?: true
            )
        }
    }
    
    override suspend fun updateDataPreferences(preferences: DataPreferences) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.STORE_LOGIN] = preferences.storeLoginSession
            prefs[PreferenceKeys.ALLOW_ANALYTICS] = preferences.allowUsageAnalytics
            prefs[PreferenceKeys.PERSONALIZE_FEEDS] = preferences.personalizeFeeds
            prefs[PreferenceKeys.SAVE_READ_HISTORY] = preferences.saveReadHistory
            prefs[PreferenceKeys.CACHE_OFFLINE] = preferences.cacheFeedsOffline
            prefs[PreferenceKeys.ENABLE_NOTIFICATIONS] = preferences.enableNotifications
            prefs[PreferenceKeys.STATELESS_MODE] = false
        }
    }
    
    override fun getNotificationPreferences(): Flow<NotificationPreferences> {
        return context.dataStore.data.map { prefs ->
            NotificationPreferences(
                criticalAlertsOnly = prefs[PreferenceKeys.CRITICAL_ALERTS_ONLY] ?: false,
                allNews = prefs[PreferenceKeys.ALL_NEWS] ?: false,
                hackathonAlerts = prefs[PreferenceKeys.HACKATHON_ALERTS] ?: true,
                dailyTips = prefs[PreferenceKeys.DAILY_TIPS] ?: true,
                breachAlerts = prefs[PreferenceKeys.BREACH_ALERTS] ?: true,
                cveAlerts = prefs[PreferenceKeys.CVE_ALERTS] ?: false
            )
        }
    }
    
    override suspend fun updateNotificationPreferences(preferences: NotificationPreferences) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.CRITICAL_ALERTS_ONLY] = preferences.criticalAlertsOnly
            prefs[PreferenceKeys.ALL_NEWS] = preferences.allNews
            prefs[PreferenceKeys.HACKATHON_ALERTS] = preferences.hackathonAlerts
            prefs[PreferenceKeys.DAILY_TIPS] = preferences.dailyTips
            prefs[PreferenceKeys.BREACH_ALERTS] = preferences.breachAlerts
            prefs[PreferenceKeys.CVE_ALERTS] = preferences.cveAlerts
        }
    }
    
    override fun isStatelessMode(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[PreferenceKeys.STATELESS_MODE] ?: false
        }
    }
    
    override suspend fun enableStatelessMode() {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.STATELESS_MODE] = true
            prefs[PreferenceKeys.STORE_LOGIN] = false
            prefs[PreferenceKeys.ALLOW_ANALYTICS] = false
            prefs[PreferenceKeys.PERSONALIZE_FEEDS] = false
            prefs[PreferenceKeys.SAVE_READ_HISTORY] = false
        }
    }
}
