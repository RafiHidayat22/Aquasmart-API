package com.example.aquasmart.API

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST ("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("dateBirth") dateBirth: String,
        @Field("phoneNumber") phoneNumber: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    // API untuk mengupdate foto profil
    @Multipart
    @PUT("profile/picture")
    suspend fun updateProfilePicture(
        @Header("Authorization") token: String,
        @Part profilePicture: MultipartBody.Part
    ): UpdateProfilePictureResponse


    //waterPredict fit 1
    @POST("api/water-quality")
    suspend fun waterPredict(
        @Header("Authorization") token: String,
        @Body waterData: WaterData
    ): ModelPredictWaterResponse

    //predict 2
    @POST("prediksiPanen")
    suspend fun prediksiPanen(
        @Header("Authorization") token: String,
        @Body PredictionRequest: PredictionRequest
    ): PredictionResponse
}