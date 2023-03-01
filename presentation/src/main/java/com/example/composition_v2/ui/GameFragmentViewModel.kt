package com.example.composition_v2.ui

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition_v2.R
import com.example.data.repository.GameRepositoryImpl
import com.example.domain.entity.GameResult
import com.example.domain.entity.GameSettings
import com.example.domain.entity.Level
import com.example.domain.entity.Questions
import com.example.domain.usecases.GenerateQuestionUseCase
import com.example.domain.usecases.GetGameSettingsUseCase

class GameFragmentViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {

    private var repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private lateinit var gameSettings: GameSettings
    private var timer: CountDownTimer? = null

    private var countOfRightAnswers: Int = 0
    private var countOfQuestions = 0

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Questions>()
    val question: LiveData<Questions>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOnRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _miPercent = MutableLiveData<Int>()
    val miPercent: LiveData<Int>
        get() = _miPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    init {
        startGame()
    }

    private fun startGame() {
        getGameSettings()
        startTimer()
        updateProgress()
        generateQuestion()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value =
            application.getString(R.string.progress_answers, countOfRightAnswers, gameSettings.minCountOfRightAnswers)
        _enoughCount.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercent.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int {
        if (countOfQuestions == 0) return 0
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (rightAnswer == number) {
            countOfRightAnswers++
        }
        countOfQuestions++
    }

    private fun getGameSettings() {
        gameSettings = getGameSettingsUseCase(level)
        _miPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun startTimer() {
        timer = object : CountDownTimer(gameSettings.gameTimeInSeconds * MILLS_IN_SECONDS, MILLS_IN_SECONDS) {

            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGames()
            }
        }
        timer?.start()
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val second = millisUntilFinished / MILLS_IN_SECONDS
        val minutes = second / SECOND_IN_MINUTES
        val leftSeconds = second - (minutes * SECOND_IN_MINUTES)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun finishGames() {
        _gameResult.value = GameResult(
            winner = enoughCount.value == true && enoughPercent.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings,
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        const val MILLS_IN_SECONDS = 1000L
        const val SECOND_IN_MINUTES = 60
    }
}