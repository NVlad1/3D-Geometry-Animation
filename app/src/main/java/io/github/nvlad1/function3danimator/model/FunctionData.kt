package io.github.nvlad1.function3danimator.model

import android.util.Log
import io.github.nvlad1.function3danimator.model.calculationModel.Calculator
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import io.github.nvlad1.function3danimator.openGLutils.FunctionRenderState
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Naboka Vladislav on 01.10.2017.
 */
class FunctionData internal constructor(
    private val funcID: Int,
    private val renderState: FunctionRenderState
) {
    private val TAG = FunctionData::class.java.simpleName
    private var xmin = 0f
    private var xmax = 0f
    private var ymin = 0f
    private var ymax = 0f
    lateinit var pointArray: FloatArray
        private set
    private lateinit var func: FunctionModel
    private var calc: Calculator? = null

    private var calcX: Calculator? = null
    private var calcY: Calculator? = null
    private var calcZ: Calculator? = null

    private var currentTime = 0f

    init {
        buildPointArray()
    }

    private fun preCalculation() {
        func = renderState.getFunction(funcID)!!
        xmin = func.xmin
        xmax = func.xmax
        ymin = func.ymin
        ymax = func.ymax
        currentTime = ((System.currentTimeMillis() - startTime).toFloat() * func.timeFactor)
        if (func.type == FunctionDefinitionType.parametric){
            calcX = Calculator.Builder(func.strX!!, func.type).build()
            calcY = Calculator.Builder(func.strY!!, func.type).build()
            calcZ = Calculator.Builder(func.strZ!!, func.type).build()
        } else {
            calc = Calculator.Builder(func.string!!, func.type).build()
        }
    }

    fun buildPointArray() {
        preCalculation()
        pointArray = FloatArray(NumberOfDotsX * NumberOfDotsY * 3)
        try {
            for (i in 0 until NumberOfDotsX) {
                for (j in 0 until NumberOfDotsY) {
                    val x = getX(i)
                    val y = getY(j)
                    when (func.type) {
                        FunctionDefinitionType.defaultType -> {
                            pointArray[3 * (i * NumberOfDotsY + j)] = x
                            pointArray[3 * (i * NumberOfDotsY + j) + 1] = y
                            pointArray[3 * (i * NumberOfDotsY + j) + 2] = calc!!.evaluate(x,y,currentTime)
                        }
                        FunctionDefinitionType.elliptical -> {
                            pointArray[3 * (i * NumberOfDotsY + j)] = x * cos(y.toDouble()).toFloat()
                            pointArray[3 * (i * NumberOfDotsY + j) + 1] = x * sin(y.toDouble()).toFloat()
                            pointArray[3 * (i * NumberOfDotsY + j) + 2] = calc!!.evaluate(x,y,currentTime)
                        }
                        FunctionDefinitionType.spherical -> {
                            val r = calc!!.evaluate(x,y,currentTime)
                            if (r >= 0) {
                                pointArray[3 * (i * NumberOfDotsY + j)] = r * Math.sin(x.toDouble()).toFloat() * Math.cos(y.toDouble()).toFloat()
                                pointArray[3 * (i * NumberOfDotsY + j) + 1] = r * Math.sin(x.toDouble()).toFloat() * Math.sin(y.toDouble()).toFloat()
                                pointArray[3 * (i * NumberOfDotsY + j) + 2] = r * Math.cos(x.toDouble()).toFloat()
                            } else {
                                Log.d(TAG, "r is negative")
                            }
                        }
                        FunctionDefinitionType.parametric -> {
                            pointArray[3 * (i * NumberOfDotsY + j)] = calcX!!.evaluate(x,y,currentTime)
                            pointArray[3 * (i * NumberOfDotsY + j) + 1] = calcY!!.evaluate(x,y,currentTime)
                            pointArray[3 * (i * NumberOfDotsY + j) + 2] = calcZ!!.evaluate(x,y,currentTime)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPointValueX(i: Int, j: Int): Float {
        return pointArray[3 * (i * NumberOfDotsY + j)]
    }

    fun getPointValueY(i: Int, j: Int): Float {
        return pointArray[3 * (i * NumberOfDotsY + j) + 1]
    }

    fun getPointValueZ(i: Int, j: Int): Float {
        return pointArray[3 * (i * NumberOfDotsY + j) + 2]
    }

    fun getX(i: Int): Float {
        return xmin + (xmax - xmin) * i / (NumberOfDotsX - 1).toFloat()
    }

    fun getY(i: Int): Float {
        return ymin + (ymax - ymin) * i / (NumberOfDotsY - 1).toFloat()
    }

    companion object {
        var NumberOfDotsX = 50
        var NumberOfDotsY = 50
        var startTime = System.currentTimeMillis()

        fun resetTime(){
            startTime = System.currentTimeMillis()
        }
    }
}
