package com.example.aquasmart.API

data class LoginResponse(
    val status: String,
    val message: String,
    val token: String
)

data class RegisterResponse(
    val status: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val userId: String
    )
}


data class ProfileResponse(
    val status: String,
    val data: ProfileData
)

data class ProfileData(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val dateBirth: String,
    val profilePicture: String // URL gambar profil
)

data class UpdateProfilePictureResponse(
    val status: String,
    val message: String,
    val data: ProfilePictureData? // Menggunakan ProfilePictureData untuk gambar profil yang baru
)

data class ProfilePictureData(
    val profilePicture: String // URL gambar profil yang baru
)

data class ModelPredictWaterResponse(
    val result: String
)