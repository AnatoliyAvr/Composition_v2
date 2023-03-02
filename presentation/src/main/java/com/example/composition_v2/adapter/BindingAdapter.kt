package com.example.composition_v2.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.composition_v2.R
import com.example.domain.entity.GameResult

@BindingAdapter("bindScorePercentage")
fun bindScorePercentage(textView: TextView, gameResult: GameResult) {

    textView.text = String.format(
        textView.context.getString(R.string.score_percentage),
        getPercentOfRightAnswers(gameResult)
    )
}

private fun getPercentOfRightAnswers(gameResult: GameResult) = with(gameResult) {
    if (countOfQuestions == 0) {
        0
    } else {
        ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
}