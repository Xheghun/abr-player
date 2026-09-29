package com.example.mediaplayerprep.player

import com.xheghun.analytics.DiagnosticEvent
import com.xheghun.analytics.TrackType
import com.xheghun.framewright.bandwidth.BandwidthEstimateSnapshot
import com.xheghun.framewright.media3.PlaybackSnapshot

internal object FramewrightDiagnosticsMapper {
    fun map(
        playbackSnapshot: PlaybackSnapshot?,
        bandwidthEstimate: BandwidthEstimateSnapshot
    ): FramewrightDiagnostics {
        val decoderInitializations = playbackSnapshot
            ?.session
            ?.events
            ?.filterIsInstance<DiagnosticEvent.DecoderInit>()
            .orEmpty()
        val latestDecoderInitialization =
            decoderInitializations.lastOrNull { it.trackType == TrackType.VIDEO }
                ?: decoderInitializations.lastOrNull()

        return FramewrightDiagnostics(
            sessionId = playbackSnapshot?.session?.sessionId,
            diagnosticEventCount = playbackSnapshot?.session?.events?.size ?: 0,
            timeToFirstFrameMs = playbackSnapshot?.summary?.timeToFirstFrameMs,
            rebufferCount = playbackSnapshot?.summary?.rebufferCount ?: 0,
            totalRebufferDurationMs = playbackSnapshot?.summary?.totalRebufferDurationMs ?: 0L,
            droppedFrameCount = playbackSnapshot?.summary?.droppedFrameCount ?: 0L,
            drivingBandwidthEstimateBps = bandwidthEstimate.customEstimateBps,
            fastBandwidthEstimateBps = bandwidthEstimate.fastEstimateBps,
            slowBandwidthEstimateBps = bandwidthEstimate.slowEstimateBps,
            media3BandwidthEstimateBps = bandwidthEstimate.defaultEstimateBps,
            bandwidthConfidence = bandwidthEstimate.confidence,
            bandwidthSampleCount = bandwidthEstimate.sessionSampleCount,
            decoderName = latestDecoderInitialization?.decoderName,
            decoderImplementationType = latestDecoderInitialization?.capabilities?.implementationType?.name,
            selectedFormatSupport = latestDecoderInitialization?.capabilities?.selectedFormatSupport?.name
        )
    }
}
