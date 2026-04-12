package io.github.nvlad1.function3danimator.model

import io.github.nvlad1.function3danimator.exception.ParseException
import io.github.nvlad1.function3danimator.exception.UnknownException
import io.github.nvlad1.function3danimator.model.calculationModel.Calculator
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType.parametric
import io.github.nvlad1.function3danimator.model.calculationModel.enums.TimeUnit
import io.github.nvlad1.function3danimator.ui.utils.FunctionUtils
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID
import kotlin.math.abs

/**
 * Created by Naboka Vladislav on 30.10.2017.
 */
data class FunctionModel(
    val xmin: Float,
    val xmax: Float,
    val ymin: Float,
    val ymax: Float,
    val string: String?,
    val strX: String?,
    val strY: String?,
    val strZ: String?,
    val type: FunctionDefinitionType,
    val timeMeasurementMode: TimeUnit,
    val color: EnumColor?,
    val id: String = UUID.randomUUID().toString(),
    val createdAt: Long = System.currentTimeMillis()
) {

    val isParametric: Boolean
        get() = type == parametric

    val timeFactor: Float
        get() = when (timeMeasurementMode) {
            TimeUnit.seconds -> 0.001f
            TimeUnit.milliseconds -> 1f
        }

    private var _isTimeDependent: Boolean? = null
    val isTimeDependent: Boolean
        get() {
            if (_isTimeDependent == null) {
                try{
                    check()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return _isTimeDependent ?: true
        }

    val isEmpty: Boolean
        get() {
            return if (type == parametric) {
                strX.isNullOrEmpty() || strY.isNullOrEmpty() || strZ.isNullOrEmpty()
            } else {
                string.isNullOrEmpty()
            }
        }

    //non-parametric default
    constructor(
        str: String?,
        color: EnumColor,
        type: FunctionDefinitionType = FunctionDefinitionType.defaultType
    ) : this(
        xmin = FunctionUtils.getDefaultLimits(type).xmin,
        xmax = FunctionUtils.getDefaultLimits(type).xmax,
        ymin = FunctionUtils.getDefaultLimits(type).ymin,
        ymax = FunctionUtils.getDefaultLimits(type).ymax,
        string = str,
        strX = null,
        strY = null,
        strZ = null,
        type = type,
        timeMeasurementMode = TimeUnit.seconds,
        color = color
    )

    @Throws(ParseException::class, UnknownException::class)
    private fun check(str: String, type: FunctionDefinitionType) {
        val calc = Calculator.Builder(str, type).build()
        _isTimeDependent = calc.isTimeDependent
    }

    private fun check(strX: String, strY: String, strZ: String) {
        val calcX = Calculator.Builder(strX, parametric).build()
        val calcY = Calculator.Builder(strY, parametric).build()
        val calcZ = Calculator.Builder(strZ, parametric).build()
        _isTimeDependent = calcX.isTimeDependent || calcY.isTimeDependent || calcZ.isTimeDependent
    }

    @Throws(ParseException::class, UnknownException::class)
    fun check() {
        if (type != parametric) {
            check(string!!, type)
        } else {
            check(strX!!, strY!!, strZ!!)
        }
    }

    fun getBorder(): Float {
        return when (type) {
            FunctionDefinitionType.defaultType -> {
                val borders = arrayListOf(abs(xmin), abs(xmax), abs(ymin), abs(ymax))
                borders.maxOrNull() ?: 1f
            }

            FunctionDefinitionType.spherical -> {
                abs(xmax)
            }

            else -> 1f
        }

    }

    fun cardString(): String {
        return when (type) {
            FunctionDefinitionType.defaultType -> "z(x,y) = " + (string ?: "")
            FunctionDefinitionType.elliptical -> "z(r,\u03c6) = " + (string ?: "")
            FunctionDefinitionType.spherical -> "r(\u03b8,\u03c6) = " + (string ?: "")
            parametric -> ""
        }
    }

    fun cardStringParametric(): Triple<String, String, String> {
        if (type != parametric) return Triple("", "", "")
        return Triple(
            "x(u,v) = " + (strX ?: ""),
            "y(u,v) = " + (strY ?: ""),
            "z(u,v) = " + (strZ ?: "")
        )
    }

    companion object {
        fun defaultEmpty(): FunctionModel {
            return FunctionModel("", EnumColor.RED)
        }
    }
}
