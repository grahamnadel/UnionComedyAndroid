package com.example.unioncomedy

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VoteViewModel: ViewModel() {
    private val _teams = MutableStateFlow(
        listOf(
            Team("Team 1"),
            Team("Team 2")
        )
    )
    val teams: StateFlow<List<Team>> = _teams

    private val db = FirebaseFirestore.getInstance()

    // Optional: expose UI feedback as state
    private val _voteResult = MutableStateFlow<String?>(null)
    val voteResult: StateFlow<String?> = _voteResult

    fun submitVote(teamName: String) {
        val vote = hashMapOf(
            "timestamp" to System.currentTimeMillis(),
            "value" to 1,
            "team" to teamName
        )

        db.collection("votes")
            .add(vote)
            .addOnSuccessListener { documentReference ->
                _voteResult.value = "Vote added with ID: ${documentReference.id}"
            }
            .addOnFailureListener { e ->
                _voteResult.value = "Error adding vote: $e"
            }
    }

    // Optionally, add a function to clear the vote result after showing
    fun clearVoteResult() {
        _voteResult.value = null
    }

    // function to update a team name
    fun updateTeamName(index: Int, newName: String) {
        val currentList = _teams.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = currentList[index].copy(name = newName)
            _teams.value = currentList
        }
    }
}