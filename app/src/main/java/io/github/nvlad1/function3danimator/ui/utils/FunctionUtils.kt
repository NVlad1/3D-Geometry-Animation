package io.github.nvlad1.function3danimator.ui.utils

import io.github.nvlad1.function3danimator.model.FunctionLimits
import io.github.nvlad1.function3danimator.model.calculationModel.Calculator
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType

object FunctionUtils {
    fun getDefaultLimits(type: FunctionDefinitionType): FunctionLimits{
        return when (type) {
            FunctionDefinitionType.defaultType -> {
                FunctionLimits(
                    xmin = -1.0f,
                    xmax = 1.0f,
                    ymin = -1.0f,
                    ymax = 1.0f,
                )
            }
            FunctionDefinitionType.elliptical -> {
                FunctionLimits(
                    xmin = 0.0f,
                    xmax = 1.0f,
                    ymin = 0.0f,
                    ymax = 6.283185f,
                )
            }
            FunctionDefinitionType.spherical -> {
                FunctionLimits(
                    xmin = 0.0f,
                    xmax = 3.141593f,
                    ymin = 0.0f,
                    ymax = 6.283185f,
                )
            }
            FunctionDefinitionType.parametric -> {
                FunctionLimits(
                    xmin = -1.0f,
                    xmax = 1.0f,
                    ymin = -1.0f,
                    ymax = 1.0f,
                )
            }
        }
    }

    fun checkConsistency(str: String, type: FunctionDefinitionType): Boolean{
        return try {
            Calculator.Builder(str, type).build()
            true
        } catch (e: Exception){
            false
        }
    }

    fun checkConsistency(strX: String, strY: String, strZ: String): Boolean{
        return try {
            val calcX = Calculator.Builder(strX, FunctionDefinitionType.parametric).build()
            val calcY = Calculator.Builder(strY, FunctionDefinitionType.parametric).build()
            val calcZ = Calculator.Builder(strZ, FunctionDefinitionType.parametric).build()
            true
        } catch (e: Exception){
            false
        }
    }
}