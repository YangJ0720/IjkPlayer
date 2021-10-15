package com.example.ijk.player.ui.view

import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * @author YangJ 视频播放行为接口实现类
 */
class IjkMediaPlayerProxy(private val player: SimpleExoPlayer, private val isFloating: Boolean) :
    IjkMediaPlayerI {

    override fun start() {
        player.play()
    }

    override fun pause() {
        player.pause()
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

    override fun getTitle(): String? {
        return null
    }

    override fun getDataSource(): String? {
        return player.currentMediaItem?.playbackProperties?.uri?.path
    }

    override fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    override fun getDuration(): Long {
        return player.duration
    }

    override fun isFloating(): Boolean {
        return isFloating
    }
}