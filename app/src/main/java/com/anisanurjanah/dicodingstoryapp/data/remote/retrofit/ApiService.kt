package com.anisanurjanah.dicodingstoryapp.data.remote.retrofit

import com.anisanurjanah.dicodingstoryapp.data.remote.response.GeneralResponse
import com.anisanurjanah.dicodingstoryapp.data.remote.response.LoginResponse
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): GeneralResponse
}