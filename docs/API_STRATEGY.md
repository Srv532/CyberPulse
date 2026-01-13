# 🔌 API Strategy Document

## Overview

CyberPulse aggregates cybersecurity news, breach data, CVE information, and events from multiple sources. This document outlines the recommended APIs and data sources.

---

## 📰 News Aggregation

### Primary Options

#### 1. **NewsAPI.org** (Recommended for MVP)
- **URL**: https://newsapi.org
- **Cost**: Free tier: 100 requests/day, Paid: $449/month
- **Pros**: Easy integration, good documentation, filters by topic
- **Cons**: 100 req/day limit on free tier, may need custom filtering for cybersecurity
- **Query Example**:
  ```
  GET /v2/everything?q=cybersecurity OR ransomware OR data breach&language=en
  ```

#### 2. **Feedly API** (Premium)
- **URL**: https://developer.feedly.com
- **Cost**: Enterprise pricing
- **Pros**: Powerful RSS aggregation, AI-powered topic detection, excellent for security news
- **Cons**: Expensive, requires enterprise agreement
- **Use Case**: Ideal for tracking specific security blogs and feeds

#### 3. **RSS Feed Aggregation** (Free, Self-Managed)
Best approach for a privacy-focused app - no third-party API dependency.

**Recommended Security RSS Feeds**:
```kotlin
val SECURITY_RSS_FEEDS = mapOf(
    "The Hacker News" to "https://feeds.feedburner.com/TheHackersNews",
    "BleepingComputer" to "https://www.bleepingcomputer.com/feed/",
    "Krebs on Security" to "https://krebsonsecurity.com/feed/",
    "Dark Reading" to "https://www.darkreading.com/rss.xml",
    "SecurityWeek" to "https://feeds.feedburner.com/securityweek",
    "Threatpost" to "https://threatpost.com/feed/",
    "CISA Alerts" to "https://www.cisa.gov/uscert/ncas/alerts.xml",
    "Naked Security" to "https://nakedsecurity.sophos.com/feed/",
    "Graham Cluley" to "https://grahamcluley.com/feed/",
    "Schneier on Security" to "https://www.schneier.com/feed/",
    "Troy Hunt" to "https://www.troyhunt.com/rss/",
    "Google Security Blog" to "https://security.googleblog.com/feeds/posts/default",
    "Microsoft Security" to "https://www.microsoft.com/security/blog/feed/"
)
```

**Implementation**: Use a Kotlin RSS parser library like `Rome` or `kotlin-xml-parser`.

---

## 🔴 Breach Data

### HaveIBeenPwned (HIBP) API
- **URL**: https://haveibeenpwned.com/API/v3
- **Cost**: $3.50/month for API key (required since 2019)
- **Documentation**: https://haveibeenpwned.com/API/v3
- **Rate Limit**: 1 request per 1500ms per IP

**Key Endpoints**:
```
GET /breachedaccount/{email}     - Check if email is pwned
GET /breaches                     - Get all known breaches
GET /breach/{name}               - Get breach details
GET /pasteaccount/{email}        - Get pastes containing email
```

**Headers Required**:
```
hibp-api-key: {YOUR_API_KEY}
User-Agent: CyberPulse-Android
```

---

## 🐛 CVE Data

### NIST National Vulnerability Database (NVD) API 2.0
- **URL**: https://services.nvd.nist.gov/rest/json/cves/2.0
- **Cost**: FREE (but rate limited)
- **Documentation**: https://nvd.nist.gov/developers/vulnerabilities
- **Rate Limit**: 5 requests per 30 seconds (public), 50 req/30s with API key

**Key Parameters**:
```
keywordSearch     - Search by keyword
cvssV3Severity    - Filter by severity (LOW, MEDIUM, HIGH, CRITICAL)
cveId             - Get specific CVE
pubStartDate      - Filter by publish date
resultsPerPage    - Pagination (max 2000)
```

**Example Request**:
```
GET /cves/2.0?keywordSearch=wordpress&cvssV3Severity=CRITICAL&resultsPerPage=20
```

**Alternative**: CVE.org API (simpler but less data)
- **URL**: https://cveawg.mitre.org/api/cve/
- **Cost**: FREE

---

## 🏆 Events (CTFs, Hackathons)

