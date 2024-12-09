package com.example.aquasmart.ui.auth.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aquasmart.API.ApiService
import com.example.aquasmart.API.ProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileViewModel(application: Application, private val apiService: ApiService) : AndroidViewModel(application) {

    private val _profileData = MutableLiveData<ProfileResponse?>()
    val profileData: LiveData<ProfileResponse?> = _profileData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _updateStatus = MutableLiveData<String>()
    val updateStatus: LiveData<String> = _updateStatus

    fun getProfile(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getProfile(token)
                if (response.status == "success") {
                    _profileData.postValue(response)
                } else {
                    _errorMessage.postValue("Failed to fetch profile data")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

    fun updateProfilePicture(token: String, imageFile: File) {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("profilePicture", imageFile.name, requestFile)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.updateProfilePicture(token, body)
                if (response.status == "success") {
                    _updateStatus.postValue("Profile picture updated successfully")
                } else {
                    _updateStatus.postValue("Failed to update profile picture")
                }
            } catch (e: Exception) {
                _updateStatus.postValue("Error: ${e.message}")
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val cursor = getApplication<Application>().contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("_data")
                val filePath = it.getString(columnIndex)
                return File(filePath)
            }
        }
        return null
    }

}
