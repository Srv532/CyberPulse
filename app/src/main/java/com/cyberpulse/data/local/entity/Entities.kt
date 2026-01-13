package com.cyberpulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.cyberpulse.domain.model.*
import java.time.Instant

/**
 * Room Entities and Type Converters
 * 
 * Database models for local caching.
 */

// ═══════════════════════════════════════════════════════════════════
// NEWS ARTICLE ENTITY
// ═══════════════════════════════════════════════════════════════════

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val summary: String,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val sourceName: String,
    val sourceIconUrl: String?,
    val sourceWebsite: String,
    val sourceReliability: String,
    val author: String?,
    val publishedAt: Long, // Epoch millis
    val tags: String, // Comma-separated
    val category: String,
    val isSaved: Boolean = false,
    val isRead: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)

fun NewsArticleEntity.toDomain(): NewsArticle {
    return NewsArticle(
        id = id,
        title = title,
        summary = summary,
        content = content,
        url = url,
        imageUrl = imageUrl,
        source = NewsSource(
            id = sourceName.lowercase().replace(" ", "_"),
            name = sourceName,
            iconUrl = sourceIconUrl,
            website = sourceWebsite,
            reliability = SourceReliability.valueOf(sourceReliability)
        ),
        author = author,
        publishedAt = Instant.ofEpochMilli(publishedAt),
        tags = tags.split(",").mapNotNull { tag ->
            CyberTag.values().find { it.name.equals(tag.trim(), ignoreCase = true) }
        },
        category = NewsCategory.valueOf(category),
        isSaved = isSaved,
        isRead = isRead
    )
}

fun NewsArticle.toEntity(): NewsArticleEntity {
    return NewsArticleEntity(
        id = id,
        title = title,
        summary = summary,
        content = content,
        url = url,
        imageUrl = imageUrl,
        sourceName = source.name,
        sourceIconUrl = source.iconUrl,
        sourceWebsite = source.website,
        sourceReliability = source.reliability.name,
        author = author,
        publishedAt = publishedAt.toEpochMilli(),
        tags = tags.joinToString(",") { it.name },
        category = category.name,
        isSaved = isSaved,
        isRead = isRead
    )
}

// ═══════════════════════════════════════════════════════════════════
// DATA BREACH ENTITY
// ═══════════════════════════════════════════════════════════════════

@Entity(tableName = "data_breaches")
data class DataBreachEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val domain: String?,
    val breachDate: Long,
    val addedDate: Long,
    val modifiedDate: Long?,
    val pwnCount: Long,
    val description: String,
    val dataClasses: String, // Comma-separated
    val isVerified: Boolean,
    val isFabricated: Boolean,
    val isSensitive: Boolean,
    val isRetired: Boolean,
    val isSpamList: Boolean,
    val logoPath: String?
)

fun DataBreachEntity.toDomain(): DataBreach {
    return DataBreach(
        id = id,
        name = name,
        domain = domain,
        breachDate = Instant.ofEpochMilli(breachDate),
        addedDate = Instant.ofEpochMilli(addedDate),
        modifiedDate = modifiedDate?.let { Instant.ofEpochMilli(it) },
        pwnCount = pwnCount,
        description = description,
        dataClasses = dataClasses.split(",").map { it.trim() },
        isVerified = isVerified,
        isFabricated = isFabricated,
        isSensitive = isSensitive,
        isRetired = isRetired,
        isSpamList = isSpamList,
        logoPath = logoPath
    )
}

fun DataBreach.toEntity(): DataBreachEntity {
    return DataBreachEntity(
        id = id,
        name = name,
        domain = domain,
        breachDate = breachDate.toEpochMilli(),
        addedDate = addedDate.toEpochMilli(),
        modifiedDate = modifiedDate?.toEpochMilli(),
        pwnCount = pwnCount,
        description = description,
        dataClasses = dataClasses.joinToString(","),
        isVerified = isVerified,
        isFabricated = isFabricated,
        isSensitive = isSensitive,
        isRetired = isRetired,
        isSpamList = isSpamList,
        logoPath = logoPath
    )
}

// ═══════════════════════════════════════════════════════════════════
// CVE ENTITY
// ═══════════════════════════════════════════════════════════════════

