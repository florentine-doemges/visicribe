package net.doemges.visiscribe.service

import net.doemges.visiscribe.model.Screenshot
import org.bytedeco.opencv.opencv_core.Mat
import org.springframework.stereotype.Service
import java.nio.FloatBuffer
import kotlin.math.abs

@Service
class FrameAnalysisService {

    fun calculateJSDivergence(hist1: Mat, hist2: Mat): Double {
        val p = hist1.toFloatArray()
        val q = hist2.toFloatArray()
        val m = FloatArray(p.size) { (p[it] + q[it]) / 2 }
        return (kullbackLeiblerDivergence(p, m) + kullbackLeiblerDivergence(q, m)) / 2
    }

    fun filterFramesWithStableJSDivergence(frames: List<Screenshot>): List<Screenshot> {
        if (frames.isEmpty()) return emptyList()

        val filteredFrames = mutableListOf(frames.first())
        val targetJS = calculateAverageJSDivergence(frames)
        var previousFrame = frames.first()

        frames.drop(1).forEach { currentFrame ->
            val jsDivergence = calculateJSDivergence(previousFrame.histogram!!, currentFrame.histogram!!)
            if (abs(jsDivergence - targetJS) < targetJS * 0.1) {
                filteredFrames.add(currentFrame)
                previousFrame = currentFrame
            }
        }
        return filteredFrames
    }

    private fun Mat.toFloatArray(): FloatArray {
        val floatArray = FloatArray(this.total().toInt())
        val byteBuffer = this.data().asByteBuffer()
        val buffer = byteBuffer.asFloatBuffer()
        buffer.get(floatArray)
        return floatArray
    }

    private fun kullbackLeiblerDivergence(p: FloatArray, q: FloatArray): Double {
        return p.indices.sumByDouble { i ->
            if (p[i] != 0f) p[i] * Math.log(p[i] / q[i].toDouble()) else 0.0
        } / Math.log(2.0) // Convert to bits
    }

    fun calculateAverageJSDivergence(screenshots: List<Screenshot>): Double {
        return screenshots.zipWithNext { a, b ->
            calculateJSDivergence(a.histogram!!, b.histogram!!)
        }.average()
    }
}
