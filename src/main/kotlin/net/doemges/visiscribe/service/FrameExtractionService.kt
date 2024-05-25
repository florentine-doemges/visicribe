package net.doemges.visiscribe.service

import com.github.kokorin.jaffree.ffmpeg.FFmpeg
import com.github.kokorin.jaffree.ffmpeg.Frame
import com.github.kokorin.jaffree.ffmpeg.FrameConsumer
import com.github.kokorin.jaffree.ffmpeg.FrameOutput
import com.github.kokorin.jaffree.ffmpeg.Stream
import com.github.kokorin.jaffree.ffmpeg.UrlInput
import net.doemges.visiscribe.model.Screenshot
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink
import java.awt.image.BufferedImage
import java.io.File

@Service
class FrameExtractionService {

    fun extractFrames(videoFile: File, intervalMs: Int): Flux<Screenshot> = Flux.generate { sink ->
        FFmpeg
            .atPath()
            .addInput(UrlInput.fromPath(videoFile.toPath()))
            .addOutput(
                FrameOutput
                    .withConsumer(ExtractionFrameConsumer(videoFile, intervalMs, sink))
                    .setFrameRate(1.0 / (intervalMs / 1000.0)))
            .execute()
        sink.complete()
    }


}

class ExtractionFrameConsumer(
    private val videoFile: File,
    private val intervalMs: Int,
    private val sink: SynchronousSink<Screenshot>) : FrameConsumer {
    private val streamsMap: MutableMap<Int, Stream> = mutableMapOf()
    override fun consumeStreams(streams: MutableList<Stream>?) {
        streams?.forEach { stream ->
            streamsMap[stream.id] = stream
        }
    }

    override fun consume(frame: Frame?) {
        frame?.let {
            sink.next(createScreenshot(it.image, it.pts * streamsMap[it.streamId]!!.timebase, videoFile, intervalMs))
        }
    }

    private fun createScreenshot(
        bufferedImage: BufferedImage,
        timeMillis: Long,
        videoFile: File,
        intervalMs: Int): Screenshot = Screenshot(
        bufferedImage = bufferedImage,
        timeMs = timeMillis,
        frameNumber = timeMillis / intervalMs,
        width = bufferedImage.width,
        height = bufferedImage.height,
        videoFileName = videoFile.name
                                                 )

}
