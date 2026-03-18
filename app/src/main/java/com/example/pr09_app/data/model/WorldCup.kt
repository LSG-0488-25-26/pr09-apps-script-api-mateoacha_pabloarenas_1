package com.example.pr09_app.data.model

data class WorldCup(
    val year: Int,
    val country: String,
    val winner: String,
    val runnerup: String,
    val third: String,
    val fourth: String,
    val goals: Int,
    val qualified: Int,
    val matches: Int,
    val attendance: String,
)

