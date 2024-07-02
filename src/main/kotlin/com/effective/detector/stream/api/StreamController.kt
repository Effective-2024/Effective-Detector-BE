package com.effective.detector.stream.api

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Base64


@Controller
class StreamController(
    private val images: MutableList<ByteArray> = ArrayList(), // 큐로 바꾸자?
    // 최근 10초간의 이미지를 저장하는 큐 도입 -> 최근꺼 조회하기.
    private val s3Client: AmazonS3,
) {

    @SubscribeMapping("/topic/image-stream/{cameraId}")
    fun receiveImage(@Payload imageBase64: String) {
        val image: ByteArray = Base64.getDecoder().decode(imageBase64)
        images.add(image)
    }

    @Scheduled(fixedRate = 600000) // 10분마다 실행 -> 30분?
    // 2020-12-01-00:00:00~00:30:00.mp3 이렇게 저장해서 시간대별로 조회가 가능하게끔?
    @Throws(IOException::class, InterruptedException::class)
    fun createVideo() {
        if (images.isEmpty()) return

        // 이미지 저장
        for (i in images.indices) {
            FileOutputStream("image_$i.jpg").use { fos ->
                fos.write(images[i])
            }
        }

        // FFmpeg로 영상 생성
        val pb = ProcessBuilder("ffmpeg", "-framerate", "1", "-i", "image_%d.jpg", "-c:v", "libx264", "output.mp4")
        pb.inheritIO().start().waitFor()

        // 영상 파일을 S3에 업로드
        val videoFile = File("output.mp4")
        PutObjectRequest("your-bucket-name", "output.mp4", videoFile).let {
            s3Client.putObject(it)
        }

        // 임시 파일 삭제
        for (i in images.indices) {
            File("image_$i.jpg").delete()
        }
        videoFile.delete()

        // 이미지 리스트 초기화
        images.clear()
    }
}
