package com.thinkauth.thinkfusionauth.interfaces

import io.minio.ObjectWriteResponse
import io.minio.Result
import io.minio.messages.Bucket
import io.minio.messages.Item
import java.io.InputStream

interface StorageServiceInterface {
    fun upload( bucketName: String,
                objectName: String,
                filename: String,
                contentType: String): ObjectWriteResponse?

    fun uploadFile(
        bucketName: String,
        objectName: String,
        inputStream: InputStream
    ): ObjectWriteResponse?
    fun download(  bucketName: String,
                   objectName: String,
                   filename: String)
    fun stream(  bucketName: String,
                 objectName: String)
    fun createBucket(bucketName: String)
    fun listBuckets(): MutableList<Bucket>?
    fun listObjects(bucketName: String): MutableIterable<Result<Item>>?

    fun composeObject(bucketName: String,
                      objectName: String):ObjectWriteResponse?

}