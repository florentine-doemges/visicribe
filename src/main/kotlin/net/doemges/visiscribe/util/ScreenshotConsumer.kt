package net.doemges.visiscribe.util

import com.github.kokorin.jaffree.ffmpeg.Frame
import com.github.kokorin.jaffree.ffmpeg.FrameConsumer
import com.github.kokorin.jaffree.ffmpeg.Stream
import net.doemges.visiscribe.model.Screenshot
import reactor.core.publisher.FluxSink
import java.util.concurrent.atomic.AtomicLong

class ScreenshotConsumer(
    private val frameCounter: AtomicLong,
    private val lastTimestamp: AtomicLong,
    private val sink: FluxSink<Screenshot>,
    private val videoFileName: String) : FrameConsumer {
    override fun consumeStreams(streams: MutableList<Stream>?) {
        // Not used but must be implemented
    }

    override fun consume(frame: Frame?) {
        frame?.let {
            val image = frame.image
            image?.let {
                val frameNumber = frameCounter.incrementAndGet()
                val screenshot = Screenshot(
                    bufferedImage = it,
                    timeMs = lastTimestamp.get(),
                    frameNumber = frameNumber,
                    width = it.width,
                    height = it.height,
                    videoFileName = videoFileName
                                           )
                sink.next(screenshot)
            }
        }
    }
}