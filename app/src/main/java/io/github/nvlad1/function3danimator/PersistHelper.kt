package io.github.nvlad1.function3danimator

import android.content.SharedPreferences
import io.github.nvlad1.function3danimator.model.FunctionData
import io.github.nvlad1.function3danimator.ui.settings.EnumMainActivityOrientation
import javax.inject.Inject

class PersistHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val customKeyboardPrefKey = "custom_keyboard"
    private val showGridPrefKey = "show_grid"
    private val mainActivityOrientationPrefKey = "main_activity_orientation"
    private val gridXPrefKey = "gridX"
    private val gridYPrefKey = "gridY"

    init{
        initGridSize()
    }

    private fun initGridSize(){
        val gridX = sharedPreferences.getInt(gridXPrefKey, 50)
        val gridY = sharedPreferences.getInt(gridYPrefKey, 50)
        FunctionData.NumberOfDotsX = gridX
        FunctionData.NumberOfDotsY = gridY
    }

    fun getCustomKeyboardPref(): Boolean{
        return sharedPreferences.getBoolean(customKeyboardPrefKey, true)
    }

    fun setCustomKeyboardPref(isOn: Boolean){
        sharedPreferences.edit().putBoolean(customKeyboardPrefKey, isOn).apply()
    }

    fun getShowGridPref(): Boolean{
        return sharedPreferences.getBoolean(showGridPrefKey, true)
    }

    fun setShowGridPref(isOn: Boolean){
        sharedPreferences.edit().putBoolean(showGridPrefKey, isOn).apply()
    }

    fun setMainScreenOrientationPref(orientation: EnumMainActivityOrientation){
        sharedPreferences.edit().putInt(mainActivityOrientationPrefKey, orientation.ordinal).apply()
    }

    fun getMainScreenOrientationPref(): EnumMainActivityOrientation{
        return EnumMainActivityOrientation.values()[sharedPreferences.getInt(mainActivityOrientationPrefKey, 0)]
    }

    fun setGridSize(gridX: Int, gridY: Int){
        sharedPreferences.edit().putInt(gridXPrefKey, gridX).apply()
        sharedPreferences.edit().putInt(gridYPrefKey, gridY).apply()
        FunctionData.NumberOfDotsX = if (gridX > 0) gridX else 1
        FunctionData.NumberOfDotsY = if (gridY > 0) gridY else 1
    }
}
