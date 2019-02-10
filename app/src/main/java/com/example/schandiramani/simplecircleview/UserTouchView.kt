package com.example.schandiramani.simplecircleview

import android.content.Context
import android.graphics.*
import android.hardware.SensorEvent
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import java.util.*

/**
 * Created by schandiramani on 2/20/18.
 */

class UserTouchView : View, GestureDetector.OnGestureListener {

    private var circle_IsTouchedDown: Boolean = false
    private var newCircle_IsTouchedDown: Boolean = false
    internal var touchedInCircle: Circle? = null
    private var circleIsMoving: Boolean = false
    private var circleIslongPressed: Boolean = false

    private var gestureDetector: GestureDetector? = null
    private var circleArrayList: ArrayList<Circle>? = null


    private var totalScreenWidth: Float = 0f
    private var totalScreenHeight: Float = 0f
    private var circle_OldXPoint: Float = 0f
    private var circle_OldYPoint: Float = 0f

    private var circleScale = 1.2f

    internal var circleLastModifiedTimestamp: Long = 0

    private var xCoordinate:Float = 0f
    private var yCoordinate:Float = 0f
    private var xVelocity :Float = 0f
    private var yVelocity :Float = 0f
    private var radius :Double = 0.0

    private val statusBarHeight: Int
        get() {
            var getResult = 0
            val resourceIdentifier = resources.getIdentifier("statusBarHeight", "dimen", "android")
            if (resourceIdentifier > 0) {
                getResult = resources.getDimensionPixelSize(resourceIdentifier)
            }
            return getResult
        }

