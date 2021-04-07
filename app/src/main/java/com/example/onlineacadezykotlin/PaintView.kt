package com.example.onlineacadezykotlin

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View


class PaintView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    lateinit var mScaleDetector: ScaleGestureDetector
    public var mScaleFactor: Float = 1f

    lateinit var bitmap: Bitmap
    lateinit var canvas: Canvas
    lateinit var penPath: Path;
    lateinit var penPaint: Paint
    lateinit var otherPaint: Paint
    lateinit var canvasPaint: Paint

    var longPressed = 0
    var mActivePointerId = -1;

    var mLastTouchX: Float = 0f
    var mLastTouchY: Float = 0f
    var marginFromTop = 50;

    var mPositionX = 0f;
    var mPositionY:Float = 0f
    var refx = 0f;
    var refy:Float = 0f
    var disablingPencilInterface:DisablingPencilInterface?=null
    lateinit var bitmapList:ArrayList<UploadedImageDetails>
    var statusToEanbleDraw:Int=0
    init {
        setUpPainting()
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
    }

    private fun setUpPainting() {
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        bitmapList=ArrayList()
        penPath = Path()
        penPaint = Paint()
        penPaint.setColor(Color.BLUE)
        penPaint.isAntiAlias = true
        penPaint.strokeWidth = 10f
        penPaint.style = Paint.Style.STROKE
        penPaint.strokeJoin = Paint.Join.ROUND
        penPaint.strokeCap = Paint.Cap.ROUND

        otherPaint = Paint()
        otherPaint.setColor(Color.BLUE)
        otherPaint.isAntiAlias = true
        otherPaint.strokeWidth = 10f
        otherPaint.style = Paint.Style.STROKE
        otherPaint.strokeJoin = Paint.Join.ROUND
        otherPaint.strokeCap = Paint.Cap.ROUND
        this.canvasPaint = Paint(Paint.DITHER_FLAG);
    }

    fun paintPoint(x: Float, y: Float)
    {
        canvas.drawPoint(x, y, otherPaint)
        invalidate()
    }

    fun clearCanvas()
    {
        penPath.reset()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    fun putImage(image: Bitmap, disablingPencilInterface:DisablingPencilInterface , status: Int)
    {
        statusToEanbleDraw=status
        this.disablingPencilInterface=disablingPencilInterface
        val uploadedImageDetails:UploadedImageDetails= UploadedImageDetails();
        val resizedBitmap:Bitmap=getResizedBitmap(
            image,
            width.toFloat(),
            StaticClass.matchParentHeight.toFloat()
        )
        uploadedImageDetails.setBitmap(resizedBitmap)
        bitmapList.add(uploadedImageDetails)
        if(bitmapList.size>1)
            marginFromTop=marginFromTop + bitmapList.get(bitmapList.size - 2).getBitmap().height+50
        uploadedImageDetails.setHeightFromTop(marginFromTop)
        invalidate()
    }

    fun callAfterZoomingImage(position: Int, bitmap: Bitmap?)
    {
        val resizedBitmap:Bitmap=getResizedBitmap(
            bitmap!!,
            width.toFloat(),
            StaticClass.matchParentHeight.toFloat()
        )
        bitmapList.get(position).bitmap=resizedBitmap
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(statusToEanbleDraw==0) {
            for (i in 0..(bitmapList.size - 1)) {
                canvas?.scale(mScaleFactor, mScaleFactor)
                canvas?.drawBitmap(
                    bitmapList.get(i).getBitmap(),
                    (width - bitmapList.get(i).bitmap.width) / 2.toFloat(),
                    bitmapList.get(i).heightFromTop.toFloat(),
                    canvasPaint
                )
            }
            canvas?.drawPath(penPath, penPaint)
            canvas?.drawBitmap(bitmap, 0F, 0F, canvasPaint)
        }
        else {

            canvas?.translate(mPositionX, mPositionY)
            canvas?.scale(mScaleFactor, mScaleFactor)
            if (bitmapList.size > 0)
                canvas?.drawBitmap(
                    bitmapList.get(0).getBitmap(),
                    (width - bitmapList.get(0).bitmap.width) / 2.toFloat(),
                    bitmapList.get(
                        0
                    ).heightFromTop.toFloat(),
                    canvasPaint
                )
            canvas?.drawPath(penPath, penPaint)
            canvas?.drawBitmap(bitmap, 0F, 0F, canvasPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)
        val touchPoint = PointF()
        touchPoint[event.x] = event.y
        when (event.action  and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                refx = event.x
                refy = event.y
                penPath.moveTo(touchPoint.x, touchPoint.y)

            }
            MotionEvent.ACTION_POINTER_DOWN ->
            {
                StaticClass.draw=false
                disablingPencilInterface?.disablePencil()
            }
            MotionEvent.ACTION_MOVE ->
            {
                if (!StaticClass.draw) {
                    val nX = event.x
                    val nY = event.y
                    mPositionX += nX - refx
                    mPositionY += nY - refy
                    refx = nX
                    refy = nY
                    invalidate()
                } else {
                    penPath.lineTo(touchPoint.x, touchPoint.y)
                }
            }
        }
        invalidate()
        return true
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            mScaleFactor *= detector?.scaleFactor!!
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))
            return true
        }
    }

    fun getResizedBitmap(bitmap: Bitmap, reqWidth: Float, reqHeight: Float) : Bitmap
    {
        val matrix=Matrix()
        val src:RectF=RectF(0F, 0F, bitmap.width.toFloat(), bitmap.height.toFloat())
        val req:RectF=RectF(0F, 0F, reqWidth, reqHeight)
        matrix.setRectToRect(src, req, Matrix.ScaleToFit.CENTER)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun returnBitmap() :Bitmap
    {
        return bitmapList.get(0).getBitmap()
    }

}