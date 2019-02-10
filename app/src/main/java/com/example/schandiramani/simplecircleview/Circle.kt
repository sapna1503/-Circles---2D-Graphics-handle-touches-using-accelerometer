package com.example.schandiramani.simplecircleview
import android.graphics.Matrix

/**
 * Created by schandiramani on 2/20/18.
 */

class Circle(x: Float, y: Float, radius: Int, motionType: CircleMotionType) {

    private var circle_XPosition: Float = 0.0f
    private var circle_YPosition: Float = 0.toFloat()
    private var circle_XEndPosition: Float = 0.toFloat()
    private var circle_YEndPosition: Float = 0.toFloat()
    private var circleRadius: Double = 0.toDouble()
    private var circle_XVelocity: Float = 0.toFloat()
    private var circle_YVelocity: Float = 0.toFloat()
    private var circle_newXPosition: Float = 0.toFloat()
    private var circle_newYPosition: Float = 0.toFloat()
    private var circle_Matrix: Matrix? = null
    private var circle_Scale: Float = 0.toFloat()
    private var circle_IsMoving: Boolean = false
    private var circle_MotionType: CircleMotionType? = null

    init {
        circle_XPosition = x
        circle_YPosition = y
        circleRadius = radius.toDouble()
        circle_MotionType = motionType
    }

    enum class CircleMotionType {
        OnStop,
        OnTouch,
        OnMove,
        OnLongPress,
        OnFling,
        None
    }

    fun getXCoordinate(): Float {
        return circle_newXPosition
    }

    fun setXCoordinate(xtranlate: Float) {
        this.circle_newXPosition = xtranlate
    }

    fun getYCoordinate(): Float {
        return circle_newYPosition
    }

    fun setYCoordinate(yTranslate: Float) {
        this.circle_newYPosition = yTranslate
    }

    fun getScale(): Float {
        return this.circle_Scale

    }

    fun setScale(scale: Float) {
        this.circle_Scale = scale
    }


    fun IsMoving(): Boolean {
        return circle_IsMoving
    }

    fun setIsMoving(isMoving: Boolean) {
        this.circle_IsMoving = isMoving
    }

    fun getMatrix(): Matrix? {
        return circle_Matrix
    }

    fun setMatrix(matrix: Matrix?) {
        this.circle_Matrix = matrix
    }

    fun getXVelocity(): Float {
        return circle_XVelocity
    }

    fun setXVelocity(xVelocity: Float) {
        this.circle_XVelocity = xVelocity
    }

    fun getYVelocity(): Float {
        return circle_YVelocity
    }

    fun setYVelocity(yVelocity: Float) {
        this.circle_YVelocity = yVelocity
    }

    fun getXPosition(): Float {
        return circle_XPosition
    }

    fun setXPosition(xPosition: Float) {
        this.circle_XPosition = xPosition
    }

    fun getYPosition(): Float {
        return circle_YPosition
    }

    fun setYPosition(yPosition: Float) {
        this.circle_YPosition = yPosition
    }

    fun getRadius(): Double {
        return circleRadius
    }

    fun setRadius(radius: Double) {
        this.circleRadius = radius
    }

    fun getXEndPosition(): Float {
        return circle_XEndPosition
    }

    fun setXEndPosition(xPosition: Float) {
        this.circle_XEndPosition = xPosition
    }

    fun getYEndPosition(): Float {
        return circle_YEndPosition
    }

    fun setYEndPosition(yPosition: Float) {
        this.circle_YEndPosition = yPosition
    }

    fun getMotionType(): CircleMotionType? {
        return this.circle_MotionType
    }

    fun setMotionType(type: CircleMotionType) {
        this.circle_MotionType = type
    }


}

