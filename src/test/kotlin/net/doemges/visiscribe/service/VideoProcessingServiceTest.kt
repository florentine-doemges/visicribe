package net.doemges.visiscribe.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class VideoProcessingServiceTest {

    @Autowired
    private lateinit var videoProcessingService: VideoProcessingService


    @Test
    fun testProcessVideo() {
        val video = java.io.File("src/test/resources/test.mp4")
        val interval = 42 // 24 times per second
        val processVideo = videoProcessingService.processVideo(video, interval)

        // Block and wait for the completion of the Flux
        processVideo
            .doOnNext { screenshot ->
                println(screenshot)
            }
            .blockLast() // This will block until the last item is emitted
    }
}