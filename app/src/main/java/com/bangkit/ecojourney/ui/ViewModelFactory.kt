package com.bangkit.ecojourney.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.repository.Repository
import com.bangkit.ecojourney.data.repository.ScanRepository
import com.bangkit.ecojourney.data.repository.UserRepository
import com.bangkit.ecojourney.di.Injection
import com.bangkit.ecojourney.ui.article.ArticleViewModel
import com.bangkit.ecojourney.ui.home.HomeViewModel
import com.bangkit.ecojourney.ui.onboarding.OnBoardingViewModel
import com.bangkit.ecojourney.ui.splashscreen.SplashScreenViewModel
import com.bangkit.ecojourney.ui.wastescan.WasteScanViewModel

class ViewModelFactory(private val userRepository: UserRepository,
                        private val articleRepository: ArticleRepository,
                        private val scanRepository: ScanRepository
) : ViewModelProvider.NewInstanceFactory() {
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
            modelClass.isAssignableFrom(WasteScanViewModel::class.java) -> {
                WasteScanViewModel(articleRepository, scanRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideUserRepository(context),
                        Injection.provideArticleRepository(context),
                        Injection.provideScanRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}