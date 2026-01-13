package com.cyberpulse.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cyberpulse.domain.model.CyberTag
import com.cyberpulse.domain.model.NewsArticle
import com.cyberpulse.domain.model.NewsCategory
import com.cyberpulse.domain.model.NewsSource
import com.cyberpulse.domain.model.SourceReliability
import com.cyberpulse.ui.theme.*
import java.time.Duration
import java.time.Instant

/**
 * CyberPulse News Card Component
 * 
 * A premium glassmorphism-styled news card with:
 * - Smooth animations and micro-interactions
 * - Smart tag display
 * - Source attribution with icons
 * - Time-since-posted display
 * - Save and share actions
 */

@Composable
fun NewsCard(
    article: NewsArticle,
    onArticleClick: (NewsArticle) -> Unit,
    onSaveClick: (NewsArticle) -> Unit,
    onShareClick: (NewsArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Smooth scale animation on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "cardScale"
    )
    
    // Glow color animation based on category
    val glowColor by animateColorAsState(
        targetValue = getCategoryGlowColor(article.category),
        animationSpec = tween(durationMillis = 300),
        label = "glowColor"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onArticleClick(article) },
        shape = NewsCardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // Glassmorphism container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkCard.copy(alpha = 0.7f),
                            DarkCard.copy(alpha = 0.9f)
                        )
                    ),
                    shape = NewsCardShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassBorder,
                            GlassBorder.copy(alpha = 0.1f)
                        )
                    ),
                    shape = NewsCardShape
                )
        ) {
            // Subtle glow effect at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                glowColor.copy(alpha = 0.1f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Thumbnail Image
                if (!article.imageUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(GlassCardShape)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(article.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = article.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // Gradient overlay for better text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            DarkBackground.copy(alpha = 0.7f)
                                        ),
                                        startY = 100f
                                    )
                                )
                        )
                        
                        // Category badge on image
                        CategoryBadge(
                            category = article.category,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Source & Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Source info
                    SourceChip(source = article.source)
                    
                    // Time since posted
                    Text(
                        text = formatTimeSince(article.publishedAt),
                        style = MetadataStyle,
                        color = TextTertiary
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Title
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Summary
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Tags Row
                if (article.tags.isNotEmpty()) {
                    TagsRow(tags = article.tags)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Save button
                    IconButton(onClick = { onSaveClick(article) }) {
                        Icon(
                            imageVector = if (article.isSaved) 
                                Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (article.isSaved) "Unsave" else "Save",
                            tint = if (article.isSaved) CyberCyan else TextSecondary
                        )
                    }
                    
                    // Share button
                    IconButton(onClick = { onShareClick(article) }) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// SUPPORTING COMPONENTS
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SourceChip(
    source: NewsSource,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = GlassWhite,
                shape = CircleShape
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Source icon
        if (!source.iconUrl.isNullOrEmpty()) {
            AsyncImage(
                model = source.iconUrl,
                contentDescription = source.name,
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
            )
        } else {
            // Fallback icon - first letter
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(CyberCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = source.name.first().uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkBackground
                )
            }
        }
        
        Text(
            text = source.name,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        
        // Verified badge
        if (source.reliability == SourceReliability.VERIFIED || 
            source.reliability == SourceReliability.OFFICIAL) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(CyberGreen, CircleShape)
            )
        }
    }
}

@Composable
private fun CategoryBadge(
    category: NewsCategory,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = getCategoryColors(category)
    
    Surface(
        modifier = modifier,
        shape = TagShape,
        color = backgroundColor.copy(alpha = 0.9f)
    ) {
        Text(
            text = category.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = TagStyle,
            color = textColor
        )
    }
}

@Composable
fun TagsRow(
    tags: List<CyberTag>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.take(3).forEach { tag ->
            SmartTag(tag = tag)
        }
        
        if (tags.size > 3) {
            Text(
                text = "+${tags.size - 3}",
                style = TagStyle,
                color = TextTertiary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun SmartTag(
    tag: CyberTag,
    modifier: Modifier = Modifier
) {
    val tagColor = getTagColor(tag.name)
    
    Surface(
        modifier = modifier,
        shape = TagShape,
        color = tagColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = tagColor.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = tag.hashtag,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = TagStyle,
            color = tagColor
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// HELPER FUNCTIONS
// ═══════════════════════════════════════════════════════════════════

private fun getCategoryGlowColor(category: NewsCategory): Color {
    return when (category) {
        NewsCategory.BREACH -> CyberRed
        NewsCategory.VULNERABILITY -> CyberOrange
        NewsCategory.MALWARE, NewsCategory.RANSOMWARE -> CyberRed
        NewsCategory.APT -> CyberPurple
        NewsCategory.TOOLS, NewsCategory.RESEARCH -> CyberGreen
        else -> CyberCyan
    }
}

private fun getCategoryColors(category: NewsCategory): Pair<Color, Color> {
    return when (category) {
        NewsCategory.BREACH -> CyberRed to TextPrimary
        NewsCategory.VULNERABILITY -> CyberOrange to DarkBackground
        NewsCategory.MALWARE -> CyberRed to TextPrimary
        NewsCategory.RANSOMWARE -> CyberRed to TextPrimary
        NewsCategory.APT -> CyberPurple to TextPrimary
        NewsCategory.PRIVACY -> CyberCyan to DarkBackground
        NewsCategory.REGULATORY -> CyberYellow to DarkBackground
        NewsCategory.TOOLS -> CyberGreen to DarkBackground
        NewsCategory.RESEARCH -> CyberGreen to DarkBackground
        NewsCategory.GENERAL -> DarkCardElevated to TextPrimary
    }
}

private fun formatTimeSince(instant: Instant): String {
    val now = Instant.now()
    val duration = Duration.between(instant, now)
    
    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
        duration.toHours() < 24 -> "${duration.toHours()}h ago"
        duration.toDays() < 7 -> "${duration.toDays()}d ago"
        duration.toDays() < 30 -> "${duration.toDays() / 7}w ago"
        else -> "${duration.toDays() / 30}mo ago"
    }
}

// ═══════════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFF0A0E17)
@Composable
private fun NewsCardPreview() {
    CyberPulseTheme {
        NewsCard(
            article = NewsArticle(
                id = "1",
                title = "Critical Zero-Day Vulnerability Discovered in Major Enterprise Software",
                summary = "Security researchers have discovered a critical zero-day vulnerability affecting millions of enterprise users worldwide.",
                content = null,
                url = "https://example.com",
                imageUrl = "https://example.com/image.jpg",
                source = NewsSource(
                    id = "thn",
                    name = "The Hacker News",
                    iconUrl = null,
                    website = "https://thehackernews.com",
                    reliability = SourceReliability.VERIFIED
                ),
                author = "Security Researcher",
                publishedAt = Instant.now().minusSeconds(3600),
                tags = listOf(CyberTag.ZERO_DAY, CyberTag.CRITICAL, CyberTag.CVE),
                category = NewsCategory.VULNERABILITY,
                isSaved = false,
                isRead = false
            ),
            onArticleClick = {},
            onSaveClick = {},
            onShareClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
