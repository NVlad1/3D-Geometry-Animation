package io.github.nvlad1.function3danimator.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.nvlad1.function3danimator.PersistHelper
import io.github.nvlad1.function3danimator.R
import io.github.nvlad1.function3danimator.model.FunctionData
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(val persistHelper: PersistHelper): ViewModel() {
    private val gridSizeMin = 1
    private val gridSizeMax = 500

    private val _gridSizeString = MutableLiveData("${gridX} x ${gridY}")
    val gridSizeString: LiveData<String> = _gridSizeString

    val gridX: Int
        get() = FunctionData.NumberOfDotsX

    val gridY: Int
        get() = FunctionData.NumberOfDotsY

    private val _isGridShown = MutableLiveData<Boolean>(false)
    val isGridShown: LiveData<Boolean> = _isGridShown

    private val _isCustomKeyboardShown = MutableLiveData<Boolean>(false)
    val isCustomKeyboardShown: LiveData<Boolean> = _isCustomKeyboardShown

    private val _graphScreenOrientation = MutableLiveData<EnumMainActivityOrientation>(EnumMainActivityOrientation.landscape)
    val graphScreenOrientation: LiveData<EnumMainActivityOrientation> = _graphScreenOrientation

    fun onClickShowGrid(){
        val newValue = !isGridShown.value!!
        _isGridShown.value = newValue
        persistHelper.setShowGridPref(newValue)
    }

    fun onClickCustomKeyboard(){
        val newValue = !isCustomKeyboardShown.value!!
        _isCustomKeyboardShown.value = newValue
        persistHelper.setCustomKeyboardPref(newValue)
    }

    fun setGraphScreenOrientation(orientation: EnumMainActivityOrientation){
        _graphScreenOrientation.value = orientation
        persistHelper.setMainScreenOrientationPref(orientation)
    }

    fun setGridSize(xstr: String, ystr: String){
        try {
            val x = Integer.parseInt(xstr)
            FunctionData.NumberOfDotsX = Math.max(gridSizeMin, Math.min(x, gridSizeMax))
        } catch (e: NumberFormatException){
            e.printStackTrace()
        }

        try {
            val y = Integer.parseInt(ystr)
            FunctionData.NumberOfDotsY = Math.max(gridSizeMin, Math.min(y, gridSizeMax))
        } catch (e: NumberFormatException){
            e.printStackTrace()
        }

        _gridSizeString.value = "${gridX} x ${gridY}"
    }

    init{
        _isGridShown.value = persistHelper.getShowGridPref()
        _isCustomKeyboardShown.value = persistHelper.getCustomKeyboardPref()
        _graphScreenOrientation.value = persistHelper.getMainScreenOrientationPref()
    }
}