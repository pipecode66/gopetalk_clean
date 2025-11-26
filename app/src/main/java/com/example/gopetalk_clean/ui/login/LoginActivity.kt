package com.example.gopetalk_clean.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gopetalk_clean.databinding.ActivityLoginBinding
import com.example.gopetalk_clean.domain.state.LoginUiState
import com.example.gopetalk_clean.event.LoginUiEvent
import com.example.gopetalk_clean.ui.main.MainActivity
import com.example.gopetalk_clean.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            loginViewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            loginViewModel.goToRegister()
        }

        observeUiEvents()
        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            loginViewModel.loginState.collectLatest { state ->
                when (state) {
                    is LoginUiState.Loading -> binding.btnLogin.isEnabled = false
                    is LoginUiState.Success -> binding.btnLogin.isEnabled = true
                    is LoginUiState.Error -> {
                        binding.btnLogin.isEnabled = true
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun observeUiEvents() {
        lifecycleScope.launch {
            loginViewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is LoginUiEvent.ShowMessage -> {
                        Toast.makeText(this@LoginActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginUiEvent.NavigateToMain -> goToMain()
                    is LoginUiEvent.NavigateToRegister -> goToRegister()
                    is LoginUiEvent.ShowEmailError -> {
                        binding.tilEmail.error = event.error
                        binding.tilEmail.isErrorEnabled = event.error != null
                    }
                    is LoginUiEvent.ShowPasswordError -> {
                        binding.tilPassword.error = event.error
                        binding.tilPassword.isErrorEnabled = event.error != null
                    }
                }
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
