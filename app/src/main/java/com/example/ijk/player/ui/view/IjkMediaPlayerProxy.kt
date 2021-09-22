package com.example.ijk.player.ui.view

import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * @author YangJ 视频播放行为接口实现类
 */
class IjkMediaPlayerProxy(private val player: SimpleExoPlayer) : IjkMediaPlayerI {

    override fun start() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun seekTo(value: Long) {
        player.seekTo(value)
    }

    override fun seekToByGestureDetector(value: Long, isPlayer: Boolean) {
        player.seekTo(value)
        if (isPlayer) {
            player.play()
        }
    }

    override fun speed(value: Float) {
        player.setPlaybackSpeed(value)
    }

    override fun getDataSource(): String {
        return "player.dataSource"
    }

    override fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    override fun getDuration(): Long {
        return player.duration
    }
}