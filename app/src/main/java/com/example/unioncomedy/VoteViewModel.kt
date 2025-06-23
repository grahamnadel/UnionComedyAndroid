package com.example.unioncomedy

import androidx.lifecycle.ViewModel

class VoteViewModel: ViewModel() {
    private val _teams = mutableListOf(
        Team("Team 1"),
        Team("Team 2")
    )
    val teams: List<Team> = _teams

    fun vote() {
//        TODO: add firebase support
    }
}