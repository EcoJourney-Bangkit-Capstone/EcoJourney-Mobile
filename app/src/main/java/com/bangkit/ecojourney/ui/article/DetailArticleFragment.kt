package com.bangkit.ecojourney.ui.article

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.databinding.FragmentArticleBinding
import com.bangkit.ecojourney.databinding.FragmentDetailArticleBinding
import com.bumptech.glide.Glide

class DetailArticleFragment : Fragment() {

    private var _binding: FragmentDetailArticleBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailArticleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        args?.let {
            with(binding) {
                articleTitle.text = it.getString(EXTRA_TITLE)
                articlePublisher.text = it.getString(EXTRA_PUBLISHER)
                articleDate.text = it.getString(EXTRA_DATE)
                articleContent.text = it.getString(EXTRA_CONTENT)
                it.getString(EXTRA_IMAGE)?.let { imageUrl ->
                    // Load image from URL using an image loading library like Glide or Picasso
                    // Example with Glide:
                    Glide.with(this@DetailArticleFragment)
                        .load(imageUrl)
                        .into(articleImage)
                }
            }
            Log.d("DetailArticle", "title: ${it.getString(EXTRA_TITLE)}, publisher: ${it.getString(EXTRA_PUBLISHER)}, date: ${it.getString(EXTRA_DATE)}, content: ${it.getString(EXTRA_CONTENT)}, image: ${it.getString(EXTRA_IMAGE)}")
        }

        binding.backCard.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_PUBLISHER = "extra_publisher"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_CONTENT = "extra_content"
        const val EXTRA_IMAGE = "extra_image"
    }
}