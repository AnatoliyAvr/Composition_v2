package com.example.domain.repository

import com.example.domain.entity.GameSettings
import com.example.domain.entity.Level
import com.example.domain.entity.Questions

interface GameRepository {

    fun generateQuestion(
        maxSumValue: Int,
        countOfOptions: Int
    ): Questions

    fun getGameSettings(level: Level): GameSettings
}