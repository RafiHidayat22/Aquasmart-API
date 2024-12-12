package com.example.aquasmart.API

//auth
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

//res profile
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
    val profilePicture: String
)

//res pp

data class UpdateProfilePictureResponse(
    val status: String,
    val message: String,
    val data: ProfilePictureData?
)

data class ProfilePictureData(
    val profilePicture: String
)


//res fit 1
data class ModelPredictWaterResponse(
    val message: String,
    val error: ErrorDetail?,
    val prediction: String,
    val recommendation: String
)

data class ErrorDetail(
    val error: String
)
data class WaterData(
    val ph: Float,
    val turbidity: Float,
    val temperature: Float
)

//res fit 2

data class PredictionRequest(
    val fish_size: Float,
    val water_condition: String,
    val fish_type: String,
    val feed_amount: Float
)

data class PredictionResponse(
    val message: String,
    val data: PredictionData
)

data class PredictionData(
    val predicted_days_to_harvest: String,
    val recommended_feed: String
)

//dataset ikan
data class FishTypesResponse(
    val message: String,
    val data: List<FishType>
)

data class FishType(
    val name: String
)


