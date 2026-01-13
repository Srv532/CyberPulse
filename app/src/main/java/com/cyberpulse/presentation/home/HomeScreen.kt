package com.cyberpulse.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cyberpulse.domain.model.DailyTip
import com.cyberpulse.domain.model.NewsArticle
import com.cyberpulse.domain.model.UserProfile
import com.cyberpulse.presentation.components.NewsCard
import com.cyberpulse.presentation.components.NewsCardShimmer
import com.cyberpulse.presentation.components.UserTerminalDrawer
import com.cyberpulse.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CyberPulse Intelligence Feed
 * 
 * The main hub featuring:
 * - Collapsing top bar with smooth scroll behavior
 * - High-contrast "Cyber Cards" for news
 * - Premium skeleton loaders for seamless loading
 * - Pull-to-refresh with neon styling
 * - User Terminal drawer integration
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToArticle: (String) -> Unit,
    onNavigateToHIBP: () -> Unit,
    onNavigateToCVE: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dailyTip by viewModel.dailyTip.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    
    val haptic = LocalHapticFeedback.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Entrance animations
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    // Track scroll for FAB visibility
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }
    
    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    
    // Parallax effect for top bar
    val topBarAlpha by remember {
        derivedStateOf {
            val expandedRatio = scrollBehavior.state.collapsedFraction
            1f - expandedRatio * 0.3f
        }
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            UserTerminalDrawer(
                userProfile = userProfile,
                isOpen = drawerState.isOpen,
                onHIBPClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToHIBP()
                },
                onCVEClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToCVE()
                },
                onSettingsClick = { /* Navigate to settings */ },
                onSignOutClick = { viewModel.signOut() },
                onClose = { scope.launch { drawerState.close() } }
            )
        },
        gesturesEnabled = true,
        scrimColor = DarkBackground.copy(alpha = 0.85f)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = DarkBackground,
            topBar = {
                CyberPulseCollapsingTopBar(
                    userProfile = userProfile,
                    onProfileClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch { drawerState.open() }
                    },
                    onSearchClick = { /* Open search */ },
                    onNotificationClick = { /* Open notifications */ },
                    scrollBehavior = scrollBehavior,
                    alpha = topBarAlpha
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showScrollToTop,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    PremiumScrollToTopFab(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch { listState.animateScrollToItem(0) }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                    },
                    label = "feedContent"
                ) { state ->
                    when (state) {
                        is HomeUiState.Loading -> {
                            SkeletonLoadingState()
                        }
                        is HomeUiState.Success -> {
                            IntelligenceFeed(
                                articles = state.articles,
                                dailyTip = dailyTip,
                                listState = listState,
                                onArticleClick = { article ->
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.markAsRead(article.id)
                                    onNavigateToArticle(article.id)
                                },
                                onSaveClick = { article ->
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.toggleSave(article.id)
                                },
                                onShareClick = { article ->
                                    // Share intent
                                },
                                onDismissTip = { tip ->
                                    viewModel.dismissTip(tip.id)
                                },
                                onRefresh = { viewModel.refresh() },
                                onHIBPClick = onNavigateToHIBP,
                                onCVEClick = onNavigateToCVE
                            )
                        }
                        is HomeUiState.Error -> {
                            ErrorState(
                                message = state.message,
                                onRetry = { viewModel.refresh() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CyberPulseCollapsingTopBar(
    userProfile: UserProfile?,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    alpha: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "titleGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleGlowPulse"
    )
    
    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = "CyberPulse",
                    style = NeonHeadlineStyle.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    ),
                    modifier = Modifier.graphicsLayer { this.alpha = glowAlpha }
                )
                
                // Subtitle appears when expanded
                AnimatedVisibility(
                    visible = scrollBehavior.state.collapsedFraction < 0.5f,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = getGreeting(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                ProfileAvatar(
                    photoUrl = userProfile?.photoUrl,
                    displayName = userProfile?.displayName
                )
            }
        },
        actions = {
            // Search button
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Notification button with badge
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = CyberRed,
                            contentColor = TextPrimary
                        ) {
                            Text(
                                text = "3",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = DarkBackground,
            scrolledContainerColor = DarkSurface.copy(alpha = 0.95f),
            navigationIconContentColor = TextPrimary,
            titleContentColor = TextPrimary,
            actionIconContentColor = TextSecondary
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ProfileAvatar(
    photoUrl: String?,
    displayName: String?
) {
    Box(
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing ring
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            CyberCyan.copy(alpha = 0.8f),
                            CyberGreen.copy(alpha = 0.6f),
                            CyberCyan.copy(alpha = 0.8f)
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(DarkSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName?.first()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = CyberCyan
                )
            }
        }
    }
}

@Composable
private fun PremiumScrollToTopFab(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "fabGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabGlowPulse"
    )
    
    Box(
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .size(64.dp)
                .blur(16.dp)
                .background(
                    color = CyberCyan.copy(alpha = glowAlpha),
                    shape = CircleShape
                )
        )
        
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            containerColor = DarkSurface,
            contentColor = CyberCyan,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowUp,
                contentDescription = "Scroll to top",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntelligenceFeed(
    articles: List<NewsArticle>,
    dailyTip: DailyTip?,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onArticleClick: (NewsArticle) -> Unit,
    onSaveClick: (NewsArticle) -> Unit,
    onShareClick: (NewsArticle) -> Unit,
    onDismissTip: (DailyTip) -> Unit,
    onRefresh: () -> Unit,
    onHIBPClick: () -> Unit,
    onCVEClick: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()
    
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
            delay(1000) // Minimum refresh time for better UX
            isRefreshing = false
            pullRefreshState.endRefresh()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily Tip Card
            if (dailyTip != null && !dailyTip.isDismissed) {
                item(key = "daily_tip") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { -50 })
                    ) {
                        DailyTipCard(
                            tip = dailyTip,
                            onDismiss = { onDismissTip(dailyTip) }
                        )
                    }
                }
            }
            
            // Quick actions row
            item(key = "quick_actions") {
                QuickActionsRow(
                    onHIBPClick = onHIBPClick,
                    onCVEClick = onCVEClick
                )
            }
            
            // Section header
            item(key = "feed_header") {
                FeedSectionHeader()
            }
            
            // News articles with staggered animation
            items(
                items = articles,
                key = { it.id }
            ) { article ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { 50 }
                    )
                ) {
                    NewsCard(
                        article = article,
                        onArticleClick = onArticleClick,
                        onSaveClick = onSaveClick,
                        onShareClick = onShareClick
                    )
                }
            }
        }
        
        // Pull to refresh indicator
        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = DarkCard,
            contentColor = CyberCyan
        )
    }
}

