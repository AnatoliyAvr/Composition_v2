package com.example.data.repository

import com.example.domain.entity.GameSettings
import com.example.domain.entity.Level
import com.example.domain.entity.Questions
import com.example.domain.repository.GameRepository
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object GameRepositoryImpl : GameRepository {

    private const val MIN_SUM_VALUE = 2
    private const val MIN_ANSWER_VALUE = 1

    override fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Questions {
        val sum = Random.nextInt(MIN_SUM_VALUE, maxSumValue + 1)
        val visibleNumber = Random.nextInt(MIN_ANSWER_VALUE, sum)
        val options = hashSetOf<Int>()
        val rightAnswer = sum - visibleNumber
        options.add(rightAnswer)
        val from = max(rightAnswer - countOfOptions, MIN_ANSWER_VALUE)
        val to = min(maxSumValue, rightAnswer + countOfOptions)
        while (options.size < countOfOptions) {
            options.add(Random.nextInt(from, to))
        }
        return Questions(sum, visibleNumber, options.toList())
    }

    override fun getGameSettings(level: Level): GameSettings {
       return when(level) {
           Level.TEST -> GameSettings(10, 3, 50, 8)
           Level.EASY -> GameSettings(10, 10, 70, 60)
           Level.NORMAL -> GameSettings(20, 20, 80, 40)
           Level.HARD -> GameSettings(30, 30, 90, 40)
       }
    }
}