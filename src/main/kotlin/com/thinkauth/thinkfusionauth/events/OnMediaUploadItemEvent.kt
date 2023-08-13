package com.thinkauth.thinkfusionauth.events

import org.springframework.context.ApplicationEvent
import org.springframework.data.rest.core.mapping.ResourceType
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

class OnMediaUploadItemEvent(
    val file: MultipartFile,
    val copyLocation: Path,
): ApplicationEvent(file) {
}