package com.bangkit.ecojourney.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.data.response.ArticlesItem
import com.bangkit.ecojourney.databinding.ItemArticleBinding
import com.bangkit.ecojourney.utils.DateConverter.Companion.formatDate
import com.bangkit.ecojourney.utils.GlideApp
import com.bangkit.ecojourney.utils.GlideAppModule
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ArticleAdapter(private val onClickCard: (ArticlesItem) -> Unit):
    ListAdapter<ArticlesItem, ArticleAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticleBinding.
        inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class MyViewHolder(val binding: ItemArticleBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            with (binding) {
                tvTitle.text = article.title
                tvDate.text = formatDate(article.datePublished)
                tvPublisher.text = article.publisher
                GlideApp.with(binding.root)
                    .load(article.imgUrl)
//                    .apply(RequestOptions().override(100, 100))
//                    .centerCrop()
                    .into(ivArticle)
            }

            itemView.setOnClickListener {
//                intent to detail article
                onClickCard(article)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame (oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame (oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}