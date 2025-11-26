package com.example.gopetalk_clean.di

import com.example.gopetalk_clean.data.audio.WebSocketManager
import com.example.gopetalk_clean.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    @Singleton
    @NetworkModule.WebSocketUrl
    fun provideWebSocketUrl(): String = Constants.WS_URL

    @Provides
    @Singleton
    fun provideWebSocketManager(
        client: OkHttpClient,
        @NetworkModule.WebSocketUrl url: String
    ): WebSocketManager {
        return WebSocketManager(client, url)
    }
}
