package io.github.nvlad1.function3danimator.model.calculationModel

import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by Vlad on 31.05.2016.
 */
internal class Gamma {
    private val Nmax = 10000
    private val step = 0.01
    private val x_in = 5.0
    private val x_fin = 20.0

    private fun stirling(x: Double): Double {
        return sqrt(2 * Math.PI * x) * (x / exp(1.0)).pow(x)
    }

    private fun direct(x: Double): Double {
        var sum = 0.0
        for (i in 0 until Nmax) {
            sum += subGamma(step * i, x) * step
        }
        return sum
    }

    private fun subGamma(t: Double, x: Double): Double {
        return t.pow(x - 1) * exp(-t)
    }

    fun gammaFunc(x: Double): Double {
        if (x < 0) return Double.NaN
        if (x == 0.0) return 1.0
        if (x < x_in) return direct(x)
        return if ((x >= x_in) and (x <= x_fin)) {
            recursiveGamma(x)
        } else stirling(x - 1)
    }

    private fun recursiveGamma(x: Double): Double {
        var sum = 1.0
        var x0 = x
        while (x0 >= x_in) {
            sum *= x0 - 1
            x0--
        }
        sum *= direct(x0)
        return sum
    }
}