@Entity(tableName = "cve_entries")
data class CVEEntity(
    @PrimaryKey
    val id: String,
    val description: String,
    val publishedDate: Long,
    val lastModifiedDate: Long,
    val cvssScore: Double?,
    val severity: String,
    val attackVector: String?,
    val affectedProducts: String, // Comma-separated
    val references: String, // Comma-separated
    val exploitAvailable: Boolean,
    val patchAvailable: Boolean
)

fun CVEEntity.toDomain(): CVEEntry {
    return CVEEntry(
        id = id,
        description = description,
        publishedDate = Instant.ofEpochMilli(publishedDate),
        lastModifiedDate = Instant.ofEpochMilli(lastModifiedDate),
        cvssScore = cvssScore,
        severity = CVESeverity.valueOf(severity),
        attackVector = attackVector,
        affectedProducts = affectedProducts.split(",").map { it.trim() },
        references = references.split(",").map { it.trim() },
        exploitAvailable = exploitAvailable,
        patchAvailable = patchAvailable
    )
}

fun CVEEntry.toEntity(): CVEEntity {
    return CVEEntity(
        id = id,
        description = description,
        publishedDate = publishedDate.toEpochMilli(),
        lastModifiedDate = lastModifiedDate.toEpochMilli(),
        cvssScore = cvssScore,
        severity = severity.name,
        attackVector = attackVector,
        affectedProducts = affectedProducts.joinToString(","),
        references = references.joinToString(","),
        exploitAvailable = exploitAvailable,
        patchAvailable = patchAvailable
    )
}

// ═══════════════════════════════════════════════════════════════════
// CYBER EVENT ENTITY
// ═══════════════════════════════════════════════════════════════════

@Entity(tableName = "cyber_events")
data class CyberEventEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val organizer: String,
    val startDate: Long,
    val endDate: Long?,
    val timezone: String,
    val isOnline: Boolean,
    val location: String?,
    val prizes: String?,
    val registrationUrl: String?,
    val registrationDeadline: Long?,
    val isRegistered: Boolean = false,
    val hasReminder: Boolean = false
)

fun CyberEventEntity.toDomain(): CyberEvent {
    return CyberEvent(
        id = id,
        name = name,
        type = EventType.valueOf(type),
        description = description,
        url = url,
        imageUrl = imageUrl,
        organizer = organizer,
        startDate = Instant.ofEpochMilli(startDate),
        endDate = endDate?.let { Instant.ofEpochMilli(it) },
        timezone = timezone,
        isOnline = isOnline,
        location = location,
        prizes = prizes,
        registrationUrl = registrationUrl,
        registrationDeadline = registrationDeadline?.let { Instant.ofEpochMilli(it) },
        isRegistered = isRegistered,
        hasReminder = hasReminder
    )
}

fun CyberEvent.toEntity(): CyberEventEntity {
    return CyberEventEntity(
        id = id,
        name = name,
        type = type.name,
        description = description,
        url = url,
        imageUrl = imageUrl,
        organizer = organizer,
        startDate = startDate.toEpochMilli(),
        endDate = endDate?.toEpochMilli(),
        timezone = timezone,
        isOnline = isOnline,
        location = location,
        prizes = prizes,
        registrationUrl = registrationUrl,
        registrationDeadline = registrationDeadline?.toEpochMilli(),
        isRegistered = isRegistered,
        hasReminder = hasReminder
    )
}

// ═══════════════════════════════════════════════════════════════════
// DAILY TIP ENTITY
// ═══════════════════════════════════════════════════════════════════

@Entity(tableName = "daily_tips")
data class DailyTipEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val date: Long,
    val isDismissed: Boolean = false
)

fun DailyTipEntity.toDomain(): DailyTip {
    return DailyTip(
        id = id,
        title = title,
        content = content,
        category = TipCategory.valueOf(category),
        date = Instant.ofEpochMilli(date),
        isDismissed = isDismissed
    )
}

fun DailyTip.toEntity(): DailyTipEntity {
    return DailyTipEntity(
        id = id,
        title = title,
        content = content,
        category = category.name,
        date = date.toEpochMilli(),
        isDismissed = isDismissed
    )
}

// ═══════════════════════════════════════════════════════════════════
// TYPE CONVERTERS
// ═══════════════════════════════════════════════════════════════════

class Converters {
    
    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        return value?.toEpochMilli()
    }
    
    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }
}
