package net.doemges.visiscribe.service

import net.doemges.visiscribe.model.Screenshot
import org.bytedeco.javacpp.FloatPointer
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.opencv.global.opencv_core.*
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.*
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage

@Service
class HistogramCalculationService {

    fun calculateHistogram(screenshot: Screenshot): Screenshot {
        val mat = bufferedImageToMat(screenshot.bufferedImage)
        val histogram = Mat()

        // Create histogram for grayscale image
        val grayMat = Mat()
        cvtColor(mat, grayMat, COLOR_BGR2GRAY)
        val histSize = IntPointer(256)
        val histRange = FloatPointer(0.0f, 256.0f)
        val channels = IntPointer(0)
        val matVector = MatVector(1)
        matVector.put(0, grayMat)
        calcHist(matVector, channels, Mat(), histogram, histSize, histRange)

        return screenshot.copy(combinedHistogram = histogram)
    }

    private fun bufferedImageToMat(image: BufferedImage): Mat {
        val mat = Mat(image.height, image.width, CV_8UC3)
        val data = (image.raster.dataBuffer as java.awt.image.DataBufferByte).data
        mat.data().put(data, 0, data.size)
        return mat
    }
}
