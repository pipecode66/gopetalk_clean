package com.example.gopetalk_clean.di

import com.example.gopetalk_clean.data.repository.AudioRepositoryImpl
import com.example.gopetalk_clean.domain.repository.AudioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {

    @Binds
    @Singleton
    abstract fun bindAudioRepository(
        impl: AudioRepositoryImpl
    ): AudioRepository
}
