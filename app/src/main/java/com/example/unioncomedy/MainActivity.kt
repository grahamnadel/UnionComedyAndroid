package com.example.unioncomedy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unioncomedy.ui.theme.UnionComedyTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val voteViewModel: VoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics

        enableEdgeToEdge()
        setContent {
            UnionComedyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val voteResult by voteViewModel.voteResult.collectAsStateWithLifecycle()
                    Column(modifier = Modifier.padding(innerPadding)) {
                        // Use the RowVoteButtons here:
                        RowVoteButtons(viewModel = voteViewModel, onVote = { team ->
                            voteViewModel.submitVote(team)
                        })

                        // Show the result text below the buttons
                        voteResult?.let {
                            Text(text = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoteButton(teamName: String, onVote: (String) -> Unit) {
    Button(onClick = { onVote(teamName) }) {
        Text(teamName)
    }
}

@Composable
fun RowVoteButtons(viewModel: VoteViewModel, onVote: (String) -> Unit) {
    val teams by viewModel.teams.collectAsStateWithLifecycle()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        teams.forEach { team ->
            VoteButton(teamName = team.name, onVote = onVote)
        }
    }
}

@Composable
fun AdminTeamEditor(viewModel: VoteViewModel) {
    val teams by viewModel.teams.collectAsState()

    // Remember editable names for each team
    val nameStates = remember(teams) {
        teams.map { mutableStateOf(it.name) }
    }

    Column {
        teams.forEachIndexed { index, team ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "Team ${index + 1}: ")

                TextField(
                    value = nameStates[index].value,
                    onValueChange = { nameStates[index].value = it }
                )

                Button(onClick = {
                    viewModel.updateTeamName(index, nameStates[index].value)
                }) {
                    Text("Update")
                }
            }
        }
    }
}