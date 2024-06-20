package com.bangkit.ecojourney.ui.onboarding

import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.ecojourney.databinding.ActivityRegisterBinding
import com.bangkit.ecojourney.ui.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<OnBoardingViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val name = binding.nameEditText.text.toString()

            if (password == confirmPassword) {
                viewModel.register(name, email, password)

                viewModel.registerResponse.observe(this) {response ->
                    if (!response.error) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        AlertDialog.Builder(this).apply {
                            setTitle("Oops! Register failed!")
                            setMessage(response.message)
                            setPositiveButton("Back", null)
                            create()
                            show()
                        }
                    }
                }
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle("Oops! Register failed!")
                    setMessage("Password didn't match!")
                    setPositiveButton("Back", null)
                    create()
                    show()
                }
            }
        }
    }
}