    companion object {
        private val circle_Radius = 90
        private val circle_Speed = 140
        private var circlePaint: Paint? = null
        init {
            circlePaint = Paint()
            circlePaint!!.color = Color.BLUE
        }
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)

    }

    private fun init(context: Context) {
        circleArrayList = ArrayList()
        gestureDetector = GestureDetector(context, this)
        gestureDetector!!.setIsLongpressEnabled(false)
        setScreenSize(context)
    }

    private fun setScreenSize(context: Context) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)

        totalScreenWidth = point.x.toFloat()
        totalScreenHeight = (point.y - statusBarHeight).toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector!!.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onActionDown(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE -> {
                onActionMove(event.x, event.y)
            }

            MotionEvent.ACTION_UP -> {
                onActionUp()
            }
        }
        return true
    }

    private fun onActionDown(x:Float, y:Float)
    {
        circle_IsTouchedDown = true

        touchedInCircle = getTouchedCircle(x, y)
        circle_OldXPoint = x
        circle_OldYPoint = y

        val circle = ifMovingStopCircle(x, y)

        if (circle != null) {
            touchedInCircle = circle
        }

        if (!checkTouchingWindowEdge(x, y, circle_Radius.toDouble()) && getTouchedCircle(x, y) == null)
        {
            circleArrayList!!.add(Circle(x, y, circle_Radius, Circle.CircleMotionType.OnTouch))
            circle_IsTouchedDown = true
            newCircle_IsTouchedDown = true
        }
        else {
            newCircle_IsTouchedDown = false
        }
    }

    private fun onActionMove(x:Float, y:Float)
    {
        if (circle_OldXPoint - x != 0f || circle_OldYPoint - y != 0f) {
            circle_OldXPoint = x
            circle_OldYPoint = y

            if (newCircle_IsTouchedDown) {
                touchedInCircle = circleArrayList!![circleArrayList!!.size - 1]
                touchedInCircle!!.setMotionType(Circle.CircleMotionType.OnMove)

                if (!checkTouchingXWindowEdge(x, touchedInCircle!!.getRadius())) {
                    touchedInCircle!!.setXPosition(x)
                }
                if (!checkTouchingYWindowEdge(y, touchedInCircle!!.getRadius())) {
                    touchedInCircle!!.setYPosition(y)
                }
                circleIsMoving = true
            }

            if (circle_IsTouchedDown && touchedInCircle != null && !touchedInCircle!!.IsMoving()) {
                if (touchedInCircle!!.getMotionType() === Circle.CircleMotionType.OnStop) {
                    touchedInCircle!!.setXPosition(x)
                    touchedInCircle!!.setYPosition(y)
                    touchedInCircle!!.setMotionType(Circle.CircleMotionType.None)
                    circleIsMoving = true
                } else {
                    if (Math.abs(Math.abs(touchedInCircle!!.getXPosition()) - Math.abs(x)) > 1) {
                        if (!checkTouchingXWindowEdge(x, touchedInCircle!!.getRadius())) {
                            touchedInCircle!!.setXPosition(x)
                            touchedInCircle!!.setMotionType(Circle.CircleMotionType.None)
                            circleIsMoving = true
                        }
                    }
                    if (Math.abs(Math.abs(touchedInCircle!!.getYPosition()) - Math.abs(y)) > 1) {
                        if (!checkTouchingYWindowEdge(y, touchedInCircle!!.getRadius())) {
                            touchedInCircle!!.setYPosition(y)
                            touchedInCircle!!.setMotionType(Circle.CircleMotionType.None)
                            circleIsMoving = true
                        }
                    }
                }
            }
        }
    }

    private fun onActionUp()
    {
        circleIslongPressed = false
        circle_IsTouchedDown = false

        if (touchedInCircle != null && newCircle_IsTouchedDown && touchedInCircle!!.getMotionType() === Circle.CircleMotionType.OnMove) {
            touchedInCircle!!.setMotionType(Circle.CircleMotionType.None)
        }

        touchedInCircle = null
        newCircle_IsTouchedDown = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (circle in circleArrayList!!) {

            if (circle.getMotionType() === Circle.CircleMotionType.OnTouch) {
                onCircleMotionTypeTouch(canvas,circle)
            }
            else if (circle.getMotionType() === Circle.CircleMotionType.None || circle.getMotionType() === Circle.CircleMotionType.OnStop) {
                onCircleMotionTypeNoneOrStop(canvas,circle)
            } else if (circle.getMotionType() === Circle.CircleMotionType.OnMove) {
                onCircleMotionTypeMove(canvas,circle)
            } else if (circle.getMotionType() === Circle.CircleMotionType.OnLongPress) {
                onCircleMotionTypeLongPress(canvas,circle)
            }
            else if (circle.getMotionType() === Circle.CircleMotionType.OnFling) {
                onCircleMotionTypeFling(canvas,circle)
            }
        }

        if (circleScale < 5 && circleIslongPressed) {
            circleScale = circleScale + 0.005f
        }

        postInvalidateDelayed((1000 / circle_Speed).toLong())
    }

    private fun onCircleMotionTypeNoneOrStop(canvas: Canvas, circle : Circle){
        if (circle.getScale() > 1.2f) {
            canvas.save()
            canvas.scale(circle.getScale(), circle.getScale(), circle.getXPosition(), circle.getYPosition())
            canvas.drawCircle(circle.getXPosition(), circle.getYPosition(), circle.getRadius().toFloat(), circlePaint!!)
            circle.setRadius(circle.getRadius() * circle.getScale())
            circle.setScale(0.0f)
            canvas.restore()
        } else {
            canvas.drawCircle(circle.getXPosition(), circle.getYPosition(), circle.getRadius().toFloat(), circlePaint!!)
        }
    }

    private fun onCircleMotionTypeTouch(canvas: Canvas, circle : Circle){
        canvas.drawCircle(circle.getXPosition(), circle.getYPosition(), circle.getRadius().toFloat(), circlePaint!!)
    }

    private fun onCircleMotionTypeMove(canvas: Canvas, circle : Circle){
        if (circleScale > 1.2f) {
            circle.setScale(circleScale)
            circleIslongPressed = false
        }
        if (circle.getScale() > 1.2f) {
            canvas.save()
            canvas.scale(circle.getScale(), circle.getScale(), circle.getXPosition(), circle.getYPosition())
            canvas.drawCircle(circle.getXPosition(), circle.getYPosition(), circle.getRadius().toFloat(), circlePaint!!)
            circle.setRadius(circle.getRadius() * circle.getScale())
            circle.setScale(0.0f)
            canvas.restore()
        } else {
            canvas.drawCircle(circle.getXPosition(), circle.getYPosition(), circle.getRadius().toFloat(), circlePaint!!)
        }
        circleScale = 1.2f
    }

    private fun onCircleMotionTypeLongPress(canvas: Canvas, circle : Circle){
        if (circle_IsTouchedDown == false) {
            canvas.drawCircle(circle.getXPosition(), circle.getYPosition(), circle.getRadius().toFloat(), circlePaint!!)
            circle.setMotionType(Circle.CircleMotionType.None)
            circle.setScale(circleScale)
            circleScale = 1.2f
        } else {
            if (circle.getScale() > 1.2f && circleScale <= 1.2f) {
                circleScale = circle.getScale()
            }
            canvas.save()
            canvas.scale(circleScale, circleScale, circle.getXPosition(), circle.getYPosition())
            canvas.drawCircle(circle.getXPosition(), circle.getYPosition(), circle.getRadius().toFloat(), circlePaint!!)
            canvas.restore()
        }
    }

    private fun onCircleMotionTypeFling(canvas: Canvas, circle : Circle){
        checkCircleTouchesEdge(circle)
        canvas.save()
        increaseVelocityAndEndPosition(circle)

        if (circle.getScale() > 1.2f) {
            canvas.scale(circle.getScale(), circle.getScale(), circle.getXPosition(), circle.getYPosition())
        }

        canvas.concat(circle.getMatrix())
        circle.setIsMoving(true)
        canvas.drawCircle(circle.getXPosition(), circle.getYPosition(), circle.getRadius().toFloat(), circlePaint!!)
        canvas.restore()
    }

    private fun checkCircleTouchesEdge(circle:Circle) {
        xCoordinate = circle.getXCoordinate()
        yCoordinate = circle.getYCoordinate()
        xVelocity = circle.getXVelocity()
        yVelocity = circle.getYVelocity()

        radius = circle.getRadius()

        if (Math.abs(xVelocity) < 0.01) {
            xVelocity = 0f
        }
        else if (Math.abs(yVelocity) < 0.01) {
            yVelocity = 0f
        }
        else {
            if (xCoordinate + radius > totalScreenWidth - 1 && xVelocity > 0) {
                xVelocity = (-xVelocity * 0.995).toFloat()
            }
            if (yCoordinate + radius > totalScreenHeight - 1 && yVelocity > 0) {
                yVelocity = (-yVelocity * 0.995).toFloat()

            }
            if (circle.IsMoving()) {
                if (xCoordinate - radius < 1 && xVelocity < 0) {
                    xVelocity = (-xVelocity * 0.995).toFloat()
                }
                if (yCoordinate - radius < 1 && yVelocity < 0) {
                    yVelocity = (-yVelocity * 0.995).toFloat()
                }
            }
        }
    }

    private fun increaseVelocityAndEndPosition(circle:Circle){
        circle.getMatrix()!!.postTranslate(xVelocity * 1 / circle_Speed, yVelocity * 1 / circle_Speed)

        this.xCoordinate = getXCoordinate(circle, xCoordinate, xVelocity)

        this.yCoordinate = getYCoordinate(circle, yCoordinate, yVelocity)

        if (circle.getXEndPosition() == 0.0f) {
            circle.setXEndPosition(circle.getXPosition() + xVelocity * 1 / circle_Speed)
        }
        else {
            circle.setXEndPosition(circle.getXEndPosition() + xVelocity * 1 / circle_Speed)
        }

        if (circle.getYEndPosition() == 0.0f) {
            circle.setYEndPosition(circle.getYPosition() + yVelocity * 1 / circle_Speed)
        } else {
            circle.setYEndPosition(circle.getYEndPosition() + yVelocity * 1 / circle_Speed)
        }
        circle.setXCoordinate(xCoordinate)
        circle.setYCoordinate(yCoordinate)
        circle.setXVelocity(xVelocity)
        circle.setYVelocity(yVelocity)
    }

    private fun getXCoordinate(circle: Circle, xCoordinate: Float, xVelocity: Float): Float {
        var xCoordinate = xCoordinate
        if (xCoordinate == 0f) {
            xCoordinate = circle.getXPosition() + xVelocity * 1.1f / circle_Speed
        } else {
            xCoordinate += xVelocity * 1.1f / circle_Speed
        }
        return xCoordinate
    }

    private fun getYCoordinate(circle: Circle, yCoordinate: Float, yVelocity: Float): Float {
        var yCoordinate = yCoordinate
        if (yCoordinate == 0f) {
            yCoordinate = circle.getYPosition() + yVelocity * 1.1f / circle_Speed
        } else {
            yCoordinate += yVelocity * 1.1f / circle_Speed
        }
        return yCoordinate
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(event: MotionEvent) {
        onLongPressed(event)
    }

    override fun onLongPress(e: MotionEvent) {
        onLongPressed(e)
    }

    private fun onLongPressed(event: MotionEvent){
        if (circleArrayList!!.isEmpty() && !checkTouchingWindowEdge(event.x, event.y, circle_Radius.toDouble())) {
            circleArrayList!!.add(Circle(event.x, event.y, circle_Radius, Circle.CircleMotionType.OnLongPress))
            circleIslongPressed = true
        }
        else {
            var isInCircle = false
            for (circle in circleArrayList!!) {
                if (circle.getMotionType() !== Circle.CircleMotionType.OnFling && checkIsTouchedWithinCircle(circle.getXPosition(), circle.getYPosition(), event.x, event.y, circle.getRadius())) {
                    if (newCircle_IsTouchedDown) {
                        circle.setMotionType(Circle.CircleMotionType.OnLongPress)
                        circleIslongPressed = true
                    }
                    isInCircle = true
                }
            }

            if (isInCircle == false && checkTouchingWindowEdge(event.x, event.y, circle_Radius.toDouble()) == false) {
                circleArrayList!!.add(Circle(event.x, event.y, circle_Radius, Circle.CircleMotionType.OnLongPress))
            }
            circleIslongPressed = true
        }
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (circleIsMoving) {
            circleIsMoving = false
        }
        setVelocityOnFling(e1.x, e1.y, velocityX, velocityY)
        return true
    }

    private fun setVelocityOnFling(x: Float, y: Float, velocityX: Float, velocityY: Float) {
        for (circle in circleArrayList!!) {
            if (checkIsTouchedWithinCircle(circle.getXPosition(), circle.getYPosition(), x, y, circle.getRadius())) {
                if (!circle.IsMoving()) {
                    newCircle_IsTouchedDown = false

                    circle.setXVelocity(velocityX)
                    circle.setYVelocity(velocityY)
                    val matrix = Matrix()
                    circle.setMatrix(matrix)
                    circle.setMotionType(Circle.CircleMotionType.OnFling)
                    return
                }
            }
        }
    }

    fun onSensorChanged(event: SensorEvent) {
        if (circleLastModifiedTimestamp == 0L) {
            circleLastModifiedTimestamp = event.timestamp
            return
        }

        val timeDelta = event.timestamp - circleLastModifiedTimestamp
        val xAcceleration = Math.round(event.values[0] * 100) / 100.0f
        val yAcceleration = Math.round(event.values[1] * 100) / 100.0f
        val timeDeltaSeconds = (timeDelta / 1000000000.0f).toDouble()

        circleLastModifiedTimestamp = event.timestamp

        for (circle in circleArrayList!!) {
            if (circle.IsMoving()) {
                decreaseVelocity(circle, (circle.getXVelocity() - 6.5 * xAcceleration.toDouble() * (timeDelta / 1000000000.0f).toDouble()).toFloat(),
                        (circle.getYVelocity() + 6.5 * yAcceleration.toDouble() * timeDeltaSeconds).toFloat())
            }
        }
    }

    private fun decreaseVelocity(circle: Circle, xVelocity: Float, yVelocity: Float) {
        var xVelocity = xVelocity
        var yVelocity = yVelocity
        xVelocity = (xVelocity * 0.950).toFloat()
        yVelocity = (yVelocity * 0.950).toFloat()

        if (Math.abs(xVelocity) < 0.01) xVelocity = 0f
        if (Math.abs(yVelocity) < 0.01) yVelocity = 0f

        circle.setXVelocity(xVelocity)
        circle.setYVelocity(yVelocity)
    }


    private fun checkTouchingWindowEdge(x: Float, y: Float, radius: Double): Boolean {
        if (x + radius > totalScreenWidth - 1 || y + radius > totalScreenHeight - 1 || x - radius < 1 || y - radius < 1) {
            Toast.makeText(context, "Touching window edge", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    private fun checkTouchingXWindowEdge(x: Float, radius: Double): Boolean {
        if (x + radius > totalScreenWidth - 1 || x - radius < 1)
        {
            return true
        }
        return false

    }

    private fun getTouchedCircle(x: Float, y: Float): Circle? {
        for (circle in circleArrayList!!) {
            if (!circle.IsMoving() && (circle.getXPosition() - x) * (circle.getXPosition() - x) + (circle.getYPosition() - y) * (circle.getYPosition() - y) <= circle.getRadius() * circle.getRadius()) {
                return circle
            }
        }
        return null
    }

    private fun checkTouchingYWindowEdge(y: Float, radius: Double): Boolean {
        if (y + radius > totalScreenHeight - 1 || y - radius < 1)
        {
            return true
        }
        return false
    }

    private fun checkIsTouchedWithinCircle(centerX: Float, centerY: Float, xPoint: Float, yPoint: Float, radius: Double): Boolean {
        if ((centerX - xPoint) * (centerX - xPoint) + (centerY - yPoint) * (centerY - yPoint) <= radius * radius)
        {
            return true
        }
        return false
    }


    private fun ifMovingStopCircle(x: Float, y: Float): Circle? {
        for (circle in circleArrayList!!) {
            if (circle.IsMoving() && (circle.getXEndPosition() - x) * (circle.getXEndPosition() - x) + (circle.getYEndPosition() - y) * (circle.getYEndPosition() - y) <= circle.getRadius() * circle.getRadius()) {
                circle.setIsMoving(false)
                circle.setXPosition(circle.getXEndPosition())
                circle.setYPosition(circle.getYEndPosition())
                circle.setMotionType(Circle.CircleMotionType.OnStop)
                circle.setMatrix(null)
                circle.setXEndPosition(0.0f)
                circle.setYEndPosition(0.0f)
                circle.setXCoordinate(0.0f)
                circle.setYCoordinate(0.0f)
                circle.setXVelocity(0.0f)
                circle.setYVelocity(0.0f)
                return circle
            }
        }
        return null
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }
}





