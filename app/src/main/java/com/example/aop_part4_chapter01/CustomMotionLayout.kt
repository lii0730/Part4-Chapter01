package com.example.aop_part4_chapter01

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout

class CustomMotionLayout (context: Context, attributeSet: AttributeSet) : MotionLayout(context, attributeSet){

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        Log.d("onInterceptTouchEvent", event.toString())
        return super.onInterceptTouchEvent(event)
        // 반환값이 True면 OnTouchEvent로 전달
        // false 면 dispatchTouchEvent() 호출
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("onTouchEvent", event.toString())

        // ACTION_MOVE -> 이동
        // ACTION_UP -> 마우스를 누르고 뗀 경우
        // ACTION_DOWN -> 마우스를 누른 경우
        return super.onTouchEvent(event)

        //반환값이 True면 이벤트를 더 이상 전달하지 않는다.
    }

}