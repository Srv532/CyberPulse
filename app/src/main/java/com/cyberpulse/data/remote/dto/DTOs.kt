package com.cyberpulse.data.remote.dto

import com.cyberpulse.domain.model.*
import com.google.gson.annotations.SerializedName
import java.time.Instant

/**
 * Data Transfer Objects (DTOs)
 * 
 * Models for API responses with mapping to domain models.
 */

// ═══════════════════════════════════════════════════════════════════
// NEWS DTOs
// ═══════════════════════════════════════════════════════════════════

data class NewsResponse(
    @SerializedName("articles")
    val articles: List<ArticleDto>,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("page")
    val page: Int
)

data class ArticleDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("content")
    val content: String?,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlToImage")
    val imageUrl: String?,
    @SerializedName("source")
    val source: SourceDto,
    @SerializedName("author")
    val author: String?,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("tags")
    val tags: List<String>?,
    @SerializedName("category")
    val category: String?
)

data class SourceDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("icon")
    val iconUrl: String?,
    @SerializedName("url")
    val website: String?
)

fun ArticleDto.toDomain(): NewsArticle {
    return NewsArticle(
        id = id,
        title = title,
        summary = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        source = NewsSource(
            id = source.id ?: source.name.lowercase().replace(" ", "_"),
            name = source.name,
            iconUrl = source.iconUrl,
            website = source.website ?: "",
            reliability = determineSourceReliability(source.name)
        ),
        author = author,
        publishedAt = parseDateTime(publishedAt),
        tags = parseTags(tags),
        category = parseCategory(category),
        isSaved = false,
        isRead = false
    )
}

// ═══════════════════════════════════════════════════════════════════
// BREACH DTOs (HaveIBeenPwned)
// ═══════════════════════════════════════════════════════════════════

data class BreachDto(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Title")
    val title: String?,
    @SerializedName("Domain")
    val domain: String?,
    @SerializedName("BreachDate")
    val breachDate: String,
    @SerializedName("AddedDate")
    val addedDate: String,
    @SerializedName("ModifiedDate")
    val modifiedDate: String?,
    @SerializedName("PwnCount")
    val pwnCount: Long,
    @SerializedName("Description")
    val description: String,
    @SerializedName("DataClasses")
    val dataClasses: List<String>,
    @SerializedName("IsVerified")
    val isVerified: Boolean,
    @SerializedName("IsFabricated")
    val isFabricated: Boolean,
    @SerializedName("IsSensitive")
    val isSensitive: Boolean,
    @SerializedName("IsRetired")
    val isRetired: Boolean,
    @SerializedName("IsSpamList")
    val isSpamList: Boolean,
    @SerializedName("LogoPath")
    val logoPath: String?
)

data class PasteDto(
    @SerializedName("Id")
    val id: String,
    @SerializedName("Source")
    val source: String,
    @SerializedName("Title")
    val title: String?,
    @SerializedName("Date")
    val date: String?,
    @SerializedName("EmailCount")
    val emailCount: Int
)

fun BreachDto.toDomain(): DataBreach {
    return DataBreach(
        id = name,
        name = title ?: name,
        domain = domain,
        breachDate = parseDate(breachDate),
        addedDate = parseDate(addedDate),
        modifiedDate = modifiedDate?.let { parseDate(it) },
        pwnCount = pwnCount,
        description = description,
        dataClasses = dataClasses,
        isVerified = isVerified,
        isFabricated = isFabricated,
        isSensitive = isSensitive,
        isRetired = isRetired,
        isSpamList = isSpamList,
        logoPath = logoPath
    )
}

fun PasteDto.toDomain(): Paste {
    return Paste(
        id = id,
        source = source,
        title = title,
        date = date?.let { parseDate(it) },
        emailCount = emailCount
    )
}

// ═══════════════════════════════════════════════════════════════════
// CVE DTOs (NIST NVD)
// ═══════════════════════════════════════════════════════════════════

data class CVEResponse(
    @SerializedName("resultsPerPage")
    val resultsPerPage: Int,
    @SerializedName("startIndex")
    val startIndex: Int,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("vulnerabilities")
    val vulnerabilities: List<CVEItemDto>
)

data class CVEItemDto(
    @SerializedName("cve")
    val cve: CVEDataDto
)

data class CVEDataDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("descriptions")
    val descriptions: List<DescriptionDto>,
    @SerializedName("published")
    val published: String,
    @SerializedName("lastModified")
    val lastModified: String,
    @SerializedName("metrics")
    val metrics: MetricsDto?,
    @SerializedName("references")
    val references: List<ReferenceDto>?,
    @SerializedName("configurations")
    val configurations: List<ConfigurationDto>?
)

data class DescriptionDto(
    @SerializedName("lang")
    val lang: String,
    @SerializedName("value")
    val value: String
)

data class MetricsDto(
    @SerializedName("cvssMetricV31")
    val cvssV31: List<CvssDto>?
)

data class CvssDto(
    @SerializedName("cvssData")
    val cvssData: CvssDataDto
)

data class CvssDataDto(
    @SerializedName("baseScore")
    val baseScore: Double,
    @SerializedName("baseSeverity")
    val baseSeverity: String,
    @SerializedName("attackVector")
    val attackVector: String?
)

data class ReferenceDto(
    @SerializedName("url")
    val url: String
)

data class ConfigurationDto(
    @SerializedName("nodes")
    val nodes: List<NodeDto>?
)

data class NodeDto(
    @SerializedName("cpeMatch")
    val cpeMatch: List<CpeMatchDto>?
)

data class CpeMatchDto(
    @SerializedName("criteria")
    val criteria: String
)

