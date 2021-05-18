package com.example.aop_part4_chapter01

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aop_part4_chapter01.DTO.VideoDTO
import com.example.aop_part4_chapter01.Service.VideoService
import com.example.aop_part4_chapter01.adapter.VideoAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var videoAdapter : VideoAdapter
    private val mainRecyclerView : RecyclerView by lazy {
        findViewById(R.id.mainRecyclerView)
    }
    private val mainMotionLayout : MotionLayout by lazy {
        findViewById(R.id.mainMotionLayout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlayerFragment())
            .commit()

        videoAdapter = VideoAdapter()
        mainRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        getVideoList()

    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.listVideos().enqueue(object: Callback<VideoDTO>{
                override fun onResponse(call: Call<VideoDTO>, response: Response<VideoDTO>) {
                    if(response.isSuccessful.not()) {
                        Log.d("MainActivity", "respones Fail")
                        return
                    }
                    response.body()?.let { videoDto->
                        Log.d("MainActivity", it.toString())
                        videoAdapter.submitList(videoDto.videos)
                    }
                }

                override fun onFailure(call: Call<VideoDTO>, t: Throwable) {
                    //TODO: 예외처리
                }
            })
        }
    }
}