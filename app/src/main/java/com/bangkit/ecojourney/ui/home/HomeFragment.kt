package com.bangkit.ecojourney.ui.home

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.databinding.FragmentHomeBinding
import com.bangkit.ecojourney.ui.ViewModelFactory
import com.bangkit.ecojourney.ui.onboarding.LoginActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.logoutBtn.setOnClickListener {
            homeViewModel.logout()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        with (binding) {
            setupFAQ(layoutFAQ1, cardFAQ1, answerFAQ1, btnFAQ1)
            setupFAQ(layoutFAQ2, cardFAQ2, answerFAQ2, btnFAQ2)
            setupFAQ(layoutFAQ3, cardFAQ3, answerFAQ3, btnFAQ3)
            setupFAQ(layoutFAQ4, cardFAQ4, answerFAQ4, btnFAQ4)

            val currentDate = SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault()).format(Date())
            tvDate.text = currentDate
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupFAQ(layout: ViewGroup, card: View, answer: View, button: View) {
        layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        card.setOnClickListener {
            val isExpand = if (answer.visibility == View.GONE) View.VISIBLE else View.GONE
            answer.visibility = isExpand

            if (isExpand == View.VISIBLE) {
                (button as? ImageView)?.setImageResource(R.drawable.ic_minimize)
            } else {
                (button as? ImageView)?.setImageResource(R.drawable.ic_expand)
            }
        }
    }
}