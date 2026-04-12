package io.github.nvlad1.function3danimator.ui.function_screen

import androidx.lifecycle.ViewModel
import io.github.nvlad1.function3danimator.PersistHelper
import io.github.nvlad1.function3danimator.exception.ParseException
import io.github.nvlad1.function3danimator.model.EnumColor
import io.github.nvlad1.function3danimator.database.FunctionRepository
import io.github.nvlad1.function3danimator.model.FunctionModel
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.TimeUnit
import io.github.nvlad1.function3danimator.ui.utils.FunctionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FunctionViewModelCompose @Inject constructor(
    val functionRepository: FunctionRepository,
    val persistHelper: PersistHelper
) : ViewModel() {
    private val _functionModel = MutableStateFlow<FunctionModel>(FunctionModel.defaultEmpty())
    val functionModel: StateFlow<FunctionModel> = _functionModel

    private val _isCustomKeyboardEnabled = MutableStateFlow<Boolean>(persistHelper.getCustomKeyboardPref())
    val isCustomKeyboardEnabled: StateFlow<Boolean> = _isCustomKeyboardEnabled

    var functionId: String? = null

    fun loadFunction(id: String?) {
        this.functionId = id
        _functionModel.value = try {
            if (id == null) {
                val newFunction = functionRepository.addFunction("")
                functionId = newFunction.id
                newFunction
            } else {
                functionRepository.getFunction(id)!!
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            FunctionModel("", functionRepository.unusedColor())
        }
    }

    fun setFunctionStr(str: String) {
        _functionModel.update { it.copy(string = str) }
    }

    fun setFunctionStrX(str: String) {
        _functionModel.update { it.copy(strX = str) }
    }

    fun setFunctionStrY(str: String) {
        _functionModel.update { it.copy(strY = str) }
    }

    fun setFunctionStrZ(str: String) {
        _functionModel.update { it.copy(strZ = str) }
    }

    fun setColor(color: EnumColor) {
        _functionModel.update { it.copy(color = color) }
    }

    fun setTimeUnit(timeUnit: TimeUnit?) {
        if (timeUnit == null) return
        _functionModel.update { it.copy(timeMeasurementMode = timeUnit) }
    }

    fun setFunctionType(typeStr: String) {
        if (typeStr == _functionModel.value.type.description) return
        val type = FunctionDefinitionType.entries.find { it.description == typeStr } ?: return
        val functionLimits = FunctionUtils.getDefaultLimits(type)
        _functionModel.update {
            it.copy(
                type = type,
                xmin = functionLimits.xmin,
                xmax = functionLimits.xmax,
                ymin = functionLimits.ymin,
                ymax = functionLimits.ymax
            )
        }
        _functionModel.value
    }

    fun setXMin(value: Float) {
        _functionModel.update { it.copy(xmin = value) }
    }

    fun setXMax(value: Float) {
        _functionModel.update { it.copy(xmax = value) }
    }

    fun setYMin(value: Float) {
        _functionModel.update { it.copy(ymin = value) }
    }

    fun setYMax(value: Float) {
        _functionModel.update { it.copy(ymax = value) }
    }

    fun save(): Boolean {
        //if edit text(s) is empty, then remove function
        val function = functionModel.value
        if ((function.type != FunctionDefinitionType.parametric && function.string.isNullOrEmpty()) ||
            (function.type == FunctionDefinitionType.parametric &&
                    (function.strZ.isNullOrEmpty() || function.strX.isNullOrEmpty() || function.strY.isNullOrEmpty()))
        ) {
            val id = functionId ?: return true
            functionRepository.deleteFunction(id)
            return true
        }

        try {
            if (function.type != FunctionDefinitionType.parametric) {
                if (!FunctionUtils.checkConsistency(
                        function.string ?: "",
                        function.type
                    )
                ) return false
                functionRepository.upsertFunction(function)
            } else {
                if (!FunctionUtils.checkConsistency(
                        function.strX ?: "",
                        function.strY ?: "",
                        function.strZ ?: ""
                    )
                ) {
                    return false
                }
                functionRepository.upsertFunction(function)
            }
        } catch (e: ParseException) {
            return false
        }
        return true
    }

    fun updateKeyboardSettingFromPreference() {
        _isCustomKeyboardEnabled.value = persistHelper.getCustomKeyboardPref()
    }
}
