package com.example.gopetalk_clean.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gopetalk_clean.databinding.ActivityRegisterBinding
import com.example.gopetalk_clean.event.RegisterUiEvent
import com.example.gopetalk_clean.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is RegisterUiEvent.ShowMessage -> {
                            Toast.makeText(this@RegisterActivity, event.message, Toast.LENGTH_SHORT).show()
                        }
                        is RegisterUiEvent.NavigateToLogin -> {
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmailAddress.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val firstName = binding.etName.text.toString()
            val lastName = binding.etLastName.text.toString()

            registerViewModel.register(firstName, lastName, email, password, confirmPassword)
        }
    }
}
