package com.truckspot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.truckspot.databinding.ActivityLoginBinding
import com.truckspot.fragment.Dashboard
import com.truckspot.request.LoginRequest
import com.truckspot.utils.Helper
import com.truckspot.utils.NetworkResult
import com.truckspot.utils.PrefRepository
import com.truckspot.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()
    lateinit var prefRepository: PrefRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        prefRepository = PrefRepository(this)
         setContentView(binding.root)
        binding.etDriver.setText(prefRepository.getUserName())
        binding.etPass.setText(prefRepository.getPassword())
        binding.etDriver.setText("test4")
        binding.etPass.setText("test@123")
//
        binding.rememberme.isChecked = prefRepository.getLoggedIn()

        binding.forgotpassword.setOnClickListener {
            val int = Intent(this, ForgotpasswordActivity::class.java)
            startActivity(int)
        }

        binding.submit.setOnClickListener {

            if (binding.etDriver.text.toString().isNullOrEmpty()) {
                Toast.makeText(this, "driver id cannot be empty ", Toast.LENGTH_SHORT).show()
            } else if (binding.etPass.text.toString().isNullOrEmpty()) {
                binding.etPass.error = "password cannot be empty "
            } else {
                Helper.hideKeyboard(it)
                binding.txtError.visibility = View.GONE

                val validationResult = validateUserInput()
                if (validationResult.first) {
                    binding.progressBar.isVisible = true
                    val loginRequest = LoginRequest(
                        binding.etDriver.text.toString(),
                        binding.etPass.text.toString()
                    )
                    loginViewModel.loginUser(
                        loginRequest
                    )
                } else {
                    showValidationErrors(validationResult.second)
                }
//
            }
        }
        bindObservers()
    }

    var token: String? = ""
    private fun bindObservers() {
        loginViewModel.loginResponseLiveData.observe(this, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success<*> -> {
                    if (it.data!!.status) {
                        prefRepository.setLoggedIn(binding.rememberme.isChecked)

                        token = it.data.results.token
                        prefRepository.setName(it.data.results.username)
                        prefRepository.setToken(token!!)
                        if (binding.rememberme.isChecked) {
                            prefRepository.setUserName(binding.etDriver.text.toString())
                            prefRepository.setPassword(binding.etPass.text.toString())
                            val int = Intent(this, Dashboard::class.java)
                            startActivity(int)
                            finish()
                        } else {
                            prefRepository.setUserName("")
                            prefRepository.setPassword("")
                            val int = Intent(this, Dashboard::class.java)
                            startActivity(int)
                            Toast.makeText(this, "User will not remember ", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {


                        showValidationErrors(it.data.message)
                    }
//                    tokenManager.saveToken(it.data!!.token)
                }
                is NetworkResult.Error<*> -> {
                    showValidationErrors(it.message.toString())
                }
                is NetworkResult.Loading<*> -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val emailAddress = binding.etDriver.text.toString()
        val password = binding.etPass.text.toString()
        return loginViewModel.validateCredentials(emailAddress, "", password, true)
    }
    private fun showValidationErrors(error: String) {
        binding.txtError.visibility = View.VISIBLE

        binding.txtError.text =
            String.format(resources.getString(R.string.txt_error_message, error))
    }
}