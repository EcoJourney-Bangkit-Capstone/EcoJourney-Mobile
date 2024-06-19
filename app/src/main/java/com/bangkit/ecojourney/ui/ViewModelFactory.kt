package com.bangkit.ecojourney.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.repository.Repository
import com.bangkit.ecojourney.data.repository.UserRepository
import com.bangkit.ecojourney.di.Injection
import com.bangkit.ecojourney.ui.article.ArticleViewModel
import com.bangkit.ecojourney.ui.home.HomeViewModel
import com.bangkit.ecojourney.ui.onboarding.OnBoardingViewModel
import com.bangkit.ecojourney.ui.splashscreen.SplashScreenViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(repository as UserRepository) as T
            }
            modelClass.isAssignableFrom(OnBoardingViewModel::class.java) -> {
                OnBoardingViewModel(repository as UserRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository as UserRepository) as T
            }
            modelClass.isAssignableFrom(ArticleViewModel::class.java) -> {
                ArticleViewModel(repository as ArticleRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, type: String): ViewModelFactory {
            synchronized(ViewModelFactory::class.java) {
                when (type) {
                    "user" -> INSTANCE = ViewModelFactory(Injection.provideUserRepository(context))
                    "article" -> INSTANCE = ViewModelFactory(Injection.provideArticleRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}