fun CVEItemDto.toDomain(): CVEEntry {
    val englishDesc = cve.descriptions.find { it.lang == "en" }?.value ?: ""
    val cvss = cve.metrics?.cvssV31?.firstOrNull()?.cvssData
    val score = cvss?.baseScore
    
    return CVEEntry(
        id = cve.id,
        description = englishDesc,
        publishedDate = parseDateTime(cve.published),
        lastModifiedDate = parseDateTime(cve.lastModified),
        cvssScore = score,
        severity = determineSeverity(score),
        attackVector = cvss?.attackVector,
        affectedProducts = extractProducts(cve.configurations),
        references = cve.references?.map { it.url } ?: emptyList(),
        exploitAvailable = false, // Would need additional API call
        patchAvailable = false // Would need additional API call
    )
}

// ═══════════════════════════════════════════════════════════════════
// EVENT DTOs
// ═══════════════════════════════════════════════════════════════════

data class EventsResponse(
    @SerializedName("events")
    val events: List<EventDto>
)

data class EventDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("image")
    val imageUrl: String?,
    @SerializedName("organizer")
    val organizer: String,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String?,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("isOnline")
    val isOnline: Boolean,
    @SerializedName("location")
    val location: String?,
    @SerializedName("prizes")
    val prizes: String?,
    @SerializedName("registrationUrl")
    val registrationUrl: String?,
    @SerializedName("registrationDeadline")
    val registrationDeadline: String?
)

data class CTFEventDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("organizers")
    val organizers: List<OrganizerDto>,
    @SerializedName("start")
    val start: String,
    @SerializedName("finish")
    val finish: String,
    @SerializedName("onsite")
    val onsite: Boolean,
    @SerializedName("location")
    val location: String?,
    @SerializedName("weight")
    val weight: Double?
)

data class OrganizerDto(
    @SerializedName("name")
    val name: String
)

fun CTFEventDto.toDomain(): CyberEvent {
    return CyberEvent(
        id = id.toString(),
        name = title,
        type = EventType.CTF,
        description = description,
        url = url,
        imageUrl = logo,
        organizer = organizers.firstOrNull()?.name ?: "Unknown",
        startDate = parseDateTime(start),
        endDate = parseDateTime(finish),
        timezone = "UTC",
        isOnline = !onsite,
        location = location,
        prizes = null,
        registrationUrl = url,
        registrationDeadline = null,
        isRegistered = false,
        hasReminder = false
    )
}

// ═══════════════════════════════════════════════════════════════════
// DAILY TIP DTO
// ═══════════════════════════════════════════════════════════════════

data class DailyTipDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("date")
    val date: String
)

fun DailyTipDto.toDomain(): DailyTip {
    return DailyTip(
        id = id,
        title = title,
        content = content,
        category = TipCategory.values().find { 
            it.name.equals(category, ignoreCase = true) 
        } ?: TipCategory.GENERAL,
        date = parseDate(date),
        isDismissed = false
    )
}

// ═══════════════════════════════════════════════════════════════════
// HELPER FUNCTIONS
// ═══════════════════════════════════════════════════════════════════

private fun parseDateTime(dateString: String): Instant {
    return try {
        Instant.parse(dateString)
    } catch (e: Exception) {
        Instant.now()
    }
}

private fun parseDate(dateString: String): Instant {
    return try {
        // Handle date-only strings by adding time
        if (!dateString.contains("T")) {
            Instant.parse("${dateString}T00:00:00Z")
        } else {
            Instant.parse(dateString)
        }
    } catch (e: Exception) {
        Instant.now()
    }
}

private fun parseTags(tags: List<String>?): List<CyberTag> {
    return tags?.mapNotNull { tag ->
        CyberTag.values().find { 
            it.name.equals(tag.replace(" ", "_"), ignoreCase = true) ||
            it.displayName.equals(tag, ignoreCase = true)
        }
    } ?: emptyList()
}

private fun parseCategory(category: String?): NewsCategory {
    return category?.let { cat ->
        NewsCategory.values().find { 
            it.name.equals(cat.replace(" ", "_"), ignoreCase = true) 
        }
    } ?: NewsCategory.GENERAL
}

private fun determineSourceReliability(sourceName: String): SourceReliability {
    val verifiedSources = listOf(
        "The Hacker News", "BleepingComputer", "Krebs on Security",
        "Dark Reading", "SecurityWeek", "Threatpost", "CISA",
        "Microsoft Security", "Google Security Blog"
    )
    val officialSources = listOf(
        "NIST", "CISA", "FBI", "NSA", "CERT"
    )
    
    return when {
        officialSources.any { sourceName.contains(it, ignoreCase = true) } -> 
            SourceReliability.OFFICIAL
        verifiedSources.any { sourceName.contains(it, ignoreCase = true) } -> 
            SourceReliability.VERIFIED
        else -> SourceReliability.COMMUNITY
    }
}

private fun determineSeverity(score: Double?): CVESeverity {
    return when {
        score == null -> CVESeverity.NONE
        score >= 9.0 -> CVESeverity.CRITICAL
        score >= 7.0 -> CVESeverity.HIGH
        score >= 4.0 -> CVESeverity.MEDIUM
        score >= 0.1 -> CVESeverity.LOW
        else -> CVESeverity.NONE
    }
}

private fun extractProducts(configurations: List<ConfigurationDto>?): List<String> {
    return configurations?.flatMap { config ->
        config.nodes?.flatMap { node ->
            node.cpeMatch?.map { cpe ->
                // Extract product name from CPE string
                val parts = cpe.criteria.split(":")
                if (parts.size >= 5) "${parts[3]} ${parts[4]}" else cpe.criteria
            } ?: emptyList()
        } ?: emptyList()
    }?.distinct()?.take(10) ?: emptyList()
}
