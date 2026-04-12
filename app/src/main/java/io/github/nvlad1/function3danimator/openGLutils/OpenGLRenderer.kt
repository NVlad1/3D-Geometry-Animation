package io.github.nvlad1.function3danimator.openGLutils

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import androidx.core.content.ContextCompat
import io.github.nvlad1.function3danimator.R
import io.github.nvlad1.function3danimator.model.EnumColor
import io.github.nvlad1.function3danimator.model.FunctionSurfaceBuilder
import io.github.nvlad1.function3danimator.model.FunctionSurfaceBuilder.Companion.NumberOfVariablesInSurfaceArray
import io.github.nvlad1.function3danimator.model.FunctionSurfaceBuilder.Companion.NumberOfVariablesInSurfaceGridArray
import io.github.nvlad1.function3danimator.utils.FLAG_LIGHTING
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

internal class OpenGLRenderer(
    private val context: Context,
    private val showGrid: Boolean,
    private val renderState: FunctionRenderState
) : GLSurfaceView.Renderer {
    private val funcSurfaces: ArrayList<FunctionSurfaceBuilder> = ArrayList()
    private lateinit var vertexData: FloatBuffer
    private lateinit var normalsData: FloatBuffer
    private var uColorLocation = 0
    private var aPositionLocation = 0
    private var aNormalLocation = 0
    private var uMatrixLocation = 0
    private var programId = 0

    //private float[] mProjectionMatrix = new float[16];
    //private float[] mViewMatrix = new float[16];
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mAccumulatedRotation = FloatArray(16)
    private val mRotationMatrixX = FloatArray(16)
    private val mRotationMatrixY = FloatArray(16)
    private val mRotationMatrixZ = FloatArray(16)
    private val mRotationMatrix_result1 = FloatArray(16)
    private val mRotationMatrix_result2 = FloatArray(16)
    private val mTemporaryMatrix = FloatArray(16)
    private val mMatrix = FloatArray(16)
    private val axis_length = 3f
//    private val vertices_axes = floatArrayOf(
//            -axis_length, 0f, 0f,
//            axis_length, 0f, 0f, 0f, -axis_length, 0f, 0f, axis_length, 0f, 0f, 0f, -axis_length, 0f, 0f, axis_length)
    private var lastWidth = 0
    private var lastHeight = 0
    var zoomRatio = 1.0f
        private set

    @Volatile
    var angleX = 0f

    @Volatile
    var angleY = 0f

    @Volatile
    var angleZ = 0f

    override fun onSurfaceCreated(arg0: GL10, arg1: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        val vertexShaderId = ShaderUtils.createShader(context, GLES20.GL_VERTEX_SHADER,
            if (FLAG_LIGHTING) R.raw.vertex_shader_lighting else R.raw.vertex_shader)
        val fragmentShaderId = ShaderUtils.createShader(context, GLES20.GL_FRAGMENT_SHADER,
            if (FLAG_LIGHTING) R.raw.fragment_shader_lighting else R.raw.fragment_shader)
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId)
        GLES20.glUseProgram(programId)

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0)
        createViewMatrix()
        prepareData()
        bindData()
        setZoom(renderState.border)
    }

    override fun onSurfaceChanged(arg0: GL10, width: Int, height: Int) {
        if (width == 0 || height == 0) return
        GLES20.glViewport(0, 0, width, height)
        createProjectionMatrix(width, height)
        bindMatrix()
    }

    private fun prepareData() {
        for (i in 0 until renderState.functionsNumber) {
            funcSurfaces.add(FunctionSurfaceBuilder(i, renderState))
        }
        val axisVertices = generateAxisVertices()
        vertexData = ByteBuffer
                .allocateDirect(axisVertices.size * 4 + NumberOfVariablesInSurfaceArray * 4 * renderState.functionsNumber + NumberOfVariablesInSurfaceGridArray * 4 * renderState.functionsNumber)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        if (FLAG_LIGHTING) {
            normalsData = ByteBuffer
                .allocateDirect(axisVertices.size * 4 + NumberOfVariablesInSurfaceArray * 4 * renderState.functionsNumber + NumberOfVariablesInSurfaceGridArray * 4 * renderState.functionsNumber)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        }
    }

    private fun recalculateData() {
        vertexData.clear()
        val axisVertices = generateAxisVertices()
        vertexData.put(axisVertices)
        if (FLAG_LIGHTING) {
            normalsData.clear()
            normalsData.put(FloatArray(axisVertices.size) { 0f })
        }
        for (i in 0 until renderState.functionsNumber) {
            funcSurfaces[i].recalculateSurface()
            val vertices = funcSurfaces[i].surfaceVertices
            vertexData.put(vertices)
            if (FLAG_LIGHTING) {
                val verticesNormals = funcSurfaces[i].normalsVertices
                normalsData.put(verticesNormals)
            }
            //            vertexData.rewind();
        }
        if (showGrid) {
            for (i in 0 until renderState.functionsNumber) {
                funcSurfaces[i].recalculateSurfaceGrid()
                val vertices = funcSurfaces[i].surfaceGridVertices
                vertexData.put(vertices)
                if (FLAG_LIGHTING) {
                    normalsData.put(FloatArray(vertices.size) { 0f })
                }
                //            vertexData.rewind();
            }
        }
        vertexData.rewind()
        if (FLAG_LIGHTING) {
            normalsData.rewind()
        }
    }

    private fun bindData() {
        aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position")
        vertexData.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GLES20.GL_FLOAT,
                false, 0, vertexData)
        GLES20.glEnableVertexAttribArray(aPositionLocation)
        if (FLAG_LIGHTING) {
            aNormalLocation = GLES20.glGetAttribLocation(programId, "a_Normal")
            normalsData.position(0)
            GLES20.glVertexAttribPointer(
                aNormalLocation, POSITION_COUNT, GLES20.GL_FLOAT,
                false, 0, normalsData
            )
            GLES20.glEnableVertexAttribArray(aNormalLocation)
        }
        uColorLocation = GLES20.glGetUniformLocation(programId, "u_Color")
        uMatrixLocation = GLES20.glGetUniformLocation(programId, "u_Matrix")
    }

    private fun createProjectionMatrix(width: Int, height: Int) {
        lastWidth = width
        lastHeight = height
        var ratio = 1.0f
        var left = -1.0f * zoomRatio
        var right = 1.0f * zoomRatio
        var bottom = -1.0f * zoomRatio
        var top = 1.0f * zoomRatio
        val near = 2.0f * zoomRatio
        val far = 8.0f * zoomRatio
        if (width > height) {
            ratio = width.toFloat() / height
            left *= ratio
            right *= ratio
        } else {
            ratio = height.toFloat() / width
            bottom *= ratio
            top *= ratio
        }
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far)
    }

    fun recreateProjectionMatrix(){
        createProjectionMatrix(lastWidth, lastHeight)
        bindMatrix()
    }

    private fun createViewMatrix() {

        // С‚РѕС‡РєР° РїРѕР»РѕР¶РµРЅРёСЏ РєР°РјРµСЂС‹
        val eyeX = 4f * zoomRatio
        val eyeY = 4f * zoomRatio
        val eyeZ = 2f * zoomRatio

        // С‚РѕС‡РєР° РЅР°РїСЂР°РІР»РµРЅРёСЏ РєР°РјРµСЂС‹
        val centerX = 0f
        val centerY = 0f
        val centerZ = 0f

        // up-РІРµРєС‚РѕСЂ
        val upX = 0f
        val upY = 0f
        val upZ = 1f
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }

    private fun bindMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mRotationMatrixX, 0)
        Matrix.setIdentityM(mRotationMatrixY, 0)
        Matrix.setIdentityM(mRotationMatrixZ, 0)
        Matrix.setRotateM(mRotationMatrixX, 0, angleX, 1f, 0f, 0f)
        Matrix.setRotateM(mRotationMatrixY, 0, angleY, 0f, 1f, 0f)
        Matrix.setRotateM(mRotationMatrixZ, 0, angleZ, 0f, 0f, 1f)
        Matrix.multiplyMM(mRotationMatrix_result1, 0, mRotationMatrixZ, 0, mRotationMatrixY, 0)
        Matrix.multiplyMM(mRotationMatrix_result2, 0, mRotationMatrix_result1, 0, mRotationMatrixX, 0)
        angleX = 0f
        angleY = 0f
        angleZ = 0f

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mRotationMatrix_result2, 0, mAccumulatedRotation, 0)
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16)
        Matrix.multiplyMM(mMatrix, 0, mMVPMatrix, 0, mAccumulatedRotation, 0)

        //glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)
    }

    private fun generateAxisVertices(): FloatArray{
        val length = axis_length * zoomRatio
        val arrow = length / 20
        return floatArrayOf(-length, 0f, 0f, length, 0f, 0f, //axis X
            length, 0f, 0f, length - arrow, - arrow, 0f, length, 0f, 0f, length - arrow, arrow, 0f, //arrows X axis
            0f, -length, 0f, 0f, length, 0f, //axis Y
            0f, length, 0f, 0f, length - arrow, arrow, 0f, length, 0f, 0f, length - arrow, -arrow, //arrows Y axis
            0f, 0f, -length, 0f, 0f, length, //axis Z
            0f, 0f, length, arrow, 0f, length - arrow, 0f, 0f, length, -arrow, 0f, length - arrow, //arrows Z axis
        )
    }

    override fun onDrawFrame(arg0: GL10) {
        val scratch = FloatArray(16)
        recalculateData()
        createViewMatrix()
        bindMatrix()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)


        // РѕСЃРё
        GLES20.glLineWidth(1f)
        GLES20.glUniform3f(uColorLocation, 0.0f, 1.0f, 1.0f) // X -- cyan
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6)
        GLES20.glUniform3f(uColorLocation, 1.0f, 0.0f, 1.0f) // Y -- violet
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 6)
        GLES20.glUniform3f(uColorLocation, 1.0f, 0.5f, 0.0f) // Z -- orange
        GLES20.glDrawArrays(GLES20.GL_LINES, 12, 6)

        var currentPointID = 18
        for (i in 0 until renderState.functionsNumber) {
            val color = ContextCompat.getColor(context, (renderState.getFunction(i)?.color ?: EnumColor.RED).colorResId)
            val v = (color shr 16 and 0xff).toFloat() / 256.0f
            val v1 = (color shr 8 and 0xff).toFloat() / 256.0f
            val v2 = (color and 0xff).toFloat() / 256.0f
            GLES20.glUniform3f(uColorLocation, v, v1, v2)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, currentPointID, NumberOfVariablesInSurfaceArray / 3)
            currentPointID += NumberOfVariablesInSurfaceArray / 3
        }
        for (i in 0 until renderState.functionsNumber) {
            GLES20.glUniform3f(uColorLocation, 1.0f, 1.0f, 1.0f)
            GLES20.glDrawArrays(GLES20.GL_LINES, currentPointID, NumberOfVariablesInSurfaceGridArray / 3)
            currentPointID += NumberOfVariablesInSurfaceGridArray / 3
        }
    }

    fun setZoom(newZoom: Float){
        zoomRatio = newZoom.coerceAtLeast(1e-20f)
    }

    companion object {
        private const val POSITION_COUNT = 3
        private const val TIME: Long = 10000
    }

}
