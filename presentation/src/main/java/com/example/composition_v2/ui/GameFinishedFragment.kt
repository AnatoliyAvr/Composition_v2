package com.example.composition_v2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.composition_v2.R
import com.example.composition_v2.databinding.FragmentGameFinishedBinding
import com.example.composition_v2.extensions.parcelable
import com.example.domain.entity.GameResult

class GameFinishedFragment : Fragment() {

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("GameFinishedFragment = null")

    private lateinit var gameResult: GameResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parsArgs()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
        bindView()
    }

    private fun bindView() {
        with(binding) {
            emojiResult.setImageResource(getSmileResId())
            tvRequiredAnswers.text = getString(R.string.required_score, gameResult.gameSettings.minCountOfRightAnswers)
            tvScoreAnswers.text = getString(R.string.score_answers, gameResult.countOfRightAnswers)
            tvRequiredPercentage.text = getString(R.string.required_percentage, gameResult.gameSettings.minPercentOfRightAnswers)
            tvScorePercentage.text = getString(R.string.score_percentage, getPercentOfRightAnswer())
        }
    }

    private fun getPercentOfRightAnswer() = with(gameResult) {
        if (countOfQuestions == 0) 0
        else ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun getSmileResId(): Int {
        return if (gameResult.winner) R.drawable.ic_smile else R.drawable.ic_sad
    }

    private fun setupClickListener() {
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    private fun retryGame() {
        findNavController().popBackStack()
    }

    private fun parsArgs() {
        requireArguments().parcelable<GameResult>(KEY_GAME_RESULT)?.let {
            gameResult = it
        }
    }

    companion object {

        const val KEY_GAME_RESULT = "game_result"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}