### CTFtime API
- **URL**: https://ctftime.org/api/v1/
- **Cost**: FREE
- **Documentation**: https://ctftime.org/api/

**Key Endpoints**:
```
GET /events/                     - Get all events
GET /events/?limit=100&start={timestamp}&finish={timestamp}
```

### Devpost (Hackathons)
- No official API, would require scraping or partnership

### Manual Curation
For webinars and conferences, consider a Firebase-backed manually curated list updated weekly.

---

## 🎓 Learning Resources

### Udemy Affiliate API
- **URL**: https://www.udemy.com/developers/affiliate/
- **Cost**: FREE (affiliate program)
- **Use Case**: Get course listings, can earn affiliate revenue

### Coursera Catalog API
- **URL**: https://building.coursera.org/app-platform/catalog/
- **Cost**: FREE for catalog access
- **Use Case**: Security-related courses

### Manual Curation
Certifications like SANS, OSCP, CompTIA don't have public APIs. 
Recommend a Firebase-backed curated database with manual updates.

---

## 📱 Architecture Recommendation

### Hybrid Approach

```
┌────────────────────────────────────────────────────────────────┐
│                        BACKEND SERVICE                          │
│                    (Firebase Cloud Functions)                   │
│                                                                 │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐               │
│  │ RSS Parser │  │ HIBP Proxy │  │ NVD Sync   │               │
│  │ (Cron: 1h) │  │            │  │ (Cron: 6h) │               │
│  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘               │
│        │               │               │                       │
│        └───────────────┼───────────────┘                       │
│                        ▼                                        │
│              ┌─────────────────┐                               │
│              │ Firebase Firestore                              │
│              │ (Aggregated Data)                               │
│              └────────┬────────┘                               │
└───────────────────────┼────────────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────────────┐
│                      ANDROID APP                                │
│                                                                 │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐               │
│  │ NewsRepo   │  │ BreachRepo │  │ CVERepo    │               │
│  │            │  │ (Direct    │  │            │               │
│  │ Firestore  │  │  HIBP API) │  │ Firestore  │               │
│  └────────────┘  └────────────┘  └────────────┘               │
└────────────────────────────────────────────────────────────────┘
```

### Benefits of This Approach:

1. **Rate Limiting**: Backend handles API rate limits, app never blocked
2. **Caching**: Pre-aggregated data = faster app response
3. **Cost Efficiency**: One backend call vs. thousands of client calls
4. **Privacy**: User's API calls don't expose their IP to third parties
5. **Consistency**: All users see the same data
6. **Offline**: Firestore provides built-in offline caching

---

## 💰 Cost Estimate (Monthly)

| Service | Free Tier | Paid Estimate |
|---------|-----------|---------------|
| Firebase (Spark) | FREE | - |
| Firebase (Blaze) | Pay-as-you-go | ~$25/month at scale |
| HIBP API | - | $3.50/month |
| NewsAPI.org | 100 req/day | $449/month (if needed) |
| NVD API | FREE | FREE (with key) |
| CTFtime | FREE | FREE |
| **TOTAL (MVP)** | **FREE** | **~$30/month** |

---

## 🔧 Implementation Priority

### Phase 1 (MVP)
- [x] RSS Feed Aggregation (free, no API keys)
- [x] NVD CVE API (free)
- [x] CTFtime API (free)
- [ ] Firebase backend for data aggregation

### Phase 2 (Launch)
- [ ] HIBP API integration ($3.50/mo)
- [ ] Push notifications (FCM - free)
- [ ] Full offline caching

### Phase 3 (Growth)
- [ ] NewsAPI.org for broader coverage
- [ ] Learning platform partnerships
- [ ] Community-contributed tips

---

## 📜 API Keys Management

Store in `local.properties` (not in version control):

```properties
# local.properties
NEWS_API_KEY=your_newsapi_key_here
HIBP_API_KEY=your_hibp_key_here
NVD_API_KEY=your_nvd_key_here
```

Access in `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        buildConfigField("String", "NEWS_API_KEY", 
            "\"${project.findProperty("NEWS_API_KEY") ?: ""}\"")
        buildConfigField("String", "HIBP_API_KEY",
            "\"${project.findProperty("HIBP_API_KEY") ?: ""}\"")
    }
}
```

---

**Document Version**: 1.0  
**Last Updated**: January 2026
