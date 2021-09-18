package com.example.ijk.player

import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * @author YangJ 视频播放行为接口实现类
 */
class IjkMediaPlayerProxy(private val player: SimpleExoPlayer) : IjkMediaPlayerI {

    override fun start() {
        // player.onResume()
    }

    override fun pause() {
        // player.onPause()
    }

    override fun seekTo(value: Long) {
        player.seekTo(value)
    }

    override fun seekToByGestureDetector(value: Long) {

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