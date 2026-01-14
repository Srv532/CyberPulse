package com.cyberpulse.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberpulse.domain.model.OmniSearchResult
import com.cyberpulse.domain.model.SearchResultItem
import com.cyberpulse.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Search Bar
        SearchInput(
            query = query,
            onQueryChanged = viewModel::onQueryChanged,
            onBack = onNavigateBack
        )

        // Results
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is SearchUiState.Idle -> IdleView()
                is SearchUiState.Loading -> LoadingView()
                is SearchUiState.Success -> SearchResultsList(state.result)
                is SearchUiState.Error -> ErrorView(state.message)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInput(
    query: String,
    onQueryChanged: (String) -> Unit,
    onBack: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search threats, CVEs, repos...", color = TextSecondary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = CyberCyan,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
fun SearchResultsList(result: OmniSearchResult) {
    if (result.isEmpty()) {
        EmptyStateView()
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Definitions
        if (result.definitions.isNotEmpty()) {
            item { SectionHeader("DEFINITIONS") }
            items(result.definitions.size) { i ->
                DefinitionCard(result.definitions[i])
            }
        }

        // Local News
        if (result.localResults.isNotEmpty()) {
            item { SectionHeader("IN THIS APP") }
            items(result.localResults.size) { i ->
                LocalNewsResultCard(result.localResults[i])
            }
        }

        // GitHub
        if (result.githubRepos.isNotEmpty()) {
            item { SectionHeader("GITHUB REPOSITORIES") }
            items(result.githubRepos.size) { i ->
                GitHubRepoCard(result.githubRepos[i])
            }
        }

        // Reddit
        if (result.redditPosts.isNotEmpty()) {
            item { SectionHeader("COMMUNITY DISCUSSIONS") }
            items(result.redditPosts.size) { i ->
                RedditPostCard(result.redditPosts[i])
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = TerminalHeaderStyle,
        color = CyberPurple,
        modifier = Modifier
            .padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun DefinitionCard(item: SearchResultItem.Definition) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.term, color = CyberYellow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.meaning, color = TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
fun GitHubRepoCard(item: SearchResultItem.GitHubRepo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, color = CyberCyan, fontWeight = FontWeight.SemiBold)
                if (!item.description.isNullOrBlank()) {
                    Text(
                        item.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = CyberYellow, modifier = Modifier.size(12.dp))
                    Text(" ${item.stars}", color = TextTertiary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    if (item.language != null) {
                        Text(item.language, color = CyberPurple, fontSize = 12.sp)
                    }
                }
            }
            Icon(Icons.Outlined.ArrowOutward, null, tint = TextTertiary)
        }
    }
}


@Composable
fun RedditPostCard(item: SearchResultItem.RedditPost) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.subreddit, color = CyberOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.title, color = TextPrimary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("↑ ${item.upvotes} upvotes", color = TextTertiary, fontSize = 12.sp)
        }
    }
}

@Composable
fun LocalNewsResultCard(item: SearchResultItem.LocalNews) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Thumbnail placeholder
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(DarkCard, RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(item.article.title, color = TextPrimary, maxLines = 2, fontWeight = FontWeight.Medium)
            Text(item.article.source, color = TextSecondary, fontSize = 12.sp)
        }
    }
}


@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CyberCyan)
    }
}

@Composable
fun IdleView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Listening for signals...", color = TextTertiary, style = TerminalTextStyle)
    }
}

@Composable
fun EmptyStateView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No intel found.", color = TextTertiary)
    }
}

@Composable
fun ErrorView(msg: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Connection Error: $msg", color = CyberRed)
    }
}
