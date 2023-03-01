package com.example.composition_v2.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition_v2.R
import com.example.composition_v2.databinding.FragmentGameBinding
import com.example.composition_v2.extensions.parcelable
import com.example.domain.entity.GameResult
import com.example.domain.entity.Level

class GameFragment : Fragment() {

    private val viewModel: GameFragmentViewModel by lazy {
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameFragmentViewModel::class.java]
    }
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("GameFragment == null")

    private lateinit var level: Level

    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setClickListenerToOptions()
        viewModel.startGame(level)
    }

    private fun setClickListenerToOptions() {
        tvOptions.forEach { textView ->
            textView.setOnClickListener {
                viewModel.chooseAnswer(textView.text.toString().toInt())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.apply {
            question.observe(viewLifecycleOwner) { questions ->
                binding.apply {
                    tvSum.text = questions.sum.toString()
                    tvLeftNumber.text = questions.visibleNumber.toString()
                    tvOptions.forEachIndexed { index, textView ->
                        textView.text = questions.options[index].toString()
                    }
                }
            }
            percentOnRightAnswers.observe(viewLifecycleOwner) {
                binding.progressBar.setProgress(it, true)
            }
            enoughCount.observe(viewLifecycleOwner) {
                binding.tvAnswersProgress.setTextColor(getColorByState(it))
            }
            enoughPercent.observe(viewLifecycleOwner) {
                val color = getColorByState(it)
                binding.progressBar.progressTintList = ColorStateList.valueOf(color)
            }
            formattedTime.observe(viewLifecycleOwner) {
                binding.tvTimer.text = it
            }
            miPercent.observe(viewLifecycleOwner) {
                binding.progressBar.secondaryProgress = it
            }
            gameResult.observe(viewLifecycleOwner) {
                launchGameFinishedFragment(it)
            }
            progressAnswers.observe(viewLifecycleOwner) {
                binding.tvAnswersProgress.text = it
            }
        }
    }

    private fun getColorByState(goodState: Boolean): Int {
        val colorResId = if (goodState) android.R.color.holo_green_light else android.R.color.holo_red_light
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    private fun parseArgs() {
        requireArguments().parcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_container, GameFinishedFragment.newInstance(gameResult)
            )
            .addToBackStack(null)
            .commit()
    }

    companion object {

        const val NAME = "game_fragment"
        private const val KEY_LEVEL = "level"
        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}