package io.github.nvlad1.function3danimator.openGLutils

import io.github.nvlad1.function3danimator.model.FunctionModel

data class FunctionRenderState(
    val functions: List<FunctionModel>,
    val border: Float
) {
    val functionsNumber: Int
        get() = functions.size

    fun getFunction(index: Int): FunctionModel? {
        return functions.getOrNull(index)
    }
}
