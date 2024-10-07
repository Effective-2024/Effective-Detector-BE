package com.effective.detector.image.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import com.effective.detector.common.error.BusinessError
import com.effective.detector.common.error.BusinessException
import com.effective.detector.common.util.findByIdOrThrow
import com.effective.detector.common.util.logError
import com.effective.detector.common.util.logInfo
import com.effective.detector.hospital.domain.*
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.nurigo.java_sdk.exceptions.CoolsmsException
import org.jcodec.api.SequenceEncoder
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.ColorSpace
import org.jcodec.common.model.Picture
import org.jcodec.common.model.Rational
import org.jcodec.scale.AWTUtil
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO


@Service
class ImageService(
    private val rabbitTemplate: RabbitTemplate,
    private val amazonS3Client: AmazonS3,
    private val hospitalRepository: HospitalRepository,
    private val cameraRepository: CameraRepository,
    private val accidentRepository: AccidentRepository,
    private val messagingTemplate: SimpMessagingTemplate,
    private val coolSmsSender: net.nurigo.java_sdk.api.Message,
) {

    companion object {
        const val S3_BUCKET = "effective-bucket"
    }

    @Value("\${rabbitmq.ttl}")
    private val ttl: String? = null

    @Value("\${video-prefix}")
    private val videoPrefix: String? = null

    @Value("\${coolsms.sender-number}")
    private val senderNumber: String? = null

    fun sendMessage(byteArray: ByteArray, queueName: String) {
        logInfo("send to rabbitMq image file")
        try {
            rabbitTemplate.convertAndSend("", queueName, ImageMessageDto(byteArray)) { message: Message ->
                message.messageProperties.expiration = ttl
                message
            }
        } catch (exception: Exception) {
            logError(exception.toString(), exception)
            throw BusinessException(BusinessError.RABBITMQ_CONNECTION_ERROR)
        }
    }

    fun accident(queueName: String, hospitalId: Long, cameraId: Long) {
        try {
            val timeInfo = convertImageToVideo(queueName) ?: return // 30초 내의 이미지들을 영상으로 변환
            val camera = cameraRepository.findByIdOrThrow(cameraId)
            val videoUrl = uploadToStorage(hospitalId, camera, timeInfo) // 영상을 S3에 저장하고 DB 테이블에 정보들 저장.
            //sendVideoMessageToUser(hospitalId, camera, videoUrl) // 사고 발생 영상을 문자로 전송. TODO: 시연시 활성화.
            sendClientMessage(hospitalId, camera) // 클라이언트로 사고 발생 알림 보내기
        } catch (exception: Exception) {
            logError("RabbitMqMessage Handling Error", exception)
        }
    }

    private fun convertImageToVideo(queueName: String): Pair<LocalDateTime?, LocalDateTime?>? {
        val imageList = mutableListOf<ByteArray>()
        var startTime: LocalDateTime? = null
        var endTime: LocalDateTime? = null
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME // 기본 ISO 형식 포맷터
        var message = rabbitTemplate.receive(queueName)
        while (message != null) {
            val imageMessageDto = rabbitTemplate.messageConverter.fromMessage(message) as ImageMessageDto
            if (startTime == null) {
                startTime = LocalDateTime.parse(imageMessageDto.time, formatter)
            }
            endTime = LocalDateTime.parse(imageMessageDto.time, formatter)
            imageList.add(imageMessageDto.bytes)
            message = rabbitTemplate.receive(queueName)
        }

        if (imageList.isNotEmpty()) {
            logInfo("Converting images to video...")
            val currentDir = System.getProperty("user.dir")
            val outputFilePath = "$currentDir/video.mp4"
            createMp4(imageList, outputFilePath)
            return Pair(startTime, endTime)
        } else {
            logInfo("No images to process.")
        }
        return null
    }

    private fun createMp4(imageList: List<ByteArray>, outputFilePath: String) {
        val out = NIOUtils.writableFileChannel(outputFilePath)
        val encoder = SequenceEncoder.createWithFps(out, Rational.R(10, 1))  // FPS 설정

        for (imageBytes in imageList) {
            val img = ImageIO.read(ByteArrayInputStream(imageBytes))
            encoder.encodeNativeFrame(bufferedImageToPicture(img))
        }

        encoder.finish()
        out.close()
    }

    private fun bufferedImageToPicture(image: BufferedImage): Picture {
        return AWTUtil.fromBufferedImage(image, ColorSpace.RGB)
    }

    private fun uploadToStorage(
        hospitalId: Long,
        camera: Camera,
        timeInfo: Pair<LocalDateTime?, LocalDateTime?>,
    ): String {
        val videoUrl = "$hospitalId-${camera.id}-${LocalDateTime.now()}.mp4"
        uploadVideo(videoUrl)
        saveVideoInfo(videoUrl, hospitalId, camera, timeInfo.first!!, timeInfo.second!!)
        return videoPrefix + videoUrl
    }

    private fun uploadVideo(videoUrl: String) {
        val currentDir = System.getProperty("user.dir")
        val filePath = "$currentDir/video.mp4"
        val file = File(filePath)

        amazonS3Client.putObject(
            PutObjectRequest(
                S3_BUCKET,
                videoUrl,
                file
            )
        )
        file.delete()
        logInfo("File Uploaded Successfully To S3!")
    }

    private fun saveVideoInfo(
        videoUrl: String,
        hospitalId: Long,
        camera: Camera,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ) {
        hospitalRepository.findByIdOrThrow(hospitalId) // 존재하는 병원인지 유효성 체크.
        accidentRepository.save(
            Accident(
                videoUrl = videoUrl,
                startTime = startTime,
                endTime = endTime,
                camera = camera
            )
        )
    }

    private fun sendVideoMessageToUser(hospitalId: Long, camera: Camera, videoUrl: String) {
        val hospital = hospitalRepository.findByIdOrThrow(hospitalId)

        val params = HashMap<String, String>()
        hospital.memberHospitals.map { it.member }.forEach {
            params["to"] = senderNumber!!
            params["from"] = it.tel!!
            params["type"] = "LMS"
            params["text"] = "${hospital.name}병원의 ${camera.name}번 카메라에서 낙상사고가 감지되었습니다. 영상을 확인해주세요. 영상 링크 : $videoUrl"
            params["app_version"] = "test app 1.2"

            try {
                coolSmsSender.send(params)
                logInfo("SMS Sent Successfully To ${it.tel}")
            } catch (e: CoolsmsException) {
                logError(e.message, e)
            }
        }
    }

    private fun sendClientMessage(hospitalId: Long, camera: Camera) {
        val topicPath = "/ws/topic/accident/hospitals/$hospitalId"
        logInfo("Send WebSocket STOMP Message To Path: $topicPath")
        messagingTemplate.convertAndSend(
            topicPath,
            AccidentMessageDto(
                hospitalId = hospitalId,
                camera = CameraMessageDto(
                    id = camera.id!!,
                    content = camera.name
                ),
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now()
            )
        )
    }
}

data class ImageMessageDto @JsonCreator constructor(
    @JsonProperty("bytes") val bytes: ByteArray,
    @JsonProperty("time") val time: String = LocalDateTime.now().toString(),
)

data class AccidentMessageDto @JsonCreator constructor(
    @JsonProperty("hospitalId") val hospitalId: Long,
    @JsonProperty("camera") val camera: CameraMessageDto,
    @JsonProperty("startTime") val startTime: LocalDateTime,
    @JsonProperty("endTime") val endTime: LocalDateTime,
)

data class CameraMessageDto @JsonCreator constructor(
    @JsonProperty("id") val id: Long,
    @JsonProperty("content") val content: String,
)
