package com.example.aop_part4_chapter01.Service

import com.example.aop_part4_chapter01.DTO.VideoDTO
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {

    @GET("/v3/96a8836d-156a-4e0f-b825-3194413d0be1")
    fun listVideos() : Call<VideoDTO>
}