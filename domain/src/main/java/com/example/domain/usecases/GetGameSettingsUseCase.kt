package com.example.domain.usecases

import com.example.domain.entity.GameSettings
import com.example.domain.entity.Level
import com.example.domain.repository.GameRepository

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(level: Level): GameSettings {
        return repository.getGameSettings(level)
    }
}