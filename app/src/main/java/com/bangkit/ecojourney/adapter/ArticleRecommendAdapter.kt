package com.bangkit.ecojourney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ecojourney.data.response.ArticleItem
import com.bangkit.ecojourney.databinding.ArticleRecommendationListItemBinding
import com.bangkit.ecojourney.databinding.ScanResultListItemBinding

class ArticleRecommendAdapter(private val data: List<ArticleItem>, private val onClickListener: (Int) -> Unit) : RecyclerView.Adapter<ArticleRecommendAdapter.ArticleRecommendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleRecommendViewHolder {
        val binding = ArticleRecommendationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleRecommendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleRecommendViewHolder, position: Int) {
        val recommendArticle = data[position]
        holder.binding.apply {
            articleTitle.text = recommendArticle.title
            readArticleBtn.setOnClickListener{
                Toast.makeText(holder.itemView.context, "Read ${recommendArticle.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = data.size

    class ArticleRecommendViewHolder(val binding : ArticleRecommendationListItemBinding) : RecyclerView.ViewHolder(binding.root)

}