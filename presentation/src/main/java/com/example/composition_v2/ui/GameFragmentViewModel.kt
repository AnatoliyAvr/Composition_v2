package com.example.composition_v2.ui

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composition_v2.R
import com.example.data.repository.GameRepositoryImpl
import com.example.domain.entity.GameResult
import com.example.domain.entity.GameSettings
import com.example.domain.entity.Level
import com.example.domain.entity.Questions
import com.example.domain.usecases.GenerateQuestionUseCase
import com.example.domain.usecases.GetGameSettingsUseCase

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private val context = application
    private lateinit var gameSettings: GameSettings
    private lateinit var level: Level
    private var timer: CountDownTimer? = null

    private var countOfRightAnswers: Int = 0
    private var countOfQuestions = 0

    private val _formattedTime = MutableLiveData<String>()
    private val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Questions>()
    val question: LiveData<Questions>
        get() = _question

    private val _percentOnRightAnswers = MutableLiveData<Int>()
    val percentOnRightAnswers: LiveData<Int>
        get() = _percentOnRightAnswers

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
    private val miPercent: LiveData<Int>
        get() = _miPercent

    private val _gameResult = MutableLiveData<GameResult>()
    private val gameResult: LiveData<GameResult>
        get() = _gameResult

    fun startGame(level: Level) {
        getGameSettings(level)
        startTimer()
        generateQuestion()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOnRightAnswers.value = percent
        _progressAnswers.value =
            context.getString(R.string.progress_answers, countOfRightAnswers, gameSettings.minCountOfRightAnswers)
        _enoughCount.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercent.value = percent >= gameSettings.minPercentOfRightAnswer
    }

    private fun calculatePercentOfRightAnswers(): Int {
        return (countOfRightAnswers / countOfQuestions * 100.0).toInt()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (rightAnswer == number) {
            countOfRightAnswers++
        }
        countOfQuestions++
    }

    private fun getGameSettings(level: Level) {
        this.level = level
        gameSettings = getGameSettingsUseCase(level)
        _miPercent.value = gameSettings.minPercentOfRightAnswer
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

    fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val second = millisUntilFinished / MILLS_IN_SECONDS
        val minutes = second / SECOND_IN_MINUTES
        val leftSeconds = second - (minutes * SECOND_IN_MINUTES)
        return String.format("%0.2d:%0.2d", minutes, leftSeconds)
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