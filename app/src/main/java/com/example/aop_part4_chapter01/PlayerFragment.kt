package com.example.aop_part4_chapter01

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aop_part4_chapter01.DTO.VideoDTO
import com.example.aop_part4_chapter01.Service.VideoService
import com.example.aop_part4_chapter01.adapter.VideoAdapter
import com.example.aop_part4_chapter01.databinding.FragmentPlayerBinding
import com.example.aop_part4_chapter01.model.VideoModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var binding: FragmentPlayerBinding? = null
    private lateinit var videoAdapter: VideoAdapter
    private var player: SimpleExoPlayer? = null
    private lateinit var mediaItem: MediaItem
    private var isPlayed: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        videoAdapter = VideoAdapter(onItemClicked = { videoModel ->
            settingVideo(videoModel)
        })

        fragmentPlayerBinding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        fragmentPlayerBinding.bottomPlayerControlButton.setOnClickListener {
            if (!isPlayed) playVideo() else stopVideo()
        }

        fragmentPlayerBinding.playerMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding?.let {
                    (activity as MainActivity).also { mainActivity ->
                        mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress =
                            abs(progress)
                    }
                }
            }


            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })

        getVideoList()
    }

    fun settingVideo(videoModel: VideoModel) {
        if (player == null) {
            context?.let {
                player = SimpleExoPlayer.Builder(it).build()
            }
        }

        mediaItem = MediaItem.fromUri(videoModel.sources)
        binding?.let {
            it.bottomTitleTextView.text = videoModel.title
            it.playerView.player = player
            it.playerView.player?.setMediaItem(mediaItem)
            it.playerView.player?.prepare()
            playVideo()
        }
    }

    fun playVideo() {
        binding?.playerMotionLayout?.transitionToEnd()
        if (binding?.playerView?.player?.mediaItemCount!! > 0) {
            binding?.playerView?.player?.prepare()
            binding?.playerView?.player?.play()
        }
        isPlayed = true
        binding?.bottomPlayerControlButton?.setImageResource(
            R.drawable.ic_baseline_pause_24,
        )
    }

    fun stopVideo() {
        binding?.playerView?.player?.pause()
        isPlayed = false
        binding?.bottomPlayerControlButton?.setImageResource(
            R.drawable.ic_baseline_play_arrow_24,
        )
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.listVideos().enqueue(object : Callback<VideoDTO> {
                override fun onResponse(call: Call<VideoDTO>, response: Response<VideoDTO>) {
                    if (response.isSuccessful.not()) {
                        Log.d("MainActivity", "respones Fail")
                        return
                    }
                    response.body()?.let { videoDto ->
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

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release()
    }
}