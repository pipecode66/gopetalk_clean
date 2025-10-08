// di/RepositoryModule.kt
package com.example.gopetalk_clean.di

import com.example.gopetalk_clean.data.repository.ChannelRepositoryImpl
import com.example.gopetalk_clean.domain.repository.ChannelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChannelModule {

    @Binds
    @Singleton
    abstract fun bindChannelRepository(
        impl: ChannelRepositoryImpl
    ): ChannelRepository


}
