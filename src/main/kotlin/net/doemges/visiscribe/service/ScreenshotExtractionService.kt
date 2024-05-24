package net.doemges.visiscribe.service

import com.github.kokorin.jaffree.StreamType
import com.github.kokorin.jaffree.ffmpeg.*
import kotlinx.coroutines.*
import net.doemges.visiscribe.model.Screenshot
import net.doemges.visiscribe.util.ScreenshotConsumer
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.File
import java.util.concurrent.atomic.AtomicLong

@Service
class ScreenshotExtractionService {

    fun extractScreenshots(video: File, msInterval: Int): Flux<Screenshot> = Flux.create { sink ->
        val scope = CoroutineScope(Dispatchers.IO)
        val lastFrameTime = AtomicLong(0)
        val intervalMs = msInterval.toLong()

        val videoFileName = video.name
        val frameCounter = AtomicLong(0)

        val lastTimestamp = AtomicLong(0)

        val ffmpeg = FFmpeg
            .atPath()
            .addInput(UrlInput.fromPath(video.toPath()))
            .addOutput(
                FrameOutput
                    .withConsumer(ScreenshotConsumer(frameCounter, lastTimestamp, sink, videoFileName))
                    .disableStream(StreamType.AUDIO))
            .setProgressListener { progress ->
                val currentTime = progress.timeMillis
                val nextFrameTime = lastFrameTime.get()
                if (currentTime >= nextFrameTime) {
                    lastFrameTime.addAndGet(intervalMs)
                    lastTimestamp.set(currentTime)
                }
            }

        val ffmpegJob = scope.launch {
            try {
                ffmpeg.execute()
            } catch (e: Exception) {
                if (!sink.isCancelled) {
                    sink.error(e)
                }
            } finally {
                if (!sink.isCancelled) {
                    sink.complete()
                }
            }
        }

        sink.onCancel {
            ffmpegJob.cancel()
        }
    }
}

