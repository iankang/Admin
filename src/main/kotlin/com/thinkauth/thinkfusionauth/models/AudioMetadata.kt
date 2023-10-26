package com.thinkauth.thinkfusionauth.models

import java.nio.file.Path

data class AudioMetadata(
    var path:Path? = null,
    var size:Long? = null,
    var length:Long? = null,
    var originalName:String? = null,
    var thumbnail:Path? = null,
)
