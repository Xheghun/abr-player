package com.example.mediaplayerprep.player

data class PlayerDiagnostics(
    val bitrate: Int? = null,
    val selectedVideoTrack: String = "unknown",
    val selectedAudioTrack: String = "unknown",
    val selectedTextTrack: String = "none",
    val customCodec: CustomCodecInfo? = null,
    val playbackPositionMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val playerState: String = "Idle",
    val framewright: FramewrightDiagnostics = FramewrightDiagnostics()
)

data class FramewrightDiagnostics(
    val sessionId: String? = null,
    val diagnosticEventCount: Int = 0,
    val timeToFirstFrameMs: Long? = null,
    val rebufferCount: Int = 0,
    val totalRebufferDurationMs: Long = 0L,
    val droppedFrameCount: Long = 0L,
    val drivingBandwidthEstimateBps: Long? = null,
    val fastBandwidthEstimateBps: Long? = null,
    val slowBandwidthEstimateBps: Long? = null,
    val media3BandwidthEstimateBps: Long? = null,
    val bandwidthConfidence: Double? = null,
    val bandwidthSampleCount: Int = 0,
    val decoderName: String? = null,
    val decoderImplementationType: String? = null,
    val selectedFormatSupport: String? = null
)
