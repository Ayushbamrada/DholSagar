// file: com/dholsagar/app/di/AuthModule.kt
package com.dholsagar.app.di

import android.content.Context
import com.dholsagar.app.R
import com.dholsagar.app.data.repository.impl.AuthRepositoryImpl
import com.dholsagar.app.domain.repository.AuthRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideOneTapClient(
            @ApplicationContext context: Context
        ): SignInClient {
            return Identity.getSignInClient(context)
        }
    }
}