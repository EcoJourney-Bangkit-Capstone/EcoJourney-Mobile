package com.bangkit.ecojourney.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.databinding.FragmentArticleBinding
import com.bangkit.ecojourney.databinding.FragmentProfileBinding
import com.bangkit.ecojourney.ui.ViewModelFactory
import com.bangkit.ecojourney.ui.article.ArticleViewModel
import com.bangkit.ecojourney.ui.onboarding.LoginActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSelfInfo()
        viewModel.selfResponse.observe(viewLifecycleOwner) {
            with(binding) {
                tvFullNameHeader.text = it?.data?.displayName ?: "User"
                tvFullName.text = it?.data?.displayName ?: "User"
                tvEmail.text = it?.data?.email ?: "user@gmail.com"
            }
        }

        binding.backCard.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.logoutBtn.setOnClickListener {
            viewModel.logout()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}