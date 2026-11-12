package com.fsv.dailyroutine.feature.habits.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fsv.dailyroutine.core.designsystem.theme.DailyRoutineTheme

@Composable
fun HabitListRoute(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HabitListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HabitListScreen(
        state = state,
        onAddClick = onAddClick,
        onToggle = viewModel::onToggle,
        modifier = modifier,
    )
}

@Composable
fun HabitListScreen(
    state: HabitListUiState,
    onAddClick: () -> Unit,
    onToggle: (habitId: Long, completed: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add habit")
            }
        },
    ) { innerPadding ->
        when (state) {
            HabitListUiState.Loading -> LoadingContent(Modifier.padding(innerPadding))
            HabitListUiState.Empty -> EmptyContent(Modifier.padding(innerPadding))
            is HabitListUiState.Content -> HabitList(state, onToggle, Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("No habits yet", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tap + to create your first habit.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HabitList(
    state: HabitListUiState.Content,
    onToggle: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 88.dp), // espaço para o FAB não tapar o último item
    ) {
        item(key = "header") {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text("Today", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "${state.completedToday} of ${state.habits.size} done",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(items = state.habits, key = { it.id }) { habit ->
            HabitRow(habit = habit, onToggle = onToggle)
        }
    }
}

@Composable
private fun HabitRow(
    habit: HabitItemUi,
    onToggle: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.toggleable(
            value = habit.completedToday,
            role = Role.Checkbox,
            onValueChange = { onToggle(habit.id, it) },
        ),
        headlineContent = { Text(habit.name) },
        supportingContent = habit.description?.let { description -> { Text(description) } },
        trailingContent = { Checkbox(checked = habit.completedToday, onCheckedChange = null) },
    )
}

@Preview(showBackground = true)
@Composable
private fun HabitListContentPreview() {
    DailyRoutineTheme {
        HabitListScreen(
            state = HabitListUiState.Content(
                habits = listOf(
                    HabitItemUi(1, "Drink water", null, completedToday = true),
                    HabitItemUi(2, "Read 20 pages", "Before bed", completedToday = false),
                ),
                completedToday = 1,
            ),
            onAddClick = {},
            onToggle = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitListEmptyPreview() {
    DailyRoutineTheme {
        HabitListScreen(state = HabitListUiState.Empty, onAddClick = {}, onToggle = { _, _ -> })
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitListLoadingPreview() {
    DailyRoutineTheme {
        HabitListScreen(state = HabitListUiState.Loading, onAddClick = {}, onToggle = { _, _ -> })
    }
}