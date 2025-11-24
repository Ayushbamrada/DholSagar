//// file: com/dholsagar/app/di/AppModule.kt
//package com.dholsagar.app.di
//
////import com.dholsagar.app.data.repository.impl.FakeProviderRepositoryImpl
//import com.dholsagar.app.data.repository.impl.ProviderRepositoryImpl
//import com.dholsagar.app.data.repository.impl.UserRepositoryImpl
//import com.dholsagar.app.domain.repository.ProviderRepository
//import com.dholsagar.app.domain.repository.UserRepository
//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class AppModule {
//
//    @Binds
//    @Singleton
//    abstract fun bindUserRepository(
//        userRepositoryImpl: UserRepositoryImpl
//    ): UserRepository
//
//    @Binds
//    @Singleton
//    abstract fun bindProviderRepository( // ADD THIS
//        providerRepositoryImpl: ProviderRepositoryImpl
//    ): ProviderRepository
//}
//
////    companion object {
////        @Provides
////        @Singleton
////        fun provideProviderRepository(): ProviderRepository {
////            return FakeProviderRepositoryImpl()
////        }
////    }
////}


// file: com/dholsagar/app/di/AppModule.kt
package com.dholsagar.app.di

import com.dholsagar.app.data.repository.impl.ProviderRepositoryImpl
import com.dholsagar.app.data.repository.impl.UserRepositoryImpl
import com.dholsagar.app.domain.repository.ProviderRepository
import com.dholsagar.app.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideProviderRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        auth: FirebaseAuth
    ): ProviderRepository {
        return ProviderRepositoryImpl(firestore, storage, auth)
    }
}