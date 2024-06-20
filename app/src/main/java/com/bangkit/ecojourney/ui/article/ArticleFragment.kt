package com.bangkit.ecojourney.ui.article

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.adapter.ArticleAdapter
import com.bangkit.ecojourney.adapter.ImageAdapter
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.databinding.FragmentArticleBinding
import com.bangkit.ecojourney.ui.ViewModelFactory
import com.bangkit.ecojourney.utils.DateConverter.Companion.formatDate
import kotlin.math.abs

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ArticleViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
    private lateinit var adapter: ImageAdapter
    private var carouselLength = 3 // default value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setUpTransformer()
        setupIndicators()
        setCurrentIndicators(0)
        setupRecyclerView()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2000)
                setCurrentIndicators(position % carouselLength)
            }
        })
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)

        binding.rvArticle.layoutManager = layoutManager

        setErrorView(false)
        viewModel.getAllArticles()
        viewModel.articles.observe(viewLifecycleOwner) {
                articles -> setArticleList(articles)
            if (articles.details?.articles?.isEmpty() == true) {
                setErrorView(true)
            } else {
                setErrorView(false)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
                isLoading -> showLoading(isLoading)
        }

        viewModel.errorToast.observe(viewLifecycleOwner) {
            errorToast -> errorToast?.let {
                if (errorToast) {
                    Toast.makeText(requireContext(), "Success to retrieve the data", Toast.LENGTH_SHORT).show()
                    viewModel.resetToast()
                } else {
                    Toast.makeText(requireContext(), "Failed to retrieve the data", Toast.LENGTH_SHORT).show()
                    viewModel.resetToast()
                    setErrorView(true)
                }
            }
        }
    }

    private fun setArticleList(articles: ArticleResponse) {
        val adapter = ArticleAdapter {
            val navController = findNavController()
            val bundle = Bundle().apply {
                putString(DetailArticleFragment.EXTRA_TITLE, it.title)
                putString(DetailArticleFragment.EXTRA_PUBLISHER, it.publisher)
                putString(DetailArticleFragment.EXTRA_DATE, it.datePublished?.let { it1 -> formatDate(it1) })
                putString(DetailArticleFragment.EXTRA_CONTENT, it.content)
                putString(DetailArticleFragment.EXTRA_IMAGE, it.imgUrl)
            }
            Log.d(TAG, "title: ${it.title}, publisher: ${it.publisher}, date: ${it.datePublished}, content: ${it.content}, image: ${it.imgUrl}")
            navController.navigate(R.id.action_navigation_articles_to_detailArticleFragment, bundle)
        }
        adapter.submitList(articles.details?.articles)
        binding.rvArticle.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setErrorView(isError: Boolean) {
        val isShow = if (isError) View.VISIBLE else View.GONE
        binding.ivError.visibility = isShow
        binding.tvError.visibility = isShow
        binding.btnRetry.visibility = isShow
        binding.rvArticle.visibility = if (isError) View.GONE else View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val runnable = Runnable {
        viewPager2.currentItem += 1
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }
        viewPager2.setPageTransformer(transformer)
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(imageList.size)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            val indicatorsContainer: LinearLayout = binding.indicatorContainer
            indicatorsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicators(index: Int) {
        val indicatorsContainer: LinearLayout = binding.indicatorContainer
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    private fun init() {
        viewPager2 = binding.viewPager2
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()

        imageList.add(R.drawable.carousel_metal)
        imageList.add(R.drawable.carousel_plastic)
        imageList.add(R.drawable.carousel_cardboard)

        carouselLength = imageList.size
        adapter = ImageAdapter(imageList, viewPager2)
        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    companion object {
        private const val TAG = "ArticleFragment"
    }
}