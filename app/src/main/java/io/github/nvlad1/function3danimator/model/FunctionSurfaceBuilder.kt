package io.github.nvlad1.function3danimator.model

import io.github.nvlad1.function3danimator.openGLutils.FunctionRenderState
import io.github.nvlad1.function3danimator.utils.FLAG_LIGHTING

/**
 * Created by Naboka Vladislav on 01.10.2017.
 */
class FunctionSurfaceBuilder(funcID: Int, renderState: FunctionRenderState) {
    private val funcData: FunctionData = FunctionData(funcID, renderState)
    lateinit var surfaceVertices: FloatArray
        private set
    lateinit var normalsVertices: FloatArray
        private set
    lateinit var surfaceGridVertices: FloatArray
        private set
    private var index = 0
    private var indexNorm = 0

    init {
        recalculateSurface()
    }

    fun recalculateSurface() {
        surfaceVertices = FloatArray(NumberOfVariablesInSurfaceArray)
        index = 0
        if (FLAG_LIGHTING) {
            normalsVertices = FloatArray(NumberOfVariablesInSurfaceArray)
            indexNorm = 0
        }
        funcData.buildPointArray()
        buildSurfaceVertices()
    }

    private fun buildSurfaceVertices() {
        for (i in 0 until FunctionData.NumberOfDotsX) {
            for (j in 0 until FunctionData.NumberOfDotsY) {
                if ((i + j) % 2 == 0) {
                    if (i > 0 && j > 0) {
                        addTriangleToArray(i, j, i, j - 1, i - 1, j)
                    }
                    if (i > 0 && j < FunctionData.NumberOfDotsY - 1) {
                        addTriangleToArray(i, j, i - 1, j, i, j + 1)
                    }
                    if (i < FunctionData.NumberOfDotsX - 1 && j > 0) {
                        addTriangleToArray(i, j, i + 1, j, i, j - 1)
                    }
                    if (i < FunctionData.NumberOfDotsX - 1 && j < FunctionData.NumberOfDotsY - 1) {
                        addTriangleToArray(i, j, i, j + 1, i + 1, j)
                    }
                }
            }
        }
        if (index != NumberOfVariablesInSurfaceArray) {
            throw AssertionError()
        }
    }

    private fun buildSurfaceGridVertices() {
        for (i in 0 until FunctionData.NumberOfDotsX) {
            for (j in 0 until FunctionData.NumberOfDotsY) {
                if ((i + j) % 2 == 0) {
                    if (j > 0) {
                        addLineToArray(i, j, i, j - 1)
                    }
                    if (i > 0) {
                        addLineToArray(i, j, i - 1, j)
                    }
                    if (i < FunctionData.NumberOfDotsX - 1) {
                        addLineToArray(i, j, i + 1, j)
                    }
                    if (j < FunctionData.NumberOfDotsY - 1) {
                        addLineToArray(i, j, i, j + 1)
                    }
                }
            }
        }
        if (index != NumberOfVariablesInSurfaceGridArray) {
            throw AssertionError()
        }
    }



    fun recalculateSurfaceGrid() {
        surfaceGridVertices = FloatArray(NumberOfVariablesInSurfaceGridArray)
        index = 0
        //funcData.buildPointArray();
        buildSurfaceGridVertices()
    }

    private fun addTriangleToArray(point1_ix: Int, point1_iy: Int, point2_ix: Int, point2_iy: Int, point3_ix: Int, point3_iy: Int) {
        //point order should be clockwise in XY plane
        surfaceVertices[index++] = funcData.getPointValueX(point1_ix, point1_iy)
        surfaceVertices[index++] = funcData.getPointValueY(point1_ix, point1_iy)
        surfaceVertices[index++] = funcData.getPointValueZ(point1_ix, point1_iy)
        surfaceVertices[index++] = funcData.getPointValueX(point2_ix, point2_iy)
        surfaceVertices[index++] = funcData.getPointValueY(point2_ix, point2_iy)
        surfaceVertices[index++] = funcData.getPointValueZ(point2_ix, point2_iy)
        surfaceVertices[index++] = funcData.getPointValueX(point3_ix, point3_iy)
        surfaceVertices[index++] = funcData.getPointValueY(point3_ix, point3_iy)
        surfaceVertices[index++] = funcData.getPointValueZ(point3_ix, point3_iy)
        if (FLAG_LIGHTING) {
            addNormalsToArray(point1_ix, point1_iy, point2_ix, point2_iy, point3_ix, point3_iy)
        }
    }

    private fun addLineToArray(point1_ix: Int, point1_iy: Int, point2_ix: Int, point2_iy: Int) {
        surfaceGridVertices[index++] = funcData.getPointValueX(point1_ix, point1_iy)
        surfaceGridVertices[index++] = funcData.getPointValueY(point1_ix, point1_iy)
        surfaceGridVertices[index++] = funcData.getPointValueZ(point1_ix, point1_iy)
        surfaceGridVertices[index++] = funcData.getPointValueX(point2_ix, point2_iy)
        surfaceGridVertices[index++] = funcData.getPointValueY(point2_ix, point2_iy)
        surfaceGridVertices[index++] = funcData.getPointValueZ(point2_ix, point2_iy)
    }

    private fun addNormalsToArray(point1_ix: Int, point1_iy: Int, point2_ix: Int, point2_iy: Int, point3_ix: Int, point3_iy: Int) {
        val x1 = funcData.getPointValueX(point1_ix, point1_iy)
        val y1 = funcData.getPointValueY(point1_ix, point1_iy)
        val z1 = funcData.getPointValueZ(point1_ix, point1_iy)
        val x2 = funcData.getPointValueX(point2_ix, point2_iy)
        val y2 = funcData.getPointValueY(point2_ix, point2_iy)
        val z2 = funcData.getPointValueZ(point2_ix, point2_iy)
        val x3 = funcData.getPointValueX(point3_ix, point3_iy)
        val y3 = funcData.getPointValueY(point3_ix, point3_iy)
        val z3 = funcData.getPointValueZ(point3_ix, point3_iy)
        //get two vectors of the triangle
        val ax = x2 - x1
        val ay = y2 - y1
        val az = z2 - z1
        val bx = x1 - x3
        val by = y1 - y3
        val bz = z1 - z3
        //calculate their vector product
        val nx = ay * bz - az * by
        val ny = az * bx - ax * bz
        val nz = ax * by - ay * bx
        val norm = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz)
        val nx_norm = nx / norm
        val ny_norm = ny / norm
        val nz_norm = nz / norm
        //add normalized vector product to array
        normalsVertices[indexNorm++] = nx_norm
        normalsVertices[indexNorm++] = ny_norm
        normalsVertices[indexNorm++] = nz_norm
        normalsVertices[indexNorm++] = nx_norm
        normalsVertices[indexNorm++] = ny_norm
        normalsVertices[indexNorm++] = nz_norm
        normalsVertices[indexNorm++] = nx_norm
        normalsVertices[indexNorm++] = ny_norm
        normalsVertices[indexNorm++] = nz_norm
    }

    companion object {
        val NumberOfVariablesInSurfaceArray: Int
            get() = 3 * 3 * 2 * (FunctionData.NumberOfDotsX - 1) * (FunctionData.NumberOfDotsY - 1)
        val NumberOfVariablesInSurfaceGridArray: Int
            get() = 2 * 3 * (2 * FunctionData.NumberOfDotsX * FunctionData.NumberOfDotsY -
                FunctionData.NumberOfDotsX - FunctionData.NumberOfDotsY)
    }
}
