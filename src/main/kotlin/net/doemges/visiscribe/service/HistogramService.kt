package net.doemges.visiscribe.service

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.opencv.global.opencv_core.*
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.*
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

@Service
class HistogramService {

    fun calculateHistogram(image: BufferedImage): Mat {
        val mat = bufferedImageToMat(image)
        return calculateHistogram(mat)
    }

    private fun bufferedImageToMat(bufferedImage: BufferedImage): Mat {
        val mat = Mat(bufferedImage.height, bufferedImage.width, CV_8UC3)
        val data = (bufferedImage.raster.dataBuffer as DataBufferByte).data
        mat
            .data()
            .put(data, 0, data.size) //The data now has the format CV_8UC3
        return mat
    }

    private fun calculateHistogram(image: Mat): Mat {
        val histSize = intArrayOf(256)
        val ranges = floatArrayOf(0f, 256f)

        return (0 until image.channels())
            .map { channelIndex ->
                val channelHist = Mat()
                calcHist(
                    image,
                    1,
                    IntBuffer.wrap(intArrayOf(channelIndex)),
                    null,
                    channelHist,
                    1,
                    IntBuffer.wrap(histSize),
                    FloatBuffer.wrap(ranges),
                    true,
                    false)
                normalize(channelHist, channelHist, 0.0, 1.0, NORM_MINMAX, -1, null)
                channelHist
            }
            .reduce { acc, mat ->
                acc.apply { push_back(mat) }
            }
    }
}
