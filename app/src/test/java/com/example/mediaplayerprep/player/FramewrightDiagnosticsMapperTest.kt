package com.example.mediaplayerprep.player

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.xheghun.analytics.CodecClassificationSource
import com.xheghun.analytics.CodecFormatSupport
import com.xheghun.analytics.CodecImplementationType
import com.xheghun.analytics.DecoderCapabilitySnapshot
import com.xheghun.analytics.DiagnosticEvent
import com.xheghun.analytics.DiagnosticEventMetadata
import com.xheghun.analytics.PlayerState
import com.xheghun.analytics.SessionSnapshot
import com.xheghun.analytics.SessionSummary
import com.xheghun.analytics.TrackType
import com.xheghun.framewright.bandwidth.BandwidthEstimateSnapshot
import com.xheghun.framewright.media3.PlaybackSnapshot
import org.junit.Test

class FramewrightDiagnosticsMapperTest {
    @Test
    fun `maps session summary bandwidth estimates and latest decoder details`() {
        val sessionId = "playback-session-123"
        val decoderInitialization = DiagnosticEvent.DecoderInit(
            metadata = metadata(sessionId, eventId = "decoder-event"),
            decoderName = "c2.android.avc.decoder",
            mimeType = "video/avc",
            trackType = TrackType.VIDEO,
            initializationDurationMs = 18L,
            isHardwareAccelerated = true,
            capabilities = DecoderCapabilitySnapshot(
                implementationType = CodecImplementationType.HARDWARE_ACCELERATED,
                classificationSource = CodecClassificationSource.PLATFORM,
                supportsAdaptivePlayback = true,
                supportsSecurePlayback = false,
                supportsTunneledPlayback = false,
                selectedFormatSupport = CodecFormatSupport.SUPPORTED
            )
        )
        val laterAudioDecoderInitialization = DiagnosticEvent.DecoderInit(
            metadata = metadata(sessionId, eventId = "audio-decoder-event"),
            decoderName = "c2.android.aac.decoder",
            mimeType = "audio/mp4a-latm",
            trackType = TrackType.AUDIO,
            initializationDurationMs = 7L,
            isHardwareAccelerated = null,
            capabilities = null
        )
        val playbackSnapshot = PlaybackSnapshot(
            session = SessionSnapshot(
                sessionId = sessionId,
                truncated = false,
                events = listOf(decoderInitialization, laterAudioDecoderInitialization)
            ),
            summary = SessionSummary(
                sessionId = sessionId,
                timeToFirstFrameMs = 420L,
                rebufferCount = 2,
                totalRebufferDurationMs = 750L,
                rebufferRatio = 0.025,
                startupFailed = false,
                trackSwitchesPerMinute = 1.5,
                averageDecoderInitializationMs = 18.0,
                droppedFrameCount = 4L
            ),
            playerState = PlayerState.READY,
            positionMs = 30_000L,
            bufferedDurationMs = 12_000L
        )
        val bandwidthEstimate = BandwidthEstimateSnapshot(
            customEstimateBps = 4_200_000L,
            fastEstimateBps = 4_800_000L,
            slowEstimateBps = 3_900_000L,
            defaultEstimateBps = 4_000_000L,
            confidence = 0.8,
            sessionSampleCount = 4
        )

        val diagnostics = FramewrightDiagnosticsMapper.map(playbackSnapshot, bandwidthEstimate)

        assertThat(diagnostics.sessionId).isEqualTo(sessionId)
        assertThat(diagnostics.diagnosticEventCount).isEqualTo(2)
        assertThat(diagnostics.timeToFirstFrameMs).isEqualTo(420L)
        assertThat(diagnostics.rebufferCount).isEqualTo(2)
        assertThat(diagnostics.totalRebufferDurationMs).isEqualTo(750L)
        assertThat(diagnostics.droppedFrameCount).isEqualTo(4L)
        assertThat(diagnostics.drivingBandwidthEstimateBps).isEqualTo(4_200_000L)
        assertThat(diagnostics.fastBandwidthEstimateBps).isEqualTo(4_800_000L)
        assertThat(diagnostics.slowBandwidthEstimateBps).isEqualTo(3_900_000L)
        assertThat(diagnostics.media3BandwidthEstimateBps).isEqualTo(4_000_000L)
        assertThat(diagnostics.bandwidthConfidence).isEqualTo(0.8)
        assertThat(diagnostics.bandwidthSampleCount).isEqualTo(4)
        assertThat(diagnostics.decoderName).isEqualTo("c2.android.avc.decoder")
        assertThat(diagnostics.decoderImplementationType).isEqualTo("HARDWARE_ACCELERATED")
        assertThat(diagnostics.selectedFormatSupport).isEqualTo("SUPPORTED")
    }

    @Test
    fun `keeps bandwidth estimates when playback session has not started`() {
        val initialBandwidthEstimate = BandwidthEstimateSnapshot(
            customEstimateBps = 1_000_000L,
            fastEstimateBps = 1_000_000L,
            slowEstimateBps = 1_000_000L,
            defaultEstimateBps = 1_000_000L,
            confidence = 0.0,
            sessionSampleCount = 0
        )

        val diagnostics = FramewrightDiagnosticsMapper.map(
            playbackSnapshot = null,
            bandwidthEstimate = initialBandwidthEstimate
        )

        assertThat(diagnostics.sessionId).isNull()
        assertThat(diagnostics.diagnosticEventCount).isEqualTo(0)
        assertThat(diagnostics.timeToFirstFrameMs).isNull()
        assertThat(diagnostics.drivingBandwidthEstimateBps).isEqualTo(1_000_000L)
        assertThat(diagnostics.decoderName).isNull()
    }

    private fun metadata(sessionId: String, eventId: String) = DiagnosticEventMetadata(
        sessionId = sessionId,
        eventId = eventId,
        timestampMs = 1_000L,
        elapsedRealtimeMs = 500L,
        playerState = PlayerState.READY
    )
}
