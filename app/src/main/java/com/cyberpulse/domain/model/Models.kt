package com.cyberpulse.domain.model

import java.time.Instant

/**
 * Core Domain Models for CyberPulse
 * 
 * Clean architecture domain layer - these are pure Kotlin classes
 * with no framework dependencies.
 */

// ═══════════════════════════════════════════════════════════════════
// NEWS & ARTICLES
// ═══════════════════════════════════════════════════════════════════

/**
 * Represents a cyber security news article.
 */
data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val source: NewsSource,
    val author: String?,
    val publishedAt: Instant,
    val tags: List<CyberTag>,
    val category: NewsCategory,
    val isSaved: Boolean = false,
    val isRead: Boolean = false
)

/**
 * News source information.
 */
data class NewsSource(
    val id: String,
    val name: String,
    val iconUrl: String?,
    val website: String,
    val reliability: SourceReliability = SourceReliability.VERIFIED
)

enum class SourceReliability {
    OFFICIAL,       // Official vendor/government sources
    VERIFIED,       // Well-known security news outlets
    COMMUNITY,      // Community/blog sources
    UNVERIFIED      // Unknown reliability
}

enum class NewsCategory {
    GENERAL,
    BREACH,
    VULNERABILITY,
    MALWARE,
    RANSOMWARE,
    APT,            // Advanced Persistent Threats
    PRIVACY,
    REGULATORY,
    TOOLS,
    RESEARCH
}

/**
 * Smart tags for categorizing news.
 */
enum class CyberTag(val displayName: String, val hashtag: String) {
    RANSOMWARE("Ransomware", "#Ransomware"),
    ZERO_DAY("Zero-Day", "#ZeroDay"),
    DATA_BREACH("Data Breach", "#DataBreach"),
    PATCH_TUESDAY("Patch Tuesday", "#PatchTuesday"),
    CVE("CVE", "#CVE"),
    PHISHING("Phishing", "#Phishing"),
    APT("APT", "#APT"),
    MALWARE("Malware", "#Malware"),
    SUPPLY_CHAIN("Supply Chain", "#SupplyChain"),
    CRITICAL("Critical", "#Critical")
}

// ═══════════════════════════════════════════════════════════════════
// BREACH DATA
// ═══════════════════════════════════════════════════════════════════

/**
 * Data breach information for Breach Radar.
 */
data class DataBreach(
    val id: String,
    val name: String,
    val domain: String?,
    val breachDate: Instant,
    val addedDate: Instant,
    val modifiedDate: Instant?,
    val pwnCount: Long,
    val description: String,
    val dataClasses: List<String>,    // email, password, phone, etc.
    val isVerified: Boolean,
    val isFabricated: Boolean,
    val isSensitive: Boolean,
    val isRetired: Boolean,
    val isSpamList: Boolean,
    val logoPath: String?
)

/**
 * Result of "Am I Pwned?" check.
 */
data class PwnedCheckResult(
    val email: String,
    val isPwned: Boolean,
    val breaches: List<DataBreach>,
    val pastes: List<Paste>?,
    val checkedAt: Instant
)

data class Paste(
    val id: String,
    val source: String,
    val title: String?,
    val date: Instant?,
    val emailCount: Int
)

// ═══════════════════════════════════════════════════════════════════
// CVE DATA
// ═══════════════════════════════════════════════════════════════════

/**
 * Common Vulnerability and Exposure entry.
 */
data class CVEEntry(
    val id: String,                     // e.g., CVE-2024-1234
    val description: String,
    val publishedDate: Instant,
    val lastModifiedDate: Instant,
    val cvssScore: Double?,             // 0.0 - 10.0
    val severity: CVESeverity,
    val attackVector: String?,
    val affectedProducts: List<String>,
    val references: List<String>,
    val exploitAvailable: Boolean,
    val patchAvailable: Boolean
)

enum class CVESeverity(val displayName: String, val minScore: Double, val maxScore: Double) {
    NONE("None", 0.0, 0.0),
    LOW("Low", 0.1, 3.9),
    MEDIUM("Medium", 4.0, 6.9),
    HIGH("High", 7.0, 8.9),
    CRITICAL("Critical", 9.0, 10.0)
}

// ═══════════════════════════════════════════════════════════════════
// LEARNING & EVENTS
// ═══════════════════════════════════════════════════════════════════

/**
 * Cybersecurity course/certification.
 */
data class Course(
    val id: String,
    val title: String,
    val provider: CourseProvider,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val price: CoursePrice,
    val duration: String?,              // e.g., "4 weeks"
    val level: CourseLevel,
    val rating: Double?,
    val enrollmentCount: Int?,
    val topics: List<String>,
    val isCertification: Boolean,
    val certificationName: String?
)

enum class CourseProvider(val displayName: String) {
    UDEMY("Udemy"),
    COURSERA("Coursera"),
    SANS("SANS"),
    OFFENSIVE_SECURITY("Offensive Security"),
    HACK_THE_BOX("Hack The Box"),
    TRY_HACK_ME("TryHackMe"),
    CYBRARY("Cybrary"),
    PLURALSIGHT("Pluralsight"),
    EC_COUNCIL("EC-Council"),
    COMPTIA("CompTIA"),
    OTHER("Other")
}

data class CoursePrice(
    val amount: Double,
    val currency: String,
    val isFree: Boolean
)

enum class CourseLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
}

/**
 * Cybersecurity event (CTF, Hackathon, Webinar).
 */
data class CyberEvent(
    val id: String,
    val name: String,
    val type: EventType,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val organizer: String,
    val startDate: Instant,
    val endDate: Instant?,
    val timezone: String,
    val isOnline: Boolean,
    val location: String?,
    val prizes: String?,
    val registrationUrl: String?,
    val registrationDeadline: Instant?,
    val isRegistered: Boolean = false,
    val hasReminder: Boolean = false
)

enum class EventType(val displayName: String) {
    CTF("Capture The Flag"),
    HACKATHON("Hackathon"),
    WEBINAR("Webinar"),
    CONFERENCE("Conference"),
    WORKSHOP("Workshop"),
    MEETUP("Meetup"),
    COMPETITION("Competition")
}

// ═══════════════════════════════════════════════════════════════════
// USER & PREFERENCES
// ═══════════════════════════════════════════════════════════════════

/**
 * User profile from Google Sign-In.
 */
data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean = false
)

/**
 * Data sovereignty preferences.
 */
data class DataPreferences(
    val storeLoginSession: Boolean = false,
    val allowUsageAnalytics: Boolean = false,
    val personalizeFeeds: Boolean = false,
    val saveReadHistory: Boolean = false,
    val cacheFeedsOffline: Boolean = true,
    val enableNotifications: Boolean = true
)

/**
 * Notification preferences.
 */
data class NotificationPreferences(
    val criticalAlertsOnly: Boolean = false,
    val allNews: Boolean = false,
    val hackathonAlerts: Boolean = true,
    val dailyTips: Boolean = true,
    val breachAlerts: Boolean = true,
    val cveAlerts: Boolean = false
)

// ═══════════════════════════════════════════════════════════════════
// DAILY TIP
// ═══════════════════════════════════════════════════════════════════

/**
 * Daily security tip.
 */
data class DailyTip(
    val id: String,
    val title: String,
    val content: String,
    val category: TipCategory,
    val date: Instant,
    val isDismissed: Boolean = false
)

enum class TipCategory {
    NETWORK,
    PASSWORD,
    PRIVACY,
    MOBILE,
    SOCIAL_ENGINEERING,
    MALWARE,
    GENERAL
}
