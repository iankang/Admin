package com.thinkauth.thinkfusionauth.interfaces

import com.thinkauth.thinkfusionauth.config.TrackExecutionTime
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse

interface DataOperations<T> {
    @TrackExecutionTime
    fun itemExistsById(id: String):Boolean
    @TrackExecutionTime
    fun findEverythingPaged(
        page:Int,
        size:Int
    ):PagedResponse<List<T>>
    @TrackExecutionTime
    fun createItem(item: T):T
    @TrackExecutionTime
    fun getItemById(id:String):T
    @TrackExecutionTime
    fun updateItem(id:String, item:T):T?
    @TrackExecutionTime
    fun deleteItemById(id:String)
    @TrackExecutionTime
    fun deleteAllItems()

}