@Composable
private fun SkeletonLoadingState() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shimmer for tip card area
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                DarkCard,
                                DarkCardElevated.copy(alpha = 0.5f),
                                DarkCard
                            )
                        )
                    )
            )
        }
        
        // Quick actions shimmer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkCard)
                    )
                }
            }
        }
        
        // News card shimmers
        items(5) {
            NewsCardShimmer()
        }
    }
}

@Composable
private fun DailyTipCard(
    tip: DailyTip,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tipGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tipGlowPulse"
    )
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CyberGreen.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = CyberGreen.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon with glow
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .blur(12.dp)
                        .background(
                            color = CyberGreen.copy(alpha = glowAlpha),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = CyberGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY TIP",
                        style = TerminalHeaderStyle.copy(fontSize = 10.sp),
                        color = CyberGreen
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(CyberGreen, CircleShape)
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = tip.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Dismiss",
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onHIBPClick: () -> Unit,
    onCVEClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionChip(
            label = "HIBP Check",
            icon = Icons.Outlined.Security,
            color = CyberRed,
            onClick = onHIBPClick,
            modifier = Modifier.weight(1f)
        )
        
        QuickActionChip(
            label = "CVE Lookup",
            icon = Icons.Outlined.BugReport,
            color = CyberOrange,
            onClick = onCVEClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "chipScale"
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(10.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = color
            )
        }
    }
}

@Composable
private fun FeedSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(3.dp, 16.dp)
                .background(CyberCyan, RoundedCornerShape(2.dp))
        )
        
        Spacer(modifier = Modifier.width(10.dp))
        
        Text(
            text = "INTELLIGENCE_FEED",
            style = TerminalHeaderStyle.copy(fontSize = 11.sp),
            color = CyberCyan
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Divider.copy(alpha = 0.5f),
            thickness = 0.5.dp
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Filter button
        Surface(
            onClick = { /* Open filters */ },
            shape = RoundedCornerShape(8.dp),
            color = DarkCard.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Filter",
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "All",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "errorPulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "errorIconPulse"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow behind icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .blur(24.dp)
                    .background(
                        color = CyberRed.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            )
            
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .scale(iconScale),
                tint = CyberRed.copy(alpha = 0.8f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Connection Lost",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberCyan,
                contentColor = DarkBackground
            ),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

private fun getGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when {
        hour < 12 -> "Good morning, stay secure 🛡️"
        hour < 18 -> "Good afternoon, stay vigilant 👀"
        else -> "Good evening, stay protected 🔒"
    }
}

// UI State
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val articles: List<NewsArticle>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
