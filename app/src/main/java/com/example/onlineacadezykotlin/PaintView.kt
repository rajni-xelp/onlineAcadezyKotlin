package com.example.onlineacadezykotlin

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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
    lateinit var bitmapList:ArrayList<UploadedImageDetails>
    init {
        setUpPainting();
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

    fun paintPoint(x:Float,y:Float)
    {
        canvas.drawPoint(x,y,otherPaint)
        invalidate()
    }

    fun clearCanvas()
    {
        penPath.reset()
        canvas.drawColor(0,PorterDuff.Mode.CLEAR)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    fun putImage(imageDataModelList:List<ImageDataModel>,image:Bitmap)
    {
        val uploadedImageDetails:UploadedImageDetails= UploadedImageDetails();
        val resizedBitmap:Bitmap=getResizedBitmap(image,width.toFloat(),StaticClass.matchParentHeight.toFloat())
        uploadedImageDetails.setBitmap(resizedBitmap)
        bitmapList.add(uploadedImageDetails)
        if(bitmapList.size>1)
            marginFromTop=marginFromTop + bitmapList.get(bitmapList.size-2).getBitmap().height+50
           uploadedImageDetails.setHeightFromTop(marginFromTop)
            invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for(i in 0 .. (bitmapList.size-1))
        {
            canvas?.scale(mScaleFactor,mScaleFactor)
            canvas?.drawBitmap(bitmapList.get(i).getBitmap(),(width-bitmapList.get(i).bitmap.width)/2.toFloat(),bitmapList.get(i).heightFromTop.toFloat(),null)
        }
        canvas?.drawPath(penPath,penPaint)
        canvas?.drawBitmap(bitmap,0F,0F,canvasPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleDetector.onTouchEvent(event)
        val action: Int = event!!.action
        val touchPoint = PointF();
        touchPoint.set(event.x, event.y)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x: Float = event.x
                val y: Float = event.y

                mLastTouchX = x
                mLastTouchY = y
                mActivePointerId = event.getPointerId(0)
                penPath.moveTo(touchPoint.x, touchPoint.y)
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(mActivePointerId)
                val x: Float = event.getX(pointerIndex)
                val y: Float = event.getY(pointerIndex)

                if (!mScaleDetector.isInProgress) {
                    if (longPressed == 1) {
                        val dxnew: Float = x - mLastTouchX
                        val dynew: Float = y - mLastTouchY

                        // imageDataModelList.get(0).setAmountX(imageDataModelList.get(0).getAmountX()+dxnew);
                        //  imageDataModelList.get(0).setAmountY(imageDataModelList.get(0).getAmountY()+dynew);
                        invalidate()

                    } else {
                    if(StaticClass.draw)
                        penPath.lineTo(touchPoint.x, touchPoint.y)
                    }
                }
                mLastTouchX = x
                mLastTouchY = y
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = -1
                canvas.drawPath(penPath, penPaint)
            }

            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = -1
            }
        }
        invalidate()
        return true
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            mScaleFactor = detector?.scaleFactor!!
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))
            return true
        }
    }

    fun getResizedBitmap(bitmap : Bitmap , reqWidth :Float,reqHeight :Float) : Bitmap
    {
        var matrix=Matrix();
        var src:RectF=RectF(0F, 0F, bitmap.width.toFloat(),bitmap.height.toFloat())
        var req:RectF=RectF(0F, 0F,reqWidth,reqHeight)
        matrix.setRectToRect(src,req,Matrix.ScaleToFit.CENTER)
        return Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
    }

}