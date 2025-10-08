package com.example.gopetalk_clean.di

import com.example.gopetalk_clean.data.audio.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    @Singleton
    fun provideWebSocketUrl(): String = "wss://TU_WS_URL_AQUI"

    @Provides
    @Singleton
    fun provideWebSocketManager(url: String): WebSocketManager {
        return WebSocketManager(OkHttpClient(), url)
    }
}
