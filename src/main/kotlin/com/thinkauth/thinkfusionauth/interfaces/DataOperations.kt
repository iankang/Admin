package com.thinkauth.thinkfusionauth.interfaces

import com.thinkauth.thinkfusionauth.models.responses.PagedResponse

interface DataOperations<T> {

    fun itemExistsById(id: String):Boolean
    fun findEverythingPaged(
        page:Int,
        size:Int
    ):PagedResponse<List<T>>

    fun createItem(item: T):T

    fun getItemById(id:String):T

    fun updateItem(id:String, item:T):T?

    fun deleteItemById(id:String)

    fun deleteAllItems()


}