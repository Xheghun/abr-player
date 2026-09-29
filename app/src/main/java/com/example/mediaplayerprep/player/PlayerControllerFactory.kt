package com.example.mediaplayerprep.player

import android.content.Context

class PlayerControllerFactory(
    private val context: Context,
    private val sharedMutedState: SharedMutedState = SharedMutedState(),
    private val defaultTuning: PlaybackTuning = PlaybackTuning.Balanced
) {
    fun create(): PlayerController = ExoPlayerController(context, sharedMutedState, defaultTuning)
}
