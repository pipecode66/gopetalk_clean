package com.example.gopetalk_clean.di

import android.content.Context
import com.example.gopetalk_clean.data.api.AuthInterceptor
import com.example.gopetalk_clean.data.api.AuthService
import com.example.gopetalk_clean.data.api.ChannelService
import com.example.gopetalk_clean.data.storage.SessionManager
import com.example.gopetalk_clean.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Qualifier
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WebSocketUrl

    // SessionManager
    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager =
        SessionManager(context)

    // Logging Interceptor
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    // OkHttpClient con AuthInterceptor
    @Provides
    @Singleton
    fun provideOkHttpClient(
        sessionManager: SessionManager,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(loggingInterceptor)
            .build()

    // Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    // ApiService
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    //ChannelService
    @Provides
    @Singleton
    fun provideChannelService(retrofit: Retrofit): ChannelService =
        retrofit.create(ChannelService::class.java)

    // WebSocket Factory (inyección de dependencias dinámica)
    @Provides
    fun provideWebSocketFactory(
        client: OkHttpClient,
        sessionManager: SessionManager
    ): (String, String, WebSocketListener) -> WebSocket = { channelName, userId, listener ->
        val token = sessionManager.getAccessToken()
        val request = Request.Builder()
            .url(Constants.WS_URL)
            .addHeader("Authorization", "Bearer $token")
            .build()
        client.newWebSocket(request, listener)
    }
}
