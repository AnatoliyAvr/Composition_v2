package com.example.composition_v2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition_v2.databinding.FragmentGameFinishedBinding

class GameFinishedFragment : Fragment() {

    private val args by navArgs<GameFinishedFragmentArgs>()

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("GameFinishedFragment = null")

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
            gameResult = args.gameResult
//            tvRequiredAnswers.text = getString(R.string.required_score, args.gameResult.gameSettings.minCountOfRightAnswers)
//            tvScoreAnswers.text = getString(R.string.score_answers, args.gameResult.countOfRightAnswers)
//            tvRequiredPercentage.text = getString(R.string.required_percentage, args.gameResult.gameSettings.minPercentOfRightAnswers)
//            tvScorePercentage.text = getString(R.string.score_percentage, getPercentOfRightAnswer())
        }
    }

//    private fun getPercentOfRightAnswer() = with(args.gameResult) {
//        if (countOfQuestions == 0) 0
//        else ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
//    }

//    private fun getSmileResId(): Int {
//        return if (args.gameResult.winner) R.drawable.ic_smile else R.drawable.ic_sad
//    }

    private fun setupClickListener() {
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    private fun retryGame() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}