package io.github.nvlad1.function3danimator.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.preference.PreferenceManager
import io.github.nvlad1.function3danimator.PersistHelper
import io.github.nvlad1.function3danimator.database.FunctionDao
import io.github.nvlad1.function3danimator.database.FunctionDatabase
import io.github.nvlad1.function3danimator.model.ColorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductionModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    @Singleton
    @Provides
    fun provideFunctionDatabase(@ApplicationContext appContext: Context): FunctionDatabase {
        return Room.databaseBuilder(appContext, FunctionDatabase::class.java, DATABASE_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun provideFunctionDao(functionDatabase: FunctionDatabase): FunctionDao {
        return functionDatabase.getFunctionDao()
    }

    @Singleton
    @Provides
    fun providePersistHelper(sharedPreferences: SharedPreferences): PersistHelper {
        return PersistHelper(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideColorManager(): ColorManager {
        return ColorManager()
    }

    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(
        @MainDispatcher mainDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    @Provides
    @Singleton
    @IoScope
    fun provideIoScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    @Provides
    @Singleton
    @DefaultScope
    fun provideDefaultScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)
 

    companion object {
        private const val DATABASE_NAME = "functions"
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoScope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultScope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultDispatcher
