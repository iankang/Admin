package com.thinkauth.thinkfusionauth.utils

import com.thinkauth.thinkfusionauth.models.responses.FileTypeEnum
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.util.StringUtils
import java.io.File
import java.util.*

@Configuration
class FileProcessingHelper {

    private val LOGGER: Logger = LoggerFactory.getLogger(FileProcessingHelper::class.java)
    fun mediaFullPath(resourceType: BucketName, name: String): String {
       return try {
            val extension = getExtensionFromResource(resourceType)
            val newFileName = generateUniqueFileName(extension)
            val copyLocation: String = when (resourceType) {
                BucketName.VOICE_COLLECTION -> {

                    BucketName.VOICE_COLLECTION.name + File.separator + StringUtils.cleanPath(
                        newFileName
                    )

                }

                BucketName.BUSINESS_PROFILE_PIC -> {

                    BucketName.BUSINESS_PROFILE_PIC.name + File.separator + StringUtils.cleanPath(
                        newFileName
                    )

                }

                BucketName.USER_ACCOUNT_PROFILE -> {

                    BucketName.USER_ACCOUNT_PROFILE.name + File.separator + StringUtils.cleanPath(
                        newFileName
                    )

                }

                BucketName.STT_UPLOAD -> {

                    BucketName.STT_UPLOAD.name + File.separator + StringUtils.cleanPath(
                        newFileName
                    )

                }

            }
            LOGGER.info("copy location: " + copyLocation)
            return copyLocation
        } catch (e: Exception) {
            println("error: " + e.message)
            LOGGER.error("error: " + e.message)
           return e.message?.toString()!!
        }

    }

    fun generateUniqueFileName(extension: String): String {
        var filename = ""
        val millis = System.currentTimeMillis()
        var datetime = Date().toGMTString()
        datetime = datetime.replace(" ", "")
        datetime = datetime.replace(":", "")
        val rndchars = RandomStringUtils.randomAlphanumeric(10)
        filename = rndchars + "_" + datetime + "_" + millis + "." + extension
        return filename
    }

    fun getExtensionFromResource(resource: BucketName): String {
        return when (resource) {
            BucketName.VOICE_COLLECTION -> {
                "mp3"
            }

            BucketName.BUSINESS_PROFILE_PIC -> {
                "jpg"
            }

            BucketName.USER_ACCOUNT_PROFILE -> {
                "jpg"
            }

            BucketName.STT_UPLOAD -> {
                "mp3"
            }
        }
    }

}