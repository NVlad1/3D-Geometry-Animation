package io.github.nvlad1.function3danimator.openGLutils

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import kotlin.math.sqrt

/**
 * Created by Naboka Vladislav on 02.10.2017.
 */
class GLSurfaceViewWithRotation(
    context: Context?,
    showGrid: Boolean,
    renderState: FunctionRenderState
) : GLSurfaceView(context) {

    private val TAG = GLSurfaceViewWithRotation::class.java.simpleName
    private var mPreviousX: Float? = null
    private var mPreviousY: Float? = null
    private val mRenderer: OpenGLRenderer
    private var doubleTapStarted = false

    private var prevX1 = 0f
    private var prevX2 = 0f
    private var prevY1 = 0f
    private var prevY2 = 0f
    private var pointerId1 = 0
    private var pointerId2 = 0
    private var initialDist = 0f
    private var initZoomRatio = 1.0f

    init {
        setEGLContextClientVersion(2)
        mRenderer = OpenGLRenderer(context!!, showGrid, renderState)
        setRenderer(mRenderer)

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        //if (e.pointerCount > 1){
            //doubleTapStarted = true
            //return true
        //}
        when (e.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {
                if (doubleTapStarted && e.pointerCount > 1){
                    processZoom(e)
                } else if (!doubleTapStarted){
                    processPan(e.x, e.y)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                doubleTapStarted = true
                initZoom(e)
            }
            MotionEvent.ACTION_UP -> {
                doubleTapStarted = false
                mPreviousX = null
                mPreviousY = null
            }
        }

        return true
    }

    private fun processPan(x: Float, y: Float){
        if (mPreviousX == null) mPreviousX = x
        if (mPreviousY == null) mPreviousY = y
        val dx = x - mPreviousX!!
        val dy = y - mPreviousY!!

        mRenderer.angleX = mRenderer.angleX - dy/sqrt(2f) * TOUCH_SCALE_FACTOR * TOUCH_SCALE_FACTOR_MULTIPLIER
        mRenderer.angleY = mRenderer.angleY + dy/sqrt(2f) * TOUCH_SCALE_FACTOR * TOUCH_SCALE_FACTOR_MULTIPLIER
        mRenderer.angleZ = mRenderer.angleZ + dx * TOUCH_SCALE_FACTOR * TOUCH_SCALE_FACTOR_MULTIPLIER
        requestRender()
        mPreviousX = x
        mPreviousY = y
    }

    private fun initZoom(e: MotionEvent){
        Log.d(TAG, "init zoom")
        if (e.pointerCount < 2) return
        pointerId1 = e.getPointerId(0)
        pointerId2 = e.getPointerId(1)
        val index1 = e.findPointerIndex(pointerId1)
        val index2 = e.findPointerIndex(pointerId2)
        prevX1 = e.getX(index1)
        prevY1 = e.getY(index1)
        prevX2 = e.getX(index2)
        prevY2 = e.getY(index2)
        initialDist = sqrt((prevX1-prevX2)*(prevX1-prevX2)+(prevY1-prevY2)*(prevY1-prevY2))
        initZoomRatio = mRenderer.zoomRatio
    }

    private fun processZoom(e: MotionEvent){
        val index1 = e.findPointerIndex(pointerId1)
        val index2 = e.findPointerIndex(pointerId2)
        if (index1 < 0 || index2 < 0) return
        val x1 = e.getX(index1)
        val y1 = e.getY(index1)
        val x2 = e.getX(index2)
        val y2 = e.getY(index2)
        val dist = sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2))
        //val diff = (dist - initialDist) * ZOOM_SCALE_FACTOR * ZOOM_SCALE_FACTOR_MULTIPLIER
        val diff = initialDist/dist
        mRenderer.setZoom(initZoomRatio * diff)
        mRenderer.recreateProjectionMatrix()
    }

    companion object{
        private val TOUCH_SCALE_FACTOR = 80.0f / 320
        private val ZOOM_SCALE_FACTOR = 1.0f
        val TOUCH_SCALE_FACTOR_MULTIPLIER = 1.0f
        val ZOOM_SCALE_FACTOR_MULTIPLIER = 1.0f
    }
}
