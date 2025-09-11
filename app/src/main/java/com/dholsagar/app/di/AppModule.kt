// file: com/dholsagar/app/di/AppModule.kt
package com.dholsagar.app.di

import com.dholsagar.app.data.repository.impl.UserRepositoryImpl
import com.dholsagar.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}