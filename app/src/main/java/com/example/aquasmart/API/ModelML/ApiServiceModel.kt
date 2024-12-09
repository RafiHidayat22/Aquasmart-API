package com.example.aquasmart.API.ModelML

import com.example.aquasmart.API.ModelPredictWaterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServiceModel {
    @FormUrlEncoded
    @POST("predict")
    suspend fun waterPredict(
        @Field("ph") ph: Float,
        @Field("turbidity") turbidity: Float,
        @Field("temperature") temperature: Float
    ): ModelPredictWaterResponse
}