package com.example.mediaplayerprep.player

import androidx.media3.exoplayer.ExoPlayer

data class PlayerSnapshot(
    val player: ExoPlayer? = null,
    val status: PlaybackStatus = PlaybackStatus.Idle,
    val isMuted: Boolean = false,
    val isPlaying: Boolean = false,
    val playbackSpeed: Float = 1f,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val userMessage: String? = null,
    val technicalError: String? = null,
    val diagnostics: PlayerDiagnostics = PlayerDiagnostics(),
    val tuning: PlaybackTuning = PlaybackTuning.Balanced,
    val qualityOptions: List<QualityOption> = emptyList(),
    val manualQualityLabel: String? = null
)
