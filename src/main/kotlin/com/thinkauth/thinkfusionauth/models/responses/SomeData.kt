package com.thinkauth.thinkfusionauth.models.responses

import kotlin.reflect.jvm.internal.impl.metadata.jvm.JvmProtoBuf.StringTableTypes.Record

data class SomeData(
    val allowed:String,
    val authenticated:Boolean,
    val authorities:String
)
