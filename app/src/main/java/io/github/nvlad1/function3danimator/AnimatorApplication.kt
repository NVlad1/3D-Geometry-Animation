package io.github.nvlad1.function3danimator

import android.app.Application
import io.github.nvlad1.function3danimator.database.FunctionRepository
import io.github.nvlad1.function3danimator.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AnimatorApplication: Application() {
    @Inject lateinit var mFunctionRepository: FunctionRepository
    @Inject @ApplicationScope lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            mFunctionRepository.loadFunctions()
        }
    }
}
