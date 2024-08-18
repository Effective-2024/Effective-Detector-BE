package com.effective.detector.stream.api

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StreamController(
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    @MessageMapping("/save/hospitals/{hospitalId}")
    fun receiveImage(@PathVariable hospitalId: String, @Payload imageMessage: ImageMessage) {
        //val imageData = Base64.getDecoder().decode(imageMessage)
        //println("Received image data: ${imageMessage.hospitalId} bytes")
        simpMessagingTemplate.convertAndSend("/hospitals/$hospitalId", imageMessage)
    }
}

data class ImageMessage(
    val hospitalId: String,
    val imageData: String,
)
