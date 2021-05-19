package com.example.aop_part4_chapter01

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

class CustomMotionLayout (context: Context, attributeSet: AttributeSet) : MotionLayout(context, attributeSet){

    private var motionTouchStarted :Boolean = false
    private val mainContainerLayout : View by lazy {
        findViewById(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    private val gestureListener by lazy {
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                mainContainerLayout.getHitRect(hitRect)
                return hitRect.contains(e1.x.toInt(), e1.y.toInt())
            }
        }
    }

    private val gestureDetertor by lazy {
        GestureDetector(context, gestureListener)
    }

    init {
        setTransitionListener(object :TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // ACTION_MOVE -> 이동
        // ACTION_UP -> 마우스를 누르고 뗀 경우
        // ACTION_DOWN -> 마우스를 누른 경우
        //반환값이 True면 이벤트를 더 이상 전달하지 않는다.

        when(event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                //원래 터치 이벤트 리턴
                return super.onTouchEvent(event)
            }
        }

        if(!motionTouchStarted) {
            mainContainerLayout.getHitRect(hitRect)
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
        }

        return super.onTouchEvent(event) && motionTouchStarted

    }


    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        // 반환값이 True면 OnTouchEvent로 전달
        // false 면 dispatchTouchEvent() 호출
        // GestureDetector 이용
        return gestureDetertor.onTouchEvent(event)
    }
}