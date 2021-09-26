package com.example.ijk.player.ui.view

/**
 * @author YangJ 视频播放行为接口
 */
interface IjkMediaPlayerI {

    /**
     * 开始播放
     */
    fun start()

    /**
     * 暂停播放
     */
    fun pause()

    /**
     * 移动到视频指定位置
     */
    fun seekToByGestureDetector(value: Long, isPlayer: Boolean)

    /**
     * 倍速播放
     */
    fun speed(value: Float)

    /**
     * 获取视频源
     */
    fun getDataSource(): String?

    /**
     * 获取视频当前播放位置
     */
    fun getCurrentPosition(): Long

    /**
     * 获取视频播放时长
     */
    fun getDuration(): Long
}