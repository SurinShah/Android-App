package com.example.csci571hw4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci571hw4.network.ApiClient
import com.example.csci571hw4.network.model.LoginRequest
import com.example.csci571hw4.network.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl

    private val _fullName = MutableStateFlow<String?>(null)
    val fullName: StateFlow<String?> = _fullName

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = ApiClient.authService.login(request)

                if (response.isSuccessful) {
                    _loginSuccess.value = true
                    _loginError.value = null
                    _isAuthenticated.value = true

                    loadProfile()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Login failed: $errorBody")
                    _loginError.value = "Invalid email or password"
                }
            } catch (e: Exception) {
                println("Exception during login: ${e.localizedMessage}")
                e.printStackTrace()
                _loginError.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = RegisterRequest(fullname = fullName, email = email, password = password)
                val response = ApiClient.authService.register(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody ?: "").getString("error")
                    } catch (e: Exception) {
                        "Registration failed. Please try again."
                    }
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val response = ApiClient.authService.getMeInfo()
                _isAuthenticated.value = true

                loadProfile()
            } catch (e: Exception) {
                _isAuthenticated.value = false
                _profileImageUrl.value = null
                _fullName.value = null
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                ApiClient.authService.logout()
            } catch (_: Exception) { }
            _isAuthenticated.value = false
            _profileImageUrl.value = null
            _fullName.value = null
        }
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ApiClient.authService.deleteAccount()
                if (response.isSuccessful) {
                    _isAuthenticated.value = false
                    _profileImageUrl.value = null
                    _fullName.value = null
                    onSuccess()
                } else {
                    onError("Failed to delete account.")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _loginSuccess.value = false
        _loginError.value = null
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val response = ApiClient.authService.getMeInfo()
                _profileImageUrl.value = response.profileImageUrl
                _fullName.value = response.fullName
            } catch (_: Exception) {
                _profileImageUrl.value = null
                _fullName.value = null
            }
        }
    }
}
