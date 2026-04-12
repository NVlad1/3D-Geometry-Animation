package io.github.nvlad1.function3danimator.openGLutils

import android.content.Context
import android.opengl.GLES20
import io.github.nvlad1.function3danimator.openGLutils.FileUtils.readTextFromRaw

object ShaderUtils {
    fun createProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programId = GLES20.glCreateProgram()
        if (programId == 0) {
            return 0
        }
        GLES20.glAttachShader(programId, vertexShaderId)
        GLES20.glAttachShader(programId, fragmentShaderId)
        GLES20.glLinkProgram(programId)
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programId)
            return 0
        }
        return programId
    }

    fun createShader(context: Context, type: Int, shaderRawId: Int): Int {
        val shaderText = readTextFromRaw(context, shaderRawId)
        return createShader(type, shaderText)
    }

    private fun createShader(type: Int, shaderText: String?): Int {
        val shaderId = GLES20.glCreateShader(type)
        if (shaderId == 0) {
            return 0
        }
        GLES20.glShaderSource(shaderId, shaderText)
        GLES20.glCompileShader(shaderId)
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderId)
            return 0
        }
        return shaderId